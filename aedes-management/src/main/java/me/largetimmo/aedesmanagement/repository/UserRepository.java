package me.largetimmo.aedesmanagement.repository;

import me.largetimmo.aedesmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User,String> {

    @Modifying
    @Query("update User u set u.password = :password where u.username =:username")
    void updatePasswordForUser(@Param("username")String username, @Param("password")String password);

    @Modifying
    @Query("update User u set u.role.name = :roleName where u.username = :username")
    void updateUserRole(@Param("username")String username, @Param("roleName") String roleName);

    @Query("select count (u) from User u where u.role.name = :roleName")
    Integer countAllByRoleName(@Param("roleName")String roleName);
}
