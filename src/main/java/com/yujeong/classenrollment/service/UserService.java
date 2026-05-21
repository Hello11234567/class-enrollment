package com.yujeong.classenrollment.service;

import com.yujeong.classenrollment.entity.User;
import com.yujeong.classenrollment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //유저 생성
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    //유저 조회
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
    }

    //이메일로 유저 조회
    public User getEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("이메일 정보를 찾을 수 없습니다."));
    }
}
