package me.largetimmo.aedesmanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoleDTO {

    private String name;

    private List<PermissionDTO> permissions;

    private Integer associatedUser;

    public RoleDTO(String name) {
        this.name = name;
    }
}
