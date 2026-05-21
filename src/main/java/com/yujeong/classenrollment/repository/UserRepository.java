package com.yujeong.classenrollment.repository;

import com.yujeong.classenrollment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //이메일로 조회
    Optional<User> findByEmail(String email);
}
