package su.vistar.datamart.serviceimpl;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import su.vistar.datamart.entity.User;
import su.vistar.datamart.exception.ResourceAlreadyExistsException;
import su.vistar.datamart.exception.ResourceNotFoundException;
import su.vistar.datamart.model.UserModel;
import su.vistar.datamart.repository.UserRepository;
import su.vistar.datamart.service.UserService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"User\" with id=" + id + " does not exist"
                ));
    }

    @Override
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(UserModel userModel) {
        if (userRepository.existsByLogin( userModel.getLogin() )) {
            throw new ResourceAlreadyExistsException("User with such login already exists.");
        }
        User user = new User(
                userModel.getFullName(),
                userModel.getLogin(),
                userModel.getPassword()
        );
        userRepository.save(user);

        return user;
    }

    @Override
    public User updateUser(UserModel userModel) {
        User user = userRepository
                .findById(userModel.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"User\" with id=" + userModel.getId() + " does not exist."));

        user.setFullName(userModel.getFullName());
        user.setLogin(userModel.getLogin());
        user.setPassword(userModel.getPassword());
        userRepository.save(user);

        return user;
    }

    @Override
    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("User with id " + id + " does not exist.", e);
        }
    }
}

