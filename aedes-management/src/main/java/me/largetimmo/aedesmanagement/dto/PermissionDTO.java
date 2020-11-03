package me.largetimmo.aedesmanagement.dto;

import lombok.Data;

@Data
public class PermissionDTO {

    private Long id;

    private String regex;

    private Integer operation;

}
