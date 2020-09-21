package me.largetimmo.aedesmanagement.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Data
public class Role {

    @Id
    private String name;

    @OneToMany
    @Column
    private Set<Permission> permissions;
}
