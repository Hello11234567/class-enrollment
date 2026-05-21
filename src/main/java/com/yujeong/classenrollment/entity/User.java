package com.yujeong.classenrollment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    //사용자 고유 ID 생성
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //사용자 이름
    @Column(nullable = false, length = 10)
    private String name;

    //사용자 이메일
    @Column(nullable = false, length = 30)
    private String email;
}
