package me.largetimmo.aedesmanagement.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Role role;
}
