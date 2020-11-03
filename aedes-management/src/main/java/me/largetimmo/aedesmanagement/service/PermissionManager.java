package me.largetimmo.aedesmanagement.service;

import lombok.AllArgsConstructor;
import me.largetimmo.aedesmanagement.dto.PermissionDTO;
import me.largetimmo.aedesmanagement.entity.ActionAvailability;
import me.largetimmo.aedesmanagement.entity.Permission;
import me.largetimmo.aedesmanagement.repository.PermissionRepository;
import me.largetimmo.aedesmanagement.repository.RoleRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PermissionManager {

    private PermissionRepository permissionRepository;

    private RoleRepository roleRepository;

    public List<PermissionDTO> getAllPermissions(){
        return permissionRepository.findAll().stream().map(this::map).collect(Collectors.toList());
    }

    public void createPermission(PermissionDTO permissionDTO){
        Permission permission = new Permission();
        permission.setRegex(permissionDTO.getRegex());
        permission.setAvailability(ActionAvailability.getById(permissionDTO.getOperation()));
        permissionRepository.save(permission);
    }

    public void updatePermission(PermissionDTO permissionDTO){
        Permission permission = new Permission();
        permission.setId(permissionDTO.getId());
        permission.setRegex(permissionDTO.getRegex());
        permission.setAvailability(ActionAvailability.getById(permissionDTO.getOperation()));
        permissionRepository.save(permission);
    }

    public void deleteById(Long id){
        permissionRepository.deleteById(id);
    }

    public PermissionDTO map(Permission permission){
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId(permission.getId());
        permissionDTO.setOperation(permission.getAvailability().getId());
        permissionDTO.setRegex(permission.getRegex());
        return permissionDTO;
    }

}
