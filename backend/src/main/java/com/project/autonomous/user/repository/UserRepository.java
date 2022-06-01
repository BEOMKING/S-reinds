package com.project.autonomous.user.repository;

import com.project.autonomous.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    User findUserByEmail(String email);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
