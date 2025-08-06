package com.pawnder.repository;

import com.pawnder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email); //소셜로그인을 위한 find

}
