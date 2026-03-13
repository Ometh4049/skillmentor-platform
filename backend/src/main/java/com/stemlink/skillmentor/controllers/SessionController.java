package com.stemlink.skillmentor.controllers;

import com.stemlink.skillmentor.dto.SessionDTO;
import com.stemlink.skillmentor.dto.request.EnrollmentRequestDTO;
import com.stemlink.skillmentor.dto.request.MeetingLinkUpdateDTO;
import com.stemlink.skillmentor.dto.request.SessionReviewRequestDTO;
import com.stemlink.skillmentor.dto.response.AdminSessionResponseDTO;
import com.stemlink.skillmentor.dto.response.SessionResponseDTO;
import com.stemlink.skillmentor.entities.Session;
import com.stemlink.skillmentor.security.UserPrincipal;
import com.stemlink.skillmentor.services.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.stemlink.skillmentor.constants.UserRoles.ROLE_ADMIN;
import static com.stemlink.skillmentor.constants.UserRoles.ROLE_STUDENT;

@RestController
@RequestMapping(path = "/api/v1/sessions")
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class SessionController extends AbstractController {

    private final SessionService sessionService;

    @GetMapping
    public Page<Session> getAllSessions(Pageable pageable) {
        return sessionService.getAllSessions(pageable);
    }

    @GetMapping("{id}")
    public Session getSessionById(@PathVariable Integer id) {
        return sessionService.getSessionById(id);
    }

    @PostMapping
    public Session createSession(@Valid @RequestBody SessionDTO sessionDTO) {
        return sessionService.createNewSession(sessionDTO);
    }

    @PutMapping("{id}")
    public Session updateSession(@PathVariable Integer id, @Valid @RequestBody SessionDTO updatedSessionDTO) {
        return sessionService.updateSessionById(id, updatedSessionDTO);
    }

    @DeleteMapping("{id}")
    public void deleteSession(@PathVariable Integer id) {
        sessionService.deleteSession(id);
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasAnyRole('" + ROLE_STUDENT + "', '" + ROLE_ADMIN + "')")
    public ResponseEntity<SessionResponseDTO> enroll(
            @Valid @RequestBody EnrollmentRequestDTO enrollmentRequestDTO,
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Session session = sessionService.enrollSession(userPrincipal, enrollmentRequestDTO);
        return sendCreatedResponse(toSessionResponseDTO(session));
    }

    @GetMapping("/my-sessions")
    @PreAuthorize("hasAnyRole('" + ROLE_STUDENT + "', '" + ROLE_ADMIN + "')")
    public ResponseEntity<List<SessionResponseDTO>> getMySessions(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<SessionResponseDTO> response = sessionService.getSessionsByStudentIdentity(userPrincipal.getEmail(), userPrincipal.getId())
                .stream()
                .map(this::toSessionResponseDTO)
                .toList();
        return sendOkResponse(response);
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('" + ROLE_STUDENT + "', '" + ROLE_ADMIN + "')")
    public ResponseEntity<SessionResponseDTO> addReview(
            @PathVariable Integer id,
            @Valid @RequestBody SessionReviewRequestDTO reviewRequestDTO,
            Authentication authentication
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Session updatedSession = sessionService.addStudentReview(id, userPrincipal.getEmail(), userPrincipal.getId(), reviewRequestDTO);
        return sendOkResponse(toSessionResponseDTO(updatedSession));
    }

    @GetMapping("/admin/bookings")
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    public ResponseEntity<Page<AdminSessionResponseDTO>> getAdminBookings(
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String sessionStatus,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        Date from = fromDate != null ? toDate(fromDate.atStartOfDay()) : null;
        Date to = toDate != null ? toDate(LocalDateTime.of(toDate, LocalTime.MAX)) : null;

        Page<AdminSessionResponseDTO> response = sessionService
                .getAdminSessions(paymentStatus, sessionStatus, from, to, query, pageable)
                .map(this::toAdminSessionResponseDTO);

        return sendOkResponse(response);
    }

    @PatchMapping("/admin/bookings/{id}/confirm-payment")
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    public ResponseEntity<AdminSessionResponseDTO> confirmPayment(@PathVariable Integer id) {
        Session session = sessionService.confirmPayment(id);
        return sendOkResponse(toAdminSessionResponseDTO(session));
    }

    @PatchMapping("/admin/bookings/{id}/mark-complete")
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    public ResponseEntity<AdminSessionResponseDTO> markCompleted(@PathVariable Integer id) {
        Session session = sessionService.markSessionCompleted(id);
        return sendOkResponse(toAdminSessionResponseDTO(session));
    }

    @PatchMapping("/admin/bookings/{id}/meeting-link")
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    public ResponseEntity<AdminSessionResponseDTO> updateMeetingLink(
            @PathVariable Integer id,
            @Valid @RequestBody MeetingLinkUpdateDTO body
    ) {
        Session session = sessionService.updateMeetingLink(id, body.getMeetingLink());
        return sendOkResponse(toAdminSessionResponseDTO(session));
    }

    private SessionResponseDTO toSessionResponseDTO(Session session) {
        SessionResponseDTO dto = new SessionResponseDTO();
        dto.setId(session.getId());
        dto.setMentorName(session.getMentor().getFirstName() + " " + session.getMentor().getLastName());
        dto.setMentorProfileImageUrl(session.getMentor().getProfileImageUrl());
        dto.setSubjectName(session.getSubject().getSubjectName());
        dto.setSessionAt(session.getSessionAt());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setSessionStatus(session.getSessionStatus());
        dto.setPaymentStatus(session.getPaymentStatus());
        dto.setMeetingLink(session.getMeetingLink());
        dto.setStudentRating(session.getStudentRating());
        dto.setStudentReview(session.getStudentReview());
        return dto;
    }

    private AdminSessionResponseDTO toAdminSessionResponseDTO(Session session) {
        AdminSessionResponseDTO dto = new AdminSessionResponseDTO();
        dto.setId(session.getId());
        dto.setStudentName(session.getStudent() != null
                ? (safe(session.getStudent().getFirstName()) + " " + safe(session.getStudent().getLastName())).trim()
                : "Unknown Student");
        dto.setStudentEmail(session.getStudent() != null ? safe(session.getStudent().getEmail()) : "");
        dto.setMentorName(session.getMentor() != null
                ? (safe(session.getMentor().getFirstName()) + " " + safe(session.getMentor().getLastName())).trim()
                : "Unknown Mentor");
        dto.setMentorEmail(session.getMentor() != null ? safe(session.getMentor().getEmail()) : "");
        dto.setSubjectName(session.getSubject() != null ? safe(session.getSubject().getSubjectName()) : "Unknown Subject");
        dto.setSessionAt(session.getSessionAt());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setPaymentStatus(safe(session.getPaymentStatus()));
        dto.setSessionStatus(safe(session.getSessionStatus()));
        dto.setMeetingLink(session.getMeetingLink());
        return dto;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
