package com.stemlink.skillmentor.services.impl;

import com.stemlink.skillmentor.dto.SessionDTO;
import com.stemlink.skillmentor.dto.request.EnrollmentRequestDTO;
import com.stemlink.skillmentor.dto.request.SessionReviewRequestDTO;
import com.stemlink.skillmentor.entities.Mentor;
import com.stemlink.skillmentor.entities.Session;
import com.stemlink.skillmentor.entities.Student;
import com.stemlink.skillmentor.entities.Subject;
import com.stemlink.skillmentor.exceptions.SkillMentorException;
import com.stemlink.skillmentor.respositories.MentorRepository;
import com.stemlink.skillmentor.respositories.SessionRepository;
import com.stemlink.skillmentor.respositories.StudentRepository;
import com.stemlink.skillmentor.respositories.SubjectRepository;
import com.stemlink.skillmentor.security.UserPrincipal;
import com.stemlink.skillmentor.services.SessionService;
import com.stemlink.skillmentor.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_CONFIRMED = "confirmed";
    private static final String STATUS_COMPLETED = "completed";

    private final SessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Session createNewSession(SessionDTO sessionDTO) {
        try {
            Student student = studentRepository.findById(sessionDTO.getStudentId()).orElseThrow(
                    () -> new SkillMentorException("Student not found", HttpStatus.NOT_FOUND)
            );
            Mentor mentor = mentorRepository.findByMentorId(sessionDTO.getMentorId()).orElseThrow(
                    () -> new SkillMentorException("Mentor not found", HttpStatus.NOT_FOUND)
            );
            Subject subject = subjectRepository.findById(sessionDTO.getSubjectId()).orElseThrow(
                    () -> new SkillMentorException("Subject not found", HttpStatus.NOT_FOUND)
            );

            validateBooking(student, mentor, subject, sessionDTO.getSessionAt(), sessionDTO.getDurationMinutes());

            Session session = modelMapper.map(sessionDTO, Session.class);
            session.setStudent(student);
            session.setMentor(mentor);
            session.setSubject(subject);

            return sessionRepository.save(session);
        } catch (SkillMentorException skillMentorException) {
            throw skillMentorException;
        } catch (Exception exception) {
            log.error("Failed to create session", exception);
            throw new SkillMentorException("Failed to create new session", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public Session getSessionById(Integer id) {
        return sessionRepository.findById(id).orElseThrow(
                () -> new SkillMentorException("Session not found", HttpStatus.NOT_FOUND)
        );
    }

    @Override
    @Transactional
    public Session updateSessionById(Integer id, SessionDTO updatedSessionDTO) {
        Session session = getSessionById(id);

        modelMapper.map(updatedSessionDTO, session);

        if (updatedSessionDTO.getStudentId() != null) {
            Student student = studentRepository.findById(updatedSessionDTO.getStudentId()).orElseThrow(
                    () -> new SkillMentorException("Student not found", HttpStatus.NOT_FOUND)
            );
            session.setStudent(student);
        }
        if (updatedSessionDTO.getMentorId() != null) {
            Mentor mentor = mentorRepository.findByMentorId(updatedSessionDTO.getMentorId())
                    .orElseThrow(() -> new SkillMentorException("Mentor not found", HttpStatus.NOT_FOUND));
            session.setMentor(mentor);
        }
        if (updatedSessionDTO.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(updatedSessionDTO.getSubjectId()).orElseThrow(
                    () -> new SkillMentorException("Subject not found", HttpStatus.NOT_FOUND)
            );
            session.setSubject(subject);
        }

        return sessionRepository.save(session);
    }

    @Override
    @Transactional
    public void deleteSession(Integer id) {
        if (!sessionRepository.existsById(id)) {
            throw new SkillMentorException("Session not found", HttpStatus.NOT_FOUND);
        }
        sessionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Session enrollSession(UserPrincipal userPrincipal, EnrollmentRequestDTO enrollmentRequestDTO) {
        Student student = resolveOrCreateStudent(userPrincipal);

        Mentor mentor = mentorRepository.findByMentorId(enrollmentRequestDTO.getMentorId())
                .orElseThrow(() -> new SkillMentorException("Mentor not found", HttpStatus.NOT_FOUND));
        Subject subject = subjectRepository.findById(enrollmentRequestDTO.getSubjectId())
                .orElseThrow(() -> new SkillMentorException("Subject not found", HttpStatus.NOT_FOUND));

        validateBooking(student, mentor, subject, enrollmentRequestDTO.getSessionAt(), enrollmentRequestDTO.getDurationMinutes());

        Session session = new Session();
        session.setStudent(student);
        session.setMentor(mentor);
        session.setSubject(subject);
        session.setSessionAt(enrollmentRequestDTO.getSessionAt());
        session.setDurationMinutes(enrollmentRequestDTO.getDurationMinutes() != null ? enrollmentRequestDTO.getDurationMinutes() : 60);
        session.setSessionStatus(STATUS_PENDING);
        session.setPaymentStatus(STATUS_PENDING);

        return sessionRepository.save(session);
    }

    @Override
    public Page<Session> getAdminSessions(
            String paymentStatus,
            String sessionStatus,
            Date fromDate,
            Date toDate,
            String query,
            Pageable pageable
    ) {
        String normalizedPaymentStatus = paymentStatus != null ? paymentStatus.trim() : "";
        String normalizedSessionStatus = sessionStatus != null ? sessionStatus.trim() : "";
        String normalizedQuery = query != null ? query.trim() : "";
        Date normalizedFromDate = fromDate != null ? fromDate : new Date(0L);
        Date normalizedToDate = toDate != null ? toDate : new Date(4102444799000L); // 2099-12-31T23:59:59Z

        return sessionRepository.findAdminSessions(
                normalizedPaymentStatus,
                normalizedSessionStatus,
                normalizedFromDate,
                normalizedToDate,
                normalizedQuery,
                pageable
        );
    }

    @Override
    @Transactional
    public Session confirmPayment(Integer sessionId) {
        Session session = getSessionById(sessionId);

        if (!STATUS_PENDING.equalsIgnoreCase(session.getPaymentStatus())) {
            throw new SkillMentorException("Only pending payments can be confirmed", HttpStatus.BAD_REQUEST);
        }

        session.setPaymentStatus(STATUS_CONFIRMED);
        if (session.getSessionStatus() == null || STATUS_PENDING.equalsIgnoreCase(session.getSessionStatus())) {
            session.setSessionStatus(STATUS_CONFIRMED);
        }

        return sessionRepository.save(session);
    }

    @Override
    @Transactional
    public Session markSessionCompleted(Integer sessionId) {
        Session session = getSessionById(sessionId);

        if (!STATUS_CONFIRMED.equalsIgnoreCase(session.getSessionStatus())) {
            throw new SkillMentorException("Only confirmed sessions can be completed", HttpStatus.BAD_REQUEST);
        }

        session.setSessionStatus(STATUS_COMPLETED);
        return sessionRepository.save(session);
    }

    @Override
    @Transactional
    public Session updateMeetingLink(Integer sessionId, String meetingLink) {
        Session session = getSessionById(sessionId);
        session.setMeetingLink(meetingLink);
        return sessionRepository.save(session);
    }

    @Override
    @Transactional
    public Session addStudentReview(Integer sessionId, String studentEmail, String studentId, SessionReviewRequestDTO reviewRequest) {
        Session session;

        if (isNotBlank(studentEmail)) {
            session = sessionRepository.findByIdAndStudent_Email(sessionId, studentEmail)
                    .orElseThrow(() -> new SkillMentorException("Session not found for current student", HttpStatus.NOT_FOUND));
        } else if (isNotBlank(studentId)) {
            session = sessionRepository.findByIdAndStudent_StudentId(sessionId, studentId)
                    .orElseThrow(() -> new SkillMentorException("Session not found for current student", HttpStatus.NOT_FOUND));
        } else {
            throw new SkillMentorException("Student identity is missing in token", HttpStatus.UNAUTHORIZED);
        }

        if (!STATUS_COMPLETED.equalsIgnoreCase(session.getSessionStatus())) {
            throw new SkillMentorException("Reviews are allowed only for completed sessions", HttpStatus.BAD_REQUEST);
        }

        session.setStudentRating(reviewRequest.getStudentRating());
        session.setStudentReview(reviewRequest.getStudentReview());
        return sessionRepository.save(session);
    }

    @Override
    public List<Session> getSessionsByStudentIdentity(String email, String studentId) {
        if (isNotBlank(email)) {
            return sessionRepository.findByStudent_Email(email);
        }

        if (isNotBlank(studentId)) {
            Optional<Student> student = studentRepository.findByStudentId(studentId);
            if (student.isEmpty()) {
                return List.of();
            }
            return sessionRepository.findByStudent_Id(student.get().getId());
        }

        throw new SkillMentorException("Student identity is missing in token", HttpStatus.UNAUTHORIZED);
    }

    private Student resolveOrCreateStudent(UserPrincipal userPrincipal) {
        String studentId = userPrincipal.getId();
        if (!isNotBlank(studentId)) {
            throw new SkillMentorException("Invalid token: missing user id", HttpStatus.UNAUTHORIZED);
        }

        Optional<Student> existingByStudentId = studentRepository.findByStudentId(studentId);
        if (existingByStudentId.isPresent()) {
            return existingByStudentId.get();
        }

        String email = normalizeEmail(userPrincipal.getEmail(), studentId);
        Optional<Student> existingByEmail = studentRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {
            Student existing = existingByEmail.get();
            if (!isNotBlank(existing.getStudentId())) {
                existing.setStudentId(studentId);
                return studentRepository.save(existing);
            }
            return existing;
        }

        Student student = new Student();
        student.setStudentId(studentId);
        student.setEmail(email);
        student.setFirstName(isNotBlank(userPrincipal.getFirstName()) ? userPrincipal.getFirstName() : "Student");
        student.setLastName(isNotBlank(userPrincipal.getLastName()) ? userPrincipal.getLastName() : "User");
        return studentRepository.save(student);
    }

    private String normalizeEmail(String email, String studentId) {
        if (isNotBlank(email)) {
            return email.trim().toLowerCase();
        }
        return studentId + "@users.skillmentor.local";
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void validateBooking(Student student, Mentor mentor, Subject subject, Date sessionAt, Integer durationMinutes) {
        if (sessionAt == null) {
            throw new SkillMentorException("Session date/time is required", HttpStatus.BAD_REQUEST);
        }

        if (sessionAt.before(new Date())) {
            throw new SkillMentorException("Session date/time cannot be in the past", HttpStatus.BAD_REQUEST);
        }

        if (subject.getMentor() == null || !subject.getMentor().getId().equals(mentor.getId())) {
            throw new SkillMentorException("Selected subject does not belong to the selected mentor", HttpStatus.BAD_REQUEST);
        }

        student.setSessions(sessionRepository.findByStudent_Id(student.getId()));
        mentor.setSessions(sessionRepository.findByMentor_Id(mentor.getId()));

        ValidationUtils.validateMentorAvailability(mentor, sessionAt, durationMinutes);
        ValidationUtils.validateStudentAvailability(student, sessionAt, durationMinutes);

        Date requestedSessionEnd = ValidationUtils.addMinutesToDate(sessionAt, durationMinutes != null ? durationMinutes : 60);
        for (Session existingSession : student.getSessions()) {
            Date existingSessionEnd = ValidationUtils.addMinutesToDate(
                    existingSession.getSessionAt(),
                    existingSession.getDurationMinutes() != null ? existingSession.getDurationMinutes() : 60
            );
            boolean overlapping = ValidationUtils.isTimeOverlap(
                    sessionAt,
                    requestedSessionEnd,
                    existingSession.getSessionAt(),
                    existingSessionEnd
            );

            if (!overlapping) {
                continue;
            }

            if (existingSession.getMentor() != null && existingSession.getMentor().getId().equals(mentor.getId())) {
                throw new SkillMentorException("You already have an overlapping booking with this mentor", HttpStatus.CONFLICT);
            }

            if (existingSession.getSubject() != null && existingSession.getSubject().getId().equals(subject.getId())) {
                throw new SkillMentorException("You already have an overlapping booking for this subject", HttpStatus.CONFLICT);
            }
        }
    }
}
