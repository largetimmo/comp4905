package me.largetimmo.aedesmanagement.repository;

import me.largetimmo.aedesmanagement.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
}
