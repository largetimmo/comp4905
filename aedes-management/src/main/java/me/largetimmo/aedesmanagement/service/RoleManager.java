package me.largetimmo.aedesmanagement.service;

import lombok.AllArgsConstructor;
import me.largetimmo.aedesmanagement.dto.RoleDTO;
import me.largetimmo.aedesmanagement.entity.Permission;
import me.largetimmo.aedesmanagement.entity.Role;
import me.largetimmo.aedesmanagement.repository.RoleRepository;
import me.largetimmo.aedesmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleManager {

    private RoleRepository roleRepository;

    private UserRepository userRepository;

    private PermissionManager permissionManager;

    private EntityManager entityManager;


    public List<RoleDTO> getAllRoleNames(){
        return roleRepository.findAll().stream().map(r->new RoleDTO(r.getName())).collect(Collectors.toList());
    }

    public List<RoleDTO> getAllRolesWithPermissions(){
        List<RoleDTO> roles = roleRepository.findAll().stream().map(this::map).collect(Collectors.toList());
        for (RoleDTO roleDTO : roles){
            roleDTO.setAssociatedUser(userRepository.countAllByRoleName(roleDTO.getName()));
        }
        return roles;

    }

    public void createRole(RoleDTO roleDTO){
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setPermissions(roleDTO.getPermissions().stream().map(p->entityManager.getReference(Permission.class,p.getId())).collect(Collectors.toSet()));
        roleRepository.save(role);
    }

    public RoleDTO map(Role role){
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName(role.getName());
        roleDTO.setPermissions(role.getPermissions().stream().map(permissionManager::map).collect(Collectors.toList()));
        return roleDTO;
    }

}
