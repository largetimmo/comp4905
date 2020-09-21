package me.largetimmo.aedesmanagement.repository;

import me.largetimmo.aedesmanagement.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,String> {
}
