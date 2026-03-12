import type { AdminBooking, Enrollment, Mentor, MentorProfile, Subject } from "@/types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";

type PagedResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

function parseErrorMessage(error: unknown, fallback: string): string {
  if (typeof error === "object" && error && "message" in error) {
    const msg = (error as { message?: string }).message;
    if (typeof msg === "string" && msg.trim()) return msg;
  }

  if (typeof error === "object" && error && "error" in error) {
    const msg = (error as { error?: string }).error;
    if (typeof msg === "string" && msg.trim()) return msg;
  }

  return fallback;
}

async function fetchWithAuth(
  endpoint: string,
  token: string,
  options: RequestInit = {},
): Promise<Response> {
  const res = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      ...options.headers,
    },
  });

  if (!res.ok) {
    const errorBody = await res.json().catch(() => ({}));
    throw new Error(parseErrorMessage(errorBody, `Request failed with HTTP ${res.status}`));
  }

  return res;
}

export async function getPublicMentors(
  page = 0,
  size = 12,
): Promise<PagedResponse<Mentor>> {
  const res = await fetch(`${API_BASE_URL}/api/v1/mentors?page=${page}&size=${size}`);
  if (!res.ok) throw new Error("Failed to fetch mentors");
  return res.json();
}

export async function getMentorProfile(mentorId: string): Promise<MentorProfile> {
  const res = await fetch(`${API_BASE_URL}/api/v1/mentors/profile/${mentorId}`);
  if (!res.ok) throw new Error("Failed to load mentor profile");
  return res.json();
}

export async function enrollInSession(
  token: string,
  data: {
    mentorId: string;
    subjectId: number;
    sessionAt: string;
    durationMinutes?: number;
  },
): Promise<Enrollment> {
  const res = await fetchWithAuth("/api/v1/sessions/enroll", token, {
    method: "POST",
    body: JSON.stringify(data),
  });
  return res.json();
}

export async function getMyEnrollments(token: string): Promise<Enrollment[]> {
  const res = await fetchWithAuth("/api/v1/sessions/my-sessions", token);
  return res.json();
}

export async function submitSessionReview(
  token: string,
  sessionId: number,
  data: { studentRating: number; studentReview: string },
): Promise<Enrollment> {
  const res = await fetchWithAuth(`/api/v1/sessions/${sessionId}/review`, token, {
    method: "PATCH",
    body: JSON.stringify(data),
  });
  return res.json();
}

export async function createSubject(
  token: string,
  data: {
    subjectName: string;
    description: string;
    courseImageUrl: string;
    mentorId: string;
  },
) {
  const res = await fetchWithAuth("/api/v1/subjects", token, {
    method: "POST",
    body: JSON.stringify(data),
  });
  return res.json();
}

export async function getAllSubjects(token: string): Promise<Subject[]> {
  const res = await fetchWithAuth("/api/v1/subjects", token);
  return res.json();
}

export async function createMentor(
  token: string,
  data: {
    mentorId: string;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber?: string;
    title?: string;
    profession?: string;
    company?: string;
    experienceYears?: number;
    bio?: string;
    profileImageUrl?: string;
    positiveReviews?: number;
    totalEnrollments?: number;
    isCertified?: boolean;
    startYear?: string;
  },
) {
  const res = await fetchWithAuth("/api/v1/mentors", token, {
    method: "POST",
    body: JSON.stringify(data),
  });
  return res.json();
}

export async function getAdminBookings(
  token: string,
  params: {
    query?: string;
    paymentStatus?: string;
    sessionStatus?: string;
    fromDate?: string;
    toDate?: string;
    page?: number;
    size?: number;
    sort?: string;
  },
): Promise<PagedResponse<AdminBooking>> {
  const searchParams = new URLSearchParams();
  if (params.query) searchParams.set("query", params.query);
  if (params.paymentStatus && params.paymentStatus !== "all") {
    searchParams.set("paymentStatus", params.paymentStatus);
  }
  if (params.sessionStatus && params.sessionStatus !== "all") {
    searchParams.set("sessionStatus", params.sessionStatus);
  }
  if (params.fromDate) searchParams.set("fromDate", params.fromDate);
  if (params.toDate) searchParams.set("toDate", params.toDate);
  searchParams.set("page", String(params.page ?? 0));
  searchParams.set("size", String(params.size ?? 10));
  searchParams.set("sort", params.sort ?? "sessionAt,desc");

  const res = await fetchWithAuth(`/api/v1/sessions/admin/bookings?${searchParams.toString()}`, token);
  return res.json();
}

export async function confirmBookingPayment(token: string, sessionId: number): Promise<AdminBooking> {
  const res = await fetchWithAuth(`/api/v1/sessions/admin/bookings/${sessionId}/confirm-payment`, token, {
    method: "PATCH",
  });
  return res.json();
}

export async function markBookingComplete(token: string, sessionId: number): Promise<AdminBooking> {
  const res = await fetchWithAuth(`/api/v1/sessions/admin/bookings/${sessionId}/mark-complete`, token, {
    method: "PATCH",
  });
  return res.json();
}

export async function updateBookingMeetingLink(
  token: string,
  sessionId: number,
  meetingLink: string,
): Promise<AdminBooking> {
  const res = await fetchWithAuth(`/api/v1/sessions/admin/bookings/${sessionId}/meeting-link`, token, {
    method: "PATCH",
    body: JSON.stringify({ meetingLink }),
  });
  return res.json();
}
