package me.largetimmo.aedesmanagement.controller;

import me.largetimmo.aedesmanagement.dto.RoleDTO;
import me.largetimmo.aedesmanagement.repository.RoleRepository;
import me.largetimmo.aedesmanagement.service.RoleManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {

    private RoleManager roleManager;

    public RoleController(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @GetMapping(path = "/api/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoleNames(@RequestParam(value = "all", required = false) String needAll) {
        if (needAll == null) {
            return ResponseEntity.ok(roleManager.getAllRoleNames());
        }
        return ResponseEntity.ok(roleManager.getAllRolesWithPermissions());
    }

    @PostMapping(path = "/api/role")
    public ResponseEntity<Void> createRole(@RequestBody RoleDTO roleDTO) {
        roleManager.createRole(roleDTO);
        return ResponseEntity.ok().build();
    }

}
