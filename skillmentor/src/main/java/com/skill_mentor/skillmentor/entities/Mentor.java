package com.skill_mentor.skillmentor.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mentor")
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email",length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", length = 50, nullable = true)
    private String firstName;

    @Column(name = "last_name",length = 50, nullable = false)
    private String lastName;

    @Column(length = 20, unique = true)
    private String phoneNumber;

    private String profession;

    private String company;

    @Column(name = "title",length = 50)
    private String title;

    @Column(name = "experience_years", nullable = false)
    private int experienceYears;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "availability_start", nullable = true)
    private LocalDateTime availabilityStart;

    @Column(name = "availability_end", nullable = true)
    private LocalDateTime availabilityEnd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany
    private List<Subject> subjects;
}
