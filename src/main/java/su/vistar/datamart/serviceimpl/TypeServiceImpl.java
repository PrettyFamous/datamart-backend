package su.vistar.datamart.serviceimpl;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import su.vistar.datamart.entity.Type;
import su.vistar.datamart.exception.ResourceAlreadyExistsException;
import su.vistar.datamart.exception.ResourceNotFoundException;
import su.vistar.datamart.model.TypeModel;
import su.vistar.datamart.repository.TypeRepository;
import su.vistar.datamart.service.TypeService;

@Service
@AllArgsConstructor
public class TypeServiceImpl implements TypeService {
    private final TypeRepository typeRepository;

    @Override
    public Type getTypeById(Long id) {
        return typeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Type\" with id=" + id + " does not exist"
                ));
    }

    @Override
    public Iterable<Type> getTypes() {
        return typeRepository.findAll();
    }

    @Override
    public Type addType(TypeModel typeModel) {

        if (typeRepository.existsByName( typeModel.getName() )) {
            throw new ResourceAlreadyExistsException("The Type with such name already exists.");
        }
        Type type = new Type( typeModel.getName() );
        typeRepository.save(type);

        return type;
    }

    @Override
    public Type updateType(TypeModel typeModel) {
        Type type = typeRepository
                .findById(typeModel.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Type\" with id=" + typeModel.getName() + " does not exist."));

        type.setName(typeModel.getName());
        typeRepository.save(type);

        return type;
    }

    @Override
    public void deleteById(Long id) {
        try {
            typeRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Type with id " + id + " does not exist.", e);
        }
    }
}
