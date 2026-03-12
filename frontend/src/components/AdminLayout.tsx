import { Link, Navigate, NavLink, Outlet } from "react-router";
import { useUser } from "@clerk/clerk-react";
import { cn } from "@/lib/utils";

function isAdminRole(roleValue: unknown): boolean {
  if (typeof roleValue === "string") {
    return roleValue.toLowerCase() === "admin";
  }
  if (Array.isArray(roleValue)) {
    return roleValue.some((role) => typeof role === "string" && role.toLowerCase() === "admin");
  }
  return false;
}

export default function AdminLayout() {
  const { isLoaded, user } = useUser();

  if (!isLoaded) {
    return <div className="container py-10">Loading admin dashboard...</div>;
  }

  const metadata = user?.publicMetadata ?? {};
  const hasAdminRole =
    isAdminRole(metadata.role) || isAdminRole(metadata.roles) || isAdminRole(metadata.userRole);

  if (!hasAdminRole) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="container py-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Admin Dashboard</h1>
        <p className="text-muted-foreground">Manage mentors, subjects, and session bookings.</p>
      </div>
      <div className="grid gap-6 lg:grid-cols-[240px_1fr]">
        <aside className="rounded-xl border bg-card p-3 h-fit">
          <nav className="space-y-1">
            <NavLink
              to="/admin/subjects"
              className={({ isActive }) =>
                cn(
                  "block rounded-md px-3 py-2 text-sm hover:bg-muted",
                  isActive && "bg-primary text-primary-foreground hover:bg-primary",
                )
              }
            >
              Subjects
            </NavLink>
            <NavLink
              to="/admin/subjects/create"
              className={({ isActive }) =>
                cn(
                  "block rounded-md px-3 py-2 text-sm hover:bg-muted",
                  isActive && "bg-primary text-primary-foreground hover:bg-primary",
                )
              }
            >
              Create Subject
            </NavLink>
            <NavLink
              to="/admin/mentors"
              className={({ isActive }) =>
                cn(
                  "block rounded-md px-3 py-2 text-sm hover:bg-muted",
                  isActive && "bg-primary text-primary-foreground hover:bg-primary",
                )
              }
            >
              Create Mentor
            </NavLink>
            <NavLink
              to="/admin/bookings"
              className={({ isActive }) =>
                cn(
                  "block rounded-md px-3 py-2 text-sm hover:bg-muted",
                  isActive && "bg-primary text-primary-foreground hover:bg-primary",
                )
              }
            >
              Manage Bookings
            </NavLink>
          </nav>
          <div className="mt-4 border-t pt-4">
            <Link to="/dashboard" className="text-sm text-muted-foreground hover:underline">
              Back to Student Dashboard
            </Link>
          </div>
        </aside>
        <section className="rounded-xl border bg-card p-4 md:p-6">
          <Outlet />
        </section>
      </div>
    </div>
  );
}
