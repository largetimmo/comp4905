package me.largetimmo.aedesmanagement.controller;

import lombok.AllArgsConstructor;
import me.largetimmo.aedesmanagement.dto.PermissionDTO;
import me.largetimmo.aedesmanagement.entity.Permission;
import me.largetimmo.aedesmanagement.service.PermissionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class PermissionController {

    private PermissionManager permissionManager;

    @GetMapping(path = "/api/permissions")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions(){
        return ResponseEntity.ok(permissionManager.getAllPermissions());
    }

    @PostMapping(path = "/api/permission")
    public ResponseEntity<Void> createPermission(@RequestBody PermissionDTO permissionDTO){
        permissionManager.createPermission(permissionDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/api/permission/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id")Long id){
        permissionManager.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/api/permission")
    public ResponseEntity<Void> updatePermission(@RequestBody PermissionDTO permissionDTO){
        permissionManager.updatePermission(permissionDTO);
        return ResponseEntity.ok().build();
    }

}
