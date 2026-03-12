// Modified to match with backend SubjectResponseDTO
export interface Subject {
  id: number;
  subjectName: string;
  description: string;
  courseImageUrl: string;
  enrollmentCount?: number;
}

// Modified to match with backend MentorResponseDTO (from GET /api/v1/mentors)
export interface Mentor {
  id: number;
  mentorId: string;
  firstName: string;
  lastName: string;
  email: string;
  title: string;
  profession: string;
  company: string;
  experienceYears: number;
  bio: string;
  profileImageUrl: string;
  positiveReviews: number;
  totalEnrollments: number;
  isCertified: boolean;
  startYear: string;
  subjects: Subject[];
}

// Modified to match with SessionResponseDTO (from GET /api/v1/sessions/my-sessions)
export interface Enrollment {
  id: number;
  mentorName: string;
  mentorProfileImageUrl: string;
  subjectName: string;
  sessionAt: string;
  durationMinutes: number;
  sessionStatus: string;
  paymentStatus: "pending" | "confirmed" | "completed" | "cancelled";
  meetingLink: string | null;
  studentRating?: number | null;
  studentReview?: string | null;
}

export interface User {
  id: string;
  name: string;
  email: string;
}

export interface AdminBooking {
  id: number;
  studentName: string;
  studentEmail: string;
  mentorName: string;
  mentorEmail: string;
  subjectName: string;
  sessionAt: string;
  durationMinutes: number;
  paymentStatus: string;
  sessionStatus: string;
  meetingLink: string | null;
}

export interface MentorReview {
  sessionId: number;
  studentName: string;
  rating: number;
  review: string;
  sessionAt: string;
}

export interface MentorProfile {
  id: number;
  mentorId: string;
  firstName: string;
  lastName: string;
  email: string;
  title: string;
  profession: string;
  company: string;
  experienceYears: number;
  bio: string;
  profileImageUrl: string;
  positiveReviews: number;
  totalEnrollments: number;
  isCertified: boolean;
  startYear: string;
  subjectsCount: number;
  sessionsCount: number;
  reviewsCount: number;
  averageRating: number | null;
  subjects: Subject[];
  reviews: MentorReview[];
}
