package com.yujeong.classenrollment.controller;

import com.yujeong.classenrollment.entity.Course;
import com.yujeong.classenrollment.entity.CourseStatus;
import com.yujeong.classenrollment.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    //강의 등록
    @PostMapping
    public ResponseEntity<Course> saveCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.saveCourse(course));
    }

    //강의 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourse(id));
    }

    //강의 목록 조회
    @GetMapping
    public ResponseEntity<List<Course>> getList(@RequestParam(required = false) CourseStatus status) {
        List<Course> list = courseService.getCourseByStatus(status);

        return ResponseEntity.ok(list);
    }

    //강의 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<Course> getStatus(@PathVariable Long id, @RequestParam CourseStatus status) {
        return ResponseEntity.ok(courseService.changeStatus(id, status));
    }
}
