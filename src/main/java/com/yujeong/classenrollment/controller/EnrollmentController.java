package com.yujeong.classenrollment.controller;

import com.yujeong.classenrollment.entity.Enrollment;
import com.yujeong.classenrollment.entity.EnrollmentStatus;
import com.yujeong.classenrollment.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    //내 수강 목록 조회
    @GetMapping("/my")
    public ResponseEntity<List<Enrollment>> getList(@RequestParam Long id) {
        List<Enrollment> list = enrollmentService.getEnrollment(id);

        return ResponseEntity.ok(list);
    }

    //수강 신청
    @PostMapping
    public ResponseEntity<Enrollment> getCourse(@RequestParam Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(enrollmentService.saveEnrollment(id, userId));
    }

    //결제 확정
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Enrollment> getConfirm(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.courseByConfirmed(id));
    }

    //수강 취소
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Enrollment> getCancel(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.courseByCancelled(id));
    }

    //페이지네이션
    @GetMapping("/my/page")
    public ResponseEntity<Enrollment> getEnrollmentPage(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentPage(userId, page, size));
    }
}
