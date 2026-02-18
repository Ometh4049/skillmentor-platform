package com.skill_mentor.skillmentor.services.impl;


import com.skill_mentor.skillmentor.entities.Student;
import com.skill_mentor.skillmentor.exception.SkillMentorException;
import com.skill_mentor.skillmentor.repositories.StudentRepository;
import com.skill_mentor.skillmentor.services.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public Student createNewStudent(Student student){
        try {
            return studentRepository.save(student);
        }
        catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating student: {}", e.getMessage());
            throw new SkillMentorException("student with this email already exists", HttpStatus.CONFLICT);
        }
        catch (Exception exception) {
            log.error("Failed to create new student", exception);
            throw new SkillMentorException("Failed to Create new student", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Page<Student> getAllStudents(String name, Pageable pageable){
        try {
            if (name != null &&  !name.isEmpty() && !name.equalsIgnoreCase("all")) {
                return studentRepository.findByName(name, pageable);
            }
            return studentRepository.findAll(pageable);
        } catch (Exception exception) {
            log.error("Failed to get all students", exception);
            throw new SkillMentorException("Failed to get all students", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Student getStudentById(Long id){
        try {

            Student student = studentRepository.findById(id).orElseThrow(
                    ()->new SkillMentorException("Student Not Found",HttpStatus.NOT_FOUND)
            );
            log.info("Successfully fetched student {}", id);
            return student;
        } catch (SkillMentorException skillMentorException) {
            log.error("Student Not Found " , skillMentorException);
            throw new SkillMentorException("Student Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("Error getting Student " , exception);
            throw new SkillMentorException("Failed to get student", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Student updateStudentById(Long id, Student updatedStudent){

        try {
            Student existingStudent = studentRepository.findById(id).orElseThrow(
                    ()->new SkillMentorException("Student Not Found",HttpStatus.NOT_FOUND));

            modelMapper.map(updatedStudent,existingStudent);

            return studentRepository.save(existingStudent);

        } catch (SkillMentorException skillMentorException) {
            log.warn("Student not found with id: {} to update", id, skillMentorException);
            throw new SkillMentorException("Student Not Found", HttpStatus.NOT_FOUND);
        }
        catch (Exception exception){
            log.error("Error Update Student ", exception);
            throw new SkillMentorException("Failed to update student", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public void deleteStudent(Long id){

        try {
            Student existingStudent = studentRepository.findById(id).orElseThrow(
                    ()->new SkillMentorException("Student Not Found",HttpStatus.NOT_FOUND));
            studentRepository.delete(existingStudent);
        }catch (SkillMentorException skillMentorException) {
            log.warn("Student not found with id: {} to delete", id, skillMentorException);
            throw new SkillMentorException("Student Not Found", HttpStatus.NOT_FOUND);
        }catch (Exception exception) {
            log.error("Error delete Student " , exception);
            throw new SkillMentorException("Failed to delete student", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
