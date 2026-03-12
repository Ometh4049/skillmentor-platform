package com.stemlink.skillmentor.services;

import com.stemlink.skillmentor.dto.SessionDTO;
import com.stemlink.skillmentor.dto.request.EnrollmentRequestDTO;
import com.stemlink.skillmentor.dto.request.SessionReviewRequestDTO;
import com.stemlink.skillmentor.entities.Session;
import com.stemlink.skillmentor.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface SessionService {

    Session createNewSession(SessionDTO sessionDTO);

    List<Session> getAllSessions();

    Session getSessionById(Integer id);

    Session updateSessionById(Integer id, SessionDTO updatedSessionDTO);

    void deleteSession(Integer id);

    Session enrollSession(UserPrincipal userPrincipal, EnrollmentRequestDTO enrollmentRequestDTO);

    List<Session> getSessionsByStudentIdentity(String email, String studentId);

    Page<Session> getAdminSessions(
            String paymentStatus,
            String sessionStatus,
            Date fromDate,
            Date toDate,
            String query,
            Pageable pageable
    );

    Session confirmPayment(Integer sessionId);

    Session markSessionCompleted(Integer sessionId);

    Session updateMeetingLink(Integer sessionId, String meetingLink);

    Session addStudentReview(Integer sessionId, String studentEmail, String studentId, SessionReviewRequestDTO reviewRequest);
}
