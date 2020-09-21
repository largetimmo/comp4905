package me.largetimmo.aedesmanagement.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table
public class User {

    @Id
    @Column
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Role role;
}
