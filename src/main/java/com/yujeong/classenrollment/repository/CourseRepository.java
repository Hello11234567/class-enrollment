package com.yujeong.classenrollment.repository;

import com.yujeong.classenrollment.entity.Course;
import com.yujeong.classenrollment.entity.CourseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    //제목으로 조회
    Optional<Course> findByTitle(String title);

    //상태로 목록 조회(상태 필터 기능에 필요)
    List<Course> findByStatus(CourseStatus statues);

    //동시성 제어
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByWithLock(@Param("id") Long id);
}
