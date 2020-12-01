package me.largetimmo.aedesmanagement.service;

import lombok.extern.slf4j.Slf4j;
import me.largetimmo.aedesmanagement.dto.UserDTO;
import me.largetimmo.aedesmanagement.entity.Role;
import me.largetimmo.aedesmanagement.entity.User;
import me.largetimmo.aedesmanagement.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserManager {

    private UserRepository userRepository;

    private EntityManager entityManager;

    private Random random;

    public UserManager(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.random = new Random();
    }

    public void create(UserDTO user) {
        userRepository.save(new User(user.getUsername(), user.getPassword(), entityManager.getReference(Role.class, user.getRole()), 0));
    }

    public void deleteByUsername(String username) {
        userRepository.deleteById(username);
    }

    public List<UserDTO> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::map).collect(Collectors.toList());
    }

    public void changePassword(String username, String password) {
        userRepository.updatePasswordForUser(username, password);
    }

    public void generateUsers(String role, Integer count) {
        Role roleEntity = entityManager.getReference(Role.class, role);
        User user = new User();
        user.setRole(roleEntity);
        user.setViolationCount(0);
        for (int i = 0; i < count; i++) {
            user.setUsername(UUID.randomUUID().toString());
            user.setPassword(random.ints(48,58)//ascii 48->0, 58 -> 10
                    .limit(16)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString());
            userRepository.save(user);
        }
    }

    public void changeUserRole(String username, String roleName) {
        userRepository.updateUserRole(username, roleName);
    }

    private UserDTO map(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole().getName());
        userDTO.setViolationCount(user.getViolationCount());
        return userDTO;
    }


}
