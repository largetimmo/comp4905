package me.largetimmo.aedesmanagement.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String regex;

    @Column
    private ActionAvailability availability;
}
