package me.largetimmo.aedesmanagement.service;

import me.largetimmo.aedesmanagement.dto.UserDTO;
import me.largetimmo.aedesmanagement.entity.Role;
import me.largetimmo.aedesmanagement.entity.User;
import me.largetimmo.aedesmanagement.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserManager {

    private UserRepository userRepository;

    private EntityManager entityManager;

    public UserManager(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    public void create(UserDTO user){
        userRepository.save(new User(user.getUsername(),user.getPassword(),entityManager.getReference(Role.class,user.getRole())));
    }

    public void deleteByUsername(String username){
        userRepository.deleteById(username);
    }

    public List<UserDTO> getAll(){
        List<User> users =  userRepository.findAll();
        return users.stream().map(this::map).collect(Collectors.toList());
    }

    public void changePassword(String username,String password){
        userRepository.updatePasswordForUser(username,password);
    }

    public void changeUserRole(String username, String roleName){
        userRepository.updateUserRole(username,roleName);
    }

    private UserDTO map(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole().getName());
        return userDTO;
    }


}
