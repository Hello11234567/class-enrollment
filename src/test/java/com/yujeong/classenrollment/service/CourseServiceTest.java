package com.yujeong.classenrollment.service;

import com.yujeong.classenrollment.entity.Course;
import com.yujeong.classenrollment.entity.CourseStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@SpringBootTest
@Transactional
class CourseServiceTest {
    @Autowired
    private CourseService courseService;

    // 1. 강의 등록 성공
    @Test
    void 강의등록_성공() {
        //given
        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("테스트 강의");
        course.setPrice(10000);
        course.setMaxCapacity(5);
        course.setStatus(CourseStatus.DRAFT);

        //when
        Course saved = courseService.saveCourse(course);

        //then
        assertNotNull(saved.getId()); //id가 생성됐는지 확인
        assertEquals("테스트 강의", saved.getTitle()); //제목이 맞는지 확인
    }

    // 2. 강의 상태 변경 성공 (DRAFT → OPEN)
    @Test
    void 강의상태변경_성공() {
        //given
        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("테스트");
        course.setStatus(CourseStatus.DRAFT);
        Course saved = courseService.saveCourse(course);

        //when
        Course changed = courseService.changeStatus(saved.getId(), CourseStatus.OPEN);

        //then
        assertEquals(CourseStatus.OPEN, changed.getStatus()); //강의 상태 변경됐는지 확인
    }

    // 3. 잘못된 상태 변경 실패 (OPEN → DRAFT 불가)
    @Test
    void 잘못된상태변경_실패() {
        //given
        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("잘못된 변경 테스트");
        course.setStatus(CourseStatus.OPEN);
        Course saved = courseService.saveCourse(course);

        //when + then (예외 테스트는 결과를 받을 게 없으므로 when과 then을 합쳐서 쓴게 자연스러움)
        assertThrows(RuntimeException.class, () -> {
            courseService.changeStatus(saved.getId(), CourseStatus.DRAFT);
        });
    }

    // 4. 강의 조회 성공
    @Test
    void 강의조회_성공() {
        //given
        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("강의 조회");
        Course saved = courseService.saveCourse(course);

        //when
        Map<String, Object> search = courseService.getCourse(saved.getId());
        Course foundCourse = (Course) search.get("course");

        //then
        assertNotNull(saved.getId()); //id가 생성됐는지 확인
        assertEquals("강의 조회", foundCourse.getTitle()); //제목으로 조회
        assertNotNull(search.get("currentCount")); //신청 인원 확인
    }

    // 5. 없는 강의 조회 실패
    @Test
    void 없는강의조회_실패() {
        //given
        Long 잘못된강의 = 9999L;

        //when + then
        assertThrows(RuntimeException.class, () ->{
            courseService.getCourse(잘못된강의);
        });
    }

    //6. 강의 목록 조회
    @Test
    void 강의목록조회_성공() {
        //given
        Course course = new Course();

        course.setDescription("테스트 설명");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(1));
        course.setTitle("강의 목록 조회");
        course.setStatus(CourseStatus.OPEN);
        Course saved = courseService.saveCourse(course);

        //when
        List<Course> search = courseService.getCourseByStatus(CourseStatus.OPEN);

        //then
        assertEquals(1, search.size());
        assertFalse(search.isEmpty());
    }
}