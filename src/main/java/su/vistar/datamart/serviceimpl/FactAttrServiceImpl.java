package su.vistar.datamart.serviceimpl;

import com.ibm.icu.text.Transliterator;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import su.vistar.datamart.entity.Fact;
import su.vistar.datamart.entity.FactAttr;
import su.vistar.datamart.entity.Type;
import su.vistar.datamart.exception.ResourceAlreadyExistsException;
import su.vistar.datamart.exception.ResourceNotFoundException;
import su.vistar.datamart.model.FactAttrModel;
import su.vistar.datamart.repository.FactAttrRepository;
import su.vistar.datamart.repository.FactRepository;
import su.vistar.datamart.repository.TypeRepository;
import su.vistar.datamart.service.FactAttrService;

@Service
@AllArgsConstructor
public class FactAttrServiceImpl implements FactAttrService {
    private final Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");
    private final FactAttrRepository factAttrRepository;
    private final FactRepository factRepository;
    private final TypeRepository typeRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public FactAttr getFactAttrById(Long id) {
        return factAttrRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"FactAttr\" with id=" + id + " does not exist"
                ));
    }

    @Override
    public Iterable<FactAttr> getFactAttrs() {
        return factAttrRepository.findAll();
    }

    @Override
    public FactAttr addFactAttr(FactAttrModel factAttrModel) {
        Type type = typeRepository
                .findById(factAttrModel.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Type\" with id=" + factAttrModel.getTypeId() + " does not exist."));

        System.out.println(factAttrModel.getFactId());
        Fact fact = factRepository
                .findById(factAttrModel.getFactId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Fact\" with id=" + factAttrModel.getFactId() + " does not exist."));

        if (factAttrRepository.existsByNameAndFact( factAttrModel.getName(), factRepository.findById(factAttrModel.getFactId()) )) {
            throw new ResourceAlreadyExistsException("FactAttr with such name already exists in this fact.");
        }

        String systemName = toLatinTrans.transliterate(factAttrModel.getName()).replaceAll(" ", "_");
        systemName += "_" + factRepository.getUniqueVal();

        String query = "ALTER TABLE " + factRepository.findById(factAttrModel.getFactId()).get().getSystemName()
                + " ADD COLUMN " + systemName + " " + typeRepository.findById(factAttrModel.getTypeId()).get().getName()
                + ";";
        jdbcTemplate.execute(query);

        FactAttr factAttr = new FactAttr(
                factAttrModel.getName(),
                systemName,
                type,
                fact
        );
        factAttrRepository.save(factAttr);

        return factAttr;
    }

    @Override
    public FactAttr updateFactAttr(FactAttrModel factAttrModel) {
        Type type = typeRepository
                .findById(factAttrModel.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Type\" with id=" + factAttrModel.getTypeId() + " does not exist."));
        Fact fact = factRepository
                .findById(factAttrModel.getFactId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Fact\" with id=" + factAttrModel.getFactId() + " does not exist."));

        FactAttr factAttr = factAttrRepository
                .findById(factAttrModel.getId())
                .orElseThrow(() -> new ResourceAlreadyExistsException("FactAttr with such name does not exists in this fact."));

        String systemName = toLatinTrans.transliterate(factAttrModel.getName()).replaceAll(" ", "_");
        systemName += "_" + factRepository.getUniqueVal();

        factAttr.setFact(fact);
        factAttr.setName(factAttrModel.getName());
        factAttr.setSystemName(systemName);
        factAttr.setType(type);
        factAttrRepository.save(factAttr);

        return factAttr;
    }

    @Override
    public void deleteById(Long id) {
        try {
            String query = "ALTER TABLE " + factRepository.findById(factAttrRepository.findById(id).get().getFact().getId()).get().getSystemName()
                    + " DROP COLUMN " + factAttrRepository.findById(id).get().getSystemName() + ";";
            jdbcTemplate.execute(query);
            factAttrRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("FactAttr with id " + id + " does not exist.", e);
        }
    }
}
