import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import Layout from "@/components/Layout";
import HomePage from "@/pages/HomePage";
import LoginPage from "@/pages/LoginPage";
import DashboardPage from "@/pages/DashboardPage";
import PaymentPage from "@/pages/PaymentPage";
import MentorProfilePage from "@/pages/MentorProfilePage";
import { SignedIn, SignedOut } from "@clerk/clerk-react";
import AdminLayout from "@/components/AdminLayout";
import CreateSubjectPage from "@/pages/admin/CreateSubjectPage";
import CreateMentorPage from "@/pages/admin/CreateMentorPage";
import ManageBookingsPage from "@/pages/admin/ManageBookingsPage";
import SubjectsListPage from "@/pages/admin/SubjectsListPage";

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/mentors/:mentorId" element={<MentorProfilePage />} />
          <Route
            path="/dashboard"
            element={
              <>
                <SignedIn>
                  <DashboardPage />
                </SignedIn>
                <SignedOut>
                  <LoginPage />
                </SignedOut>
              </>
            }
          />
          <Route
            path="/payment/:sessionId"
            element={
              <>
                <SignedIn>
                  <PaymentPage />
                </SignedIn>
                <SignedOut>
                  <LoginPage />
                </SignedOut>
              </>
            }
          />
          <Route
            path="/admin"
            element={
              <>
                <SignedIn>
                  <AdminLayout />
                </SignedIn>
                <SignedOut>
                  <LoginPage />
                </SignedOut>
              </>
            }
          >
            <Route index element={<Navigate to="/admin/bookings" replace />} />
            <Route path="subjects" element={<SubjectsListPage />} />
            <Route path="subjects/create" element={<CreateSubjectPage />} />
            <Route path="mentors" element={<CreateMentorPage />} />
            <Route path="bookings" element={<ManageBookingsPage />} />
          </Route>
          <Route path="*" element={<LoginPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;
