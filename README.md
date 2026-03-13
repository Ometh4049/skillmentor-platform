# SkillMentor - Online Mentoring Platform

SkillMentor is a full-stack mentoring platform that connects students with mentors for subject-based one-on-one sessions.  
Students can discover mentors, view mentor profiles, book sessions, upload payment proof, and track bookings.  
Admins can manage subjects, mentors, and booking lifecycle states from the web UI.

## Tech Stack

- Frontend: React + TypeScript + Vite + Tailwind CSS + shadcn/ui
- Backend: Spring Boot (Java 17), Spring Security, Spring Data JPA
- Database: PostgreSQL (Supabase)
- Authentication: Clerk (JWT via JWKS validation)
- Deployment: Vercel (frontend), Render/Railway (backend)

## Monorepo Structure

```text
.
|-- frontend/   # React app (Vite)
|-- backend/    # Spring Boot API
`-- README.md   # This file
```

## Core Features

### Student Features

- Browse public mentor list
- Open mentor profile page at `/mentors/:mentorId`
- View mentor subjects with enrollment counts
- Book session with selected subject and time
- Upload payment slip
- View personal bookings in dashboard
- Write review for completed sessions

### Admin Features

- Admin-only dashboard routes (`/admin/*`)
- Create subject form
- Create mentor form (with full profile fields)
- Manage bookings table with:
  - filtering
  - pagination
  - confirm payment
  - mark complete
  - meeting link update

### Booking Rules Enforced

- Session date/time cannot be in the past
- Mentor availability conflict detection
- Student overlapping booking conflict detection
- Duplicate overlapping booking prevention for same mentor/subject

## Authentication and Roles (Clerk)

This project expects Clerk JWT and role claims.

### Recommended user public metadata for admin

```json
{
  "roles": ["ADMIN"]
}
```

### Recommended Clerk JWT template custom claim

```json
{
  "roles": "{{user.public_metadata.roles}}"
}
```

Backend role extraction supports:

- `roles`
- `role`
- `public_metadata.roles`
- `public_metadata.role`

If no role is present, backend falls back to `STUDENT`.

## Local Development

## 1) Prerequisites

- Node.js 20+
- npm 10+
- Java 17
- Maven 3.9+
- PostgreSQL (or Supabase project)
- Clerk account and application

## 2) Environment Variables

### Frontend (`frontend/.env`)

```bash
VITE_CLERK_PUBLISHABLE_KEY=pk_test_xxx
VITE_API_BASE_URL=http://localhost:8081
```

### Backend (Render/local environment)

```bash
# App
PORT=8081

# Database
DATABASE_URL=jdbc:postgresql://<host>:5432/<db>
DB_USERNAME=<username>
DB_PASSWORD=<password>

# Clerk
CLERK_JWKS_URL=https://<your-clerk-domain>/.well-known/jwks.json

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3001

# Optional (switch validator implementation)
AUTH_VALIDATOR_TYPE=clerk
```

Notes:

- Backend reads `auth.validator.type` property; default profile is currently `prod`.
- Frontend dev server runs on port `3001`.
- Backend default port is `8081` unless `PORT` is provided.

## 3) Run Backend

```bash
cd backend
mvn spring-boot:run
```

Swagger/OpenAPI:

- `http://localhost:8081/swagger-ui/index.html`
- `http://localhost:8081/v3/api-docs`

## 4) Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

- `http://localhost:3001`

## 5) Build Commands

### Frontend

```bash
cd frontend
npm run build
```

### Backend

```bash
cd backend
mvn -DskipTests compile
```

## API Overview

Base URL: `http://localhost:8081/api/v1`

| Method | Endpoint | Auth | Purpose |
|---|---|---|---|
| GET | `/mentors` | No | List mentors (paged) |
| GET | `/mentors/{id}` | No | Mentor by DB id |
| GET | `/mentors/profile/{mentorId}` | No | Enhanced public mentor profile |
| POST | `/mentors` | Admin/Mentor | Create mentor |
| PUT | `/mentors/{id}` | Admin/Mentor | Update mentor |
| DELETE | `/mentors/{id}` | Admin | Delete mentor |
| GET | `/subjects` | Yes | List subjects |
| GET | `/subjects/{id}` | Yes | Subject by id |
| POST | `/subjects` | Admin | Create subject |
| PUT | `/subjects/{id}` | Admin | Update subject |
| DELETE | `/subjects/{id}` | Admin | Delete subject |
| GET | `/students` | Yes | List students |
| GET | `/students/{id}` | Yes | Student by id |
| POST | `/students` | Student/Admin | Create student |
| PUT | `/students/{id}` | Student/Admin | Update student |
| DELETE | `/students/{id}` | Admin | Delete student |
| GET | `/sessions` | Yes | List sessions (paged) |
| GET | `/sessions/{id}` | Yes | Session by id |
| POST | `/sessions` | Yes | Create session (general API) |
| PUT | `/sessions/{id}` | Yes | Update session |
| DELETE | `/sessions/{id}` | Yes | Delete session |
| POST | `/sessions/enroll` | Student/Admin | Create session booking |
| GET | `/sessions/my-sessions` | Student/Admin | Current user sessions |
| PATCH | `/sessions/{id}/review` | Student/Admin | Add session review |
| GET | `/sessions/admin/bookings` | Admin | Admin booking list (paged/filtered) |
| PATCH | `/sessions/admin/bookings/{id}/confirm-payment` | Admin | Payment pending -> confirmed |
| PATCH | `/sessions/admin/bookings/{id}/mark-complete` | Admin | Session confirmed -> completed |
| PATCH | `/sessions/admin/bookings/{id}/meeting-link` | Admin | Add/update meeting link |

## Key Domain Model

- `Mentor` (1) -> (N) `Subject`
- `Student` (1) -> (N) `Session`
- `Mentor` (1) -> (N) `Session`
- `Subject` (1) -> (N) `Session`

Session stores:

- schedule (`sessionAt`, `durationMinutes`)
- booking/payment states (`sessionStatus`, `paymentStatus`)
- meeting link
- student review and rating

## Deployment Checklist

## Frontend (Vercel)

- Configure `VITE_CLERK_PUBLISHABLE_KEY`
- Configure `VITE_API_BASE_URL` to deployed backend URL
- Ensure SPA route fallback is enabled

## Backend (Render/Railway)

- Configure environment variables listed above
- Ensure `CORS_ALLOWED_ORIGINS` includes Vercel URL
- Ensure `CLERK_JWKS_URL` points to your Clerk instance
- Verify Swagger endpoint is reachable

## Database (Supabase)

- PostgreSQL connection variables set
- Tables available (`mentor`, `student`, `subject`, `session`)
- Seed sample mentors/subjects for discovery UI

## Deployed Links (fill before submission)

- Frontend: `TODO`
- Backend API: `TODO`
- Swagger: `TODO`

## Notes

- `mentorId` used in public routes is the canonical mentor identifier (recommended to align with Clerk user id when mentor identity comes from Clerk).
- A startup schema patch currently ensures `mentor.profile_image_url` is `TEXT` to support long image URLs.
