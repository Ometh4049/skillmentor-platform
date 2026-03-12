package com.stemlink.skillmentor.respositories;

import com.stemlink.skillmentor.entities.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByStudent_Email(String email);

    List<Session> findByStudent_Id(Integer studentId);

    List<Session> findByMentor_Id(Long mentorId);

    Optional<Session> findByIdAndStudent_Email(Integer id, String email);

    Optional<Session> findByIdAndStudent_StudentId(Integer id, String studentId);

    long countBySubject_Id(Long subjectId);

    long countByMentor_Id(Long mentorId);

    long countByMentor_IdAndStudentRatingIsNotNull(Long mentorId);

    @Query("SELECT AVG(s.studentRating) FROM Session s WHERE s.mentor.id = :mentorId AND s.studentRating IS NOT NULL")
    Double findAverageRatingByMentorId(@Param("mentorId") Long mentorId);

    List<Session> findTop10ByMentor_IdAndStudentReviewIsNotNullAndStudentRatingIsNotNullOrderBySessionAtDesc(Long mentorId);

    @Query("""
            SELECT s FROM Session s
            LEFT JOIN s.student st
            LEFT JOIN s.mentor m
            LEFT JOIN s.subject sub
            WHERE (:paymentStatus = '' OR LOWER(CAST(s.paymentStatus AS string)) = LOWER(CAST(:paymentStatus AS string)))
              AND (:sessionStatus = '' OR LOWER(CAST(s.sessionStatus AS string)) = LOWER(CAST(:sessionStatus AS string)))
              AND s.sessionAt >= :fromDate
              AND s.sessionAt <= :toDate
              AND (
                :query = '' OR
                LOWER(CONCAT(COALESCE(CAST(st.firstName AS string), ''), ' ', COALESCE(CAST(st.lastName AS string), ''))) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR
                LOWER(CONCAT(COALESCE(CAST(m.firstName AS string), ''), ' ', COALESCE(CAST(m.lastName AS string), ''))) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR
                LOWER(COALESCE(CAST(sub.subjectName AS string), '')) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
              )
            """)
    Page<Session> findAdminSessions(
            @Param("paymentStatus") String paymentStatus,
            @Param("sessionStatus") String sessionStatus,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("query") String query,
            Pageable pageable
    );
}
