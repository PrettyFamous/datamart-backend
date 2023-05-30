package su.vistar.datamart.serviceimpl;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import su.vistar.datamart.entity.Dimension;
import su.vistar.datamart.entity.DimensionAttr;
import su.vistar.datamart.entity.Type;
import su.vistar.datamart.exception.ResourceAlreadyExistsException;
import su.vistar.datamart.exception.ResourceNotFoundException;
import su.vistar.datamart.model.DimensionAttrModel;
import su.vistar.datamart.repository.DimensionAttrRepository;
import su.vistar.datamart.repository.DimensionRepository;
import su.vistar.datamart.repository.TypeRepository;
import su.vistar.datamart.service.DimensionAttrService;

@Service
@AllArgsConstructor
public class DimensionAttrServiceImpl implements DimensionAttrService {
    private final DimensionAttrRepository dimensionAttrRepository;
    private final DimensionRepository dimensionRepository;
    private final TypeRepository typeRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public DimensionAttr getDimensionAttrById(Long id) {
        return dimensionAttrRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Fact Attr\" with id=" + id + " does not exist"
                ));
    }

    @Override
    public Iterable<DimensionAttr> getDimensionAttrs() {
        return dimensionAttrRepository.findAll();
    }

    @Override
    public DimensionAttr addDimensionAttr(DimensionAttrModel dimensionAttrModel) {
        Type type = typeRepository
                .findById(dimensionAttrModel.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Type\" with id=" + dimensionAttrModel.getTypeId() + " does not exist."));
        Dimension dimension = dimensionRepository
                .findById(dimensionAttrModel.getDimensionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Dimension\" with id=" + dimensionAttrModel.getDimensionId() + " does not exist."));

        if (!dimensionAttrRepository.existsByNameAndDimension( dimensionAttrModel.getName(), dimensionRepository.findById(dimensionAttrModel.getDimensionId()) )) {
            throw new ResourceAlreadyExistsException("DimensionAttr with such name does not exists in this dimension.");
        }

        String query = "ALTER TABLE " + dimensionRepository.findById(dimensionAttrModel.getDimensionId()).get().getSystemName()
                + " ADD COLUMN " + dimensionAttrModel.getName() + " " + typeRepository.findById(dimensionAttrModel.getTypeId()).get().getName()
                + ";";
        jdbcTemplate.execute(query);


        DimensionAttr dimensionAttr = new DimensionAttr(
                dimensionAttrModel.getName(),
                type,
                dimension
        );
        dimensionAttrRepository.save(dimensionAttr);

        return dimensionAttr;
    }

    @Override
    public DimensionAttr updateDimensionAttr(DimensionAttrModel dimensionAttrModel) {
        Type type = typeRepository
                .findById(dimensionAttrModel.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Type\" with id=" + dimensionAttrModel.getTypeId() + " does not exist."));
        Dimension dimension = dimensionRepository
                .findById(dimensionAttrModel.getDimensionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Dimension\" with id=" + dimensionAttrModel.getDimensionId() + " does not exist."));

        DimensionAttr dimensionAttr = dimensionAttrRepository
                .findById(dimensionAttrModel.getId())
                .orElseThrow(() -> new ResourceAlreadyExistsException("DimensionAttr with such name already exists in this dimension."));

        dimensionAttr.setName(dimensionAttrModel.getName());
        dimensionAttr.setType(type);
        dimensionAttr.setDimension(dimension);
        dimensionAttrRepository.save(dimensionAttr);

        return dimensionAttr;
    }

    @Override
    public void deleteById(Long id) {
        try {
            String query = "ALTER TABLE " + dimensionRepository.findById(dimensionAttrRepository.findById(id).get().getDimension().getId()).get().getSystemName()
                    + " DROP COLUMN " + dimensionAttrRepository.findById(id).get().getName() + ";";
            jdbcTemplate.execute(query);
            dimensionAttrRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("DimensionAttr with id " + id + " does not exist.", e);
        }
    }
}
