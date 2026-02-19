package com.skill_mentor.skillmentor.repositories;

import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.entities.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    @Query("SELECT m FROM Mentor m WHERE LOWER(m.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Mentor> findByName(String name, Pageable pageable);
}
