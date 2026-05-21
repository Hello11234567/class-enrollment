package com.yujeong.classenrollment.service;

import com.yujeong.classenrollment.entity.Course;
import com.yujeong.classenrollment.entity.CourseStatus;
import com.yujeong.classenrollment.entity.Enrollment;
import com.yujeong.classenrollment.entity.EnrollmentStatus;
import com.yujeong.classenrollment.entity.User;
import com.yujeong.classenrollment.repository.EnrollmentRepository;
import com.yujeong.classenrollment.repository.CourseRepository;
import com.yujeong.classenrollment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    //내 수강 목록 조회
    public List<Enrollment> getEnrollment(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    //수강 신청
    @Transactional
    public Enrollment saveEnrollment(Long courseId, Long userId) {
        //강의 찾기
        //A,B 동시 강의 검색 시 모두 findByWithLock 호출 -> A가 먼저 락 획득 -> 강의 조회 성공
        //B는 A 조회가 끝날 때까지 대기
        //A 정원체크 -> 신청 -> 저장 -> 락 해제
        //B 락 획득 -> 강의 조회 -> 정원 체크 -> 정원 초과 예외
        //A 신청 X -> 락 해제 -> B 락 획득 -> 강의 조회 -> 신청 -> 저장 -> 락 해제
        Course course = courseRepository.findByWithLock(courseId)
                .orElseThrow(() -> new RuntimeException("강의 정보를 찾을 수 없습니다."));

        //강의 상태 체크(open상태와 정원초과)
        if (course.getStatus() == CourseStatus.OPEN) {
            Long currentCount = enrollmentRepository.countByCourseIdAndStatusNot(courseId, EnrollmentStatus.CANCELLED);

            if (currentCount >= course.getMaxCapacity()) {
                throw new RuntimeException("정원이 초과되었습니다.");
            }

            //정원 통과 시 enrollment 생성
            Enrollment enrollment = new Enrollment();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));

            enrollment.setUser(user);
            enrollment.setCourse(course);
            enrollment.setStatus(EnrollmentStatus.PENDING);
            enrollment.setEnrolledAt(LocalDateTime.now());

            return enrollmentRepository.save(enrollment);
        } else {
            throw new RuntimeException("신청 불가능한 강의입니다.");
        }
    }

    // 2. 결제 확정
    // - PENDING → CONFIRMED 상태 변경
    public Enrollment courseByConfirmed(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("수강 신청 정보를 찾을 수 없습니다."));

        if (enrollment.getStatus() == EnrollmentStatus.PENDING) {
            enrollment.setStatus(EnrollmentStatus.CONFIRMED);

            return enrollmentRepository.save(enrollment);
        } else {
            throw new RuntimeException("결제가 완료되지 않았습니다.");
        }
    }

    // 3. 수강 취소
    // - CONFIRMED → CANCELLED 상태 변경
    public Enrollment courseByCancelled(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("수강 신청 정보를 찾을 수 없습니다."));

        if (enrollment.getStatus() == EnrollmentStatus.CONFIRMED) {
            enrollment.setStatus(EnrollmentStatus.CANCELLED);

            return enrollmentRepository.save(enrollment);
        } else {
            throw new RuntimeException("취소가 되지 않았습니다.");
        }
    }


}
