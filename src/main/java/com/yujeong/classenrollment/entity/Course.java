package com.yujeong.classenrollment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
public class Course {
    //강의 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //강의 제목
    @Column(nullable = false, length = 20)
    private String title;

    //강의 가격
    private int price;

    //강의 설명
    @Column(nullable = false, length = 50)
    private String description;

    //강의 최대 인원수
    private int maxCapacity;

    //강의 시작일
    @Column(nullable = false)
    private LocalDate startDate;

    //강의 종료일
    @Column(nullable = false)
    private LocalDate endDate;

    //상태(draft, open, closed)
    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;
}
