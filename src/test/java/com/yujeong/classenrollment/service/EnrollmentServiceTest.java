package com.yujeong.classenrollment.service;

import com.yujeong.classenrollment.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EnrollmentServiceTest {
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;

    //1. 수강신청 성공
    @Test
    void 수강신청_성공() {
        //given
        User user = new User();

        user.setName("테스트 유저");
        user.setEmail("test@test.com");
        User savedUser = userService.saveUser(user);

        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("테스트 강의");
        course.setMaxCapacity(5);
        course.setStatus(CourseStatus.OPEN);
        Course savedCourse = courseService.saveCourse(course);

        //when
        Enrollment enrollment = enrollmentService.saveEnrollment(savedCourse.getId(), savedUser.getId());

        //then
        assertNotNull(enrollment.getId());
        assertEquals(EnrollmentStatus.PENDING, enrollment.getStatus());
    }

    //2. 정원초과 신청 실패
    @Test
    void 정원초과신청_실패() {
        //given
        User user1 = new User();

        user1.setName("테스트 유저");
        user1.setEmail("test1@test.com");
        User savedUser1 = userService.saveUser(user1);

        User user2 = new User();

        user2.setName("테스트 유저");
        user2.setEmail("test2@test.com");
        User savedUser2 = userService.saveUser(user2);

        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("테스트 강의");
        course.setMaxCapacity(1);
        course.setStatus(CourseStatus.OPEN);
        Course savedCourse = courseService.saveCourse(course);

        //when + then
        enrollmentService.saveEnrollment(savedCourse.getId(), savedUser1.getId());

        assertThrows(RuntimeException.class, () -> {
            enrollmentService.saveEnrollment(savedCourse.getId(), savedUser2.getId());
        });
    }

    //3. 오픈하지않은 강의 신청 실패
    @Test
    void 열지않은강의신청_실패() {
        //given
        User user = new User();

        user.setName("테스트 유저");
        user.setEmail("test@test.com");
        User savedUser = userService.saveUser(user);

        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("테스트 강의");
        course.setStatus(CourseStatus.DRAFT);
        Course savedCourse = courseService.saveCourse(course);

        //when + then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.saveEnrollment(savedCourse.getId(), savedUser.getId());
        });
    }

    //4. 결제 확정
    @Test
    void 결제확정_성공() {
        //given
        User user = new User();

        user.setName("테스트 유저");
        user.setEmail("test@test.com");
        User savedUser = userService.saveUser(user);

        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("테스트 강의");
        course.setMaxCapacity(5);
        course.setStatus(CourseStatus.OPEN);
        Course savedCourse = courseService.saveCourse(course);

        //when
        Enrollment savedEnrollment = enrollmentService.saveEnrollment(savedCourse.getId(), savedUser.getId());
        Enrollment enrollment = enrollmentService.courseByConfirmed(savedEnrollment.getId());

        //then
        assertEquals(EnrollmentStatus.CONFIRMED, enrollment.getStatus());
    }

    //5. 수강 신청 취소
    @Test
    void 신청취소_성공() {
        User user = new User();

        user.setName("테스트 유저");
        user.setEmail("test@test.com");
        User savedUser = userService.saveUser(user);

        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("테스트 강의");
        course.setMaxCapacity(5);
        course.setStatus(CourseStatus.OPEN);
        Course savedCourse = courseService.saveCourse(course);

        //when
        Enrollment savedEnrollment = enrollmentService.saveEnrollment(savedCourse.getId(), savedUser.getId());
        Enrollment confirmed = enrollmentService.courseByConfirmed(savedEnrollment.getId()); //결제 완료 상태
        Enrollment cancelled = enrollmentService.courseByCancelled(confirmed.getId()); //취소

        //then
        assertEquals(EnrollmentStatus.CANCELLED, cancelled.getStatus());
    }

    //6. 수강 목록 조회
    @Test
    void 수강목록조회_성공() {
        //given
        User user = new User();

        user.setName("테스트 유저");
        user.setEmail("test@test.com");
        User savedUser = userService.saveUser(user);

        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("테스트 강의");
        course.setMaxCapacity(5);
        course.setStatus(CourseStatus.OPEN);
        Course savedCourse = courseService.saveCourse(course);

        //when
        enrollmentService.saveEnrollment(savedCourse.getId(), savedUser.getId());

        List<Enrollment> search = enrollmentService.getEnrollment(savedUser.getId());

        //then
        assertFalse(search.isEmpty());
    }
}