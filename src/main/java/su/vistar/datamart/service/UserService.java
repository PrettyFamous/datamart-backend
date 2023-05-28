package su.vistar.datamart.service;


import su.vistar.datamart.entity.User;
import su.vistar.datamart.model.UserModel;

public interface UserService {
    User getUserById(Long id);

    Iterable<User> getUsers();

    User addUser(UserModel userModel);

    User updateUser(UserModel userModel);

    void deleteById(Long id);
}
