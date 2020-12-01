package me.largetimmo.aedesmanagement.controller;

import me.largetimmo.aedesmanagement.dto.UserDTO;
import me.largetimmo.aedesmanagement.entity.User;
import me.largetimmo.aedesmanagement.service.UserManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping(path = "/api/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(userManager.getAll());
    }

    @PutMapping(path = "/api/user/{username}/password")
    public ResponseEntity<Void> updateUserPassword(@PathVariable("username")String username, @RequestBody UserDTO userDTO){
        String password = userDTO.getPassword();
        userManager.changePassword(username,password);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/api/user/role")
    public ResponseEntity<Void> updateUserRole(@RequestBody UserDTO userDTO){
        String username = userDTO.getUsername();
        String role = userDTO.getRole();
        userManager.changeUserRole(username,role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/api/user/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable("username")String username){
        userManager.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/api/user")
    public ResponseEntity<Void> createUser(@RequestBody UserDTO userDTO){
        userManager.create(userDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/api/user/bulk",params = "count")
    public ResponseEntity<Void> generateUsers(@RequestParam("count")Integer count, @RequestBody UserDTO userDTO){
        userManager.generateUsers(userDTO.getRole(),count);
        return ResponseEntity.ok().build();
    }


}
