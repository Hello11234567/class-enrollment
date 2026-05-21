package com.yujeong.classenrollment.repository;

import com.yujeong.classenrollment.entity.Enrollment;
import com.yujeong.classenrollment.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    //상태로 조회
    List<Enrollment> findByStatus(EnrollmentStatus status);

    //특정 유저의 수강 신청 목록 조회
    List<Enrollment> findByUserId(Long userId);

    //특정 강의의 수강 신청 수 (정원 체크에 필요)
    Long countByCourseIdAndStatusNot(Long courseId, EnrollmentStatus status);
}
