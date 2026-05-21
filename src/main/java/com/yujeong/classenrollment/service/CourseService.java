package com.yujeong.classenrollment.service;

import com.yujeong.classenrollment.entity.Course;
import com.yujeong.classenrollment.entity.CourseStatus;
import com.yujeong.classenrollment.entity.EnrollmentStatus;
import com.yujeong.classenrollment.repository.CourseRepository;
import com.yujeong.classenrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    //강의 등록
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    //강의 목록 조회
    public List<Course> getCourseByStatus(CourseStatus status) {
        if(status == null) {
            return courseRepository.findAll();
        }

        return courseRepository.findByStatus(status);
    }

    //강의 상세 조회
    //두가지 정보 반환(강의 정보, 현재 신청 인원) -> Map 사용
    //Map은 key-value 즉, 여러 데이터를 한 번에 담을 수 있는 바구니
    public Map<String, Object> getCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강의 정보를 찾을 수 없습니다."));

        long currentCount = enrollmentRepository.countByCourseIdAndStatusNot(courseId, EnrollmentStatus.CANCELLED);

        Map<String, Object> response = new HashMap<>();
        response.put("course", course);
        response.put("currentCount", currentCount);

        return response;
    }
    //강의 상태 변경
    public Course changeStatus(Long courseId, CourseStatus newStatus) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강의 정보를 찾을 수 없습니다."));

        // - DRAFT → OPEN → CLOSED 순서만 가능
        if (course.getStatus() == CourseStatus.DRAFT && newStatus == CourseStatus.OPEN) {
            course.setStatus(newStatus);
        } else if (course.getStatus() == CourseStatus.OPEN && newStatus == CourseStatus.CLOSED) {
            course.setStatus(newStatus);
        } else {
            throw  new RuntimeException("잘못된 상태 변경입니다.");
        }
        return courseRepository.save(course);
    }
}
