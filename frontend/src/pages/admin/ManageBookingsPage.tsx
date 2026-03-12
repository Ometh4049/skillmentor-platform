import { useEffect, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import type { AdminBooking } from "@/types";
import {
  confirmBookingPayment,
  getAdminBookings,
  markBookingComplete,
  updateBookingMeetingLink,
} from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/components/hooks/use-toast";
import { StatusPill } from "@/components/StatusPill";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

type SortOption = "sessionAt,desc" | "sessionAt,asc" | "student.firstName,asc" | "mentor.firstName,asc";

export default function ManageBookingsPage() {
  const { getToken } = useAuth();
  const { toast } = useToast();

  const [bookings, setBookings] = useState<AdminBooking[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [query, setQuery] = useState("");
  const [paymentStatus, setPaymentStatus] = useState("all");
  const [sessionStatus, setSessionStatus] = useState("all");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [sort, setSort] = useState<SortOption>("sessionAt,desc");

  const [meetingDialogOpen, setMeetingDialogOpen] = useState(false);
  const [selectedBookingId, setSelectedBookingId] = useState<number | null>(null);
  const [meetingLink, setMeetingLink] = useState("");
  const [actionLoading, setActionLoading] = useState<number | null>(null);

  const loadBookings = async () => {
    setLoading(true);
    try {
      const token = await getToken({ template: "skillmentor-auth" });
      if (!token) throw new Error("Authentication token not found.");

      const data = await getAdminBookings(token, {
        page,
        size: 10,
        query,
        paymentStatus,
        sessionStatus,
        fromDate: fromDate || undefined,
        toDate: toDate || undefined,
        sort,
      });

      setBookings(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      toast({
        title: "Failed to load bookings",
        description: err instanceof Error ? err.message : "Unexpected error occurred.",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBookings();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, paymentStatus, sessionStatus, fromDate, toDate, sort]);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    await loadBookings();
  };

  const handleConfirmPayment = async (sessionId: number) => {
    setActionLoading(sessionId);
    try {
      const token = await getToken({ template: "skillmentor-auth" });
      if (!token) throw new Error("Authentication token not found.");
      await confirmBookingPayment(token, sessionId);
      toast({ title: "Payment confirmed", description: `Booking #${sessionId} is now confirmed.` });
      await loadBookings();
    } catch (err) {
      toast({
        title: "Action failed",
        description: err instanceof Error ? err.message : "Unexpected error occurred.",
        variant: "destructive",
      });
    } finally {
      setActionLoading(null);
    }
  };

  const handleMarkComplete = async (sessionId: number) => {
    setActionLoading(sessionId);
    try {
      const token = await getToken({ template: "skillmentor-auth" });
      if (!token) throw new Error("Authentication token not found.");
      await markBookingComplete(token, sessionId);
      toast({ title: "Session completed", description: `Booking #${sessionId} marked as completed.` });
      await loadBookings();
    } catch (err) {
      toast({
        title: "Action failed",
        description: err instanceof Error ? err.message : "Unexpected error occurred.",
        variant: "destructive",
      });
    } finally {
      setActionLoading(null);
    }
  };

  const openMeetingDialog = (booking: AdminBooking) => {
    setSelectedBookingId(booking.id);
    setMeetingLink(booking.meetingLink ?? "");
    setMeetingDialogOpen(true);
  };

  const submitMeetingLink = async () => {
    if (!selectedBookingId) return;
    if (!meetingLink.trim()) {
      toast({ title: "Meeting link required", variant: "destructive" });
      return;
    }

    setActionLoading(selectedBookingId);
    try {
      const token = await getToken({ template: "skillmentor-auth" });
      if (!token) throw new Error("Authentication token not found.");
      await updateBookingMeetingLink(token, selectedBookingId, meetingLink.trim());
      toast({ title: "Meeting link saved" });
      setMeetingDialogOpen(false);
      await loadBookings();
    } catch (err) {
      toast({
        title: "Action failed",
        description: err instanceof Error ? err.message : "Unexpected error occurred.",
        variant: "destructive",
      });
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold">Manage Bookings</h2>

      <form onSubmit={handleSearch} className="grid gap-3 md:grid-cols-2 lg:grid-cols-6">
        <div className="lg:col-span-2">
          <Label htmlFor="search">Search</Label>
          <Input
            id="search"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Student, mentor, or subject"
          />
        </div>
        <div>
          <Label htmlFor="paymentStatus">Payment</Label>
          <select
            id="paymentStatus"
            value={paymentStatus}
            onChange={(e) => setPaymentStatus(e.target.value)}
            className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
          >
            <option value="all">All</option>
            <option value="pending">Pending</option>
            <option value="confirmed">Confirmed</option>
          </select>
        </div>
        <div>
          <Label htmlFor="sessionStatus">Session</Label>
          <select
            id="sessionStatus"
            value={sessionStatus}
            onChange={(e) => setSessionStatus(e.target.value)}
            className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
          >
            <option value="all">All</option>
            <option value="pending">Pending</option>
            <option value="confirmed">Confirmed</option>
            <option value="completed">Completed</option>
          </select>
        </div>
        <div>
          <Label htmlFor="fromDate">From</Label>
          <Input id="fromDate" type="date" value={fromDate} onChange={(e) => setFromDate(e.target.value)} />
        </div>
        <div>
          <Label htmlFor="toDate">To</Label>
          <Input id="toDate" type="date" value={toDate} onChange={(e) => setToDate(e.target.value)} />
        </div>
        <div>
          <Label htmlFor="sort">Sort</Label>
          <select
            id="sort"
            value={sort}
            onChange={(e) => setSort(e.target.value as SortOption)}
            className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
          >
            <option value="sessionAt,desc">Date (Newest)</option>
            <option value="sessionAt,asc">Date (Oldest)</option>
            <option value="student.firstName,asc">Student Name</option>
            <option value="mentor.firstName,asc">Mentor Name</option>
          </select>
        </div>
        <div className="self-end">
          <Button type="submit" className="w-full">
            Apply Filters
          </Button>
        </div>
      </form>

      <div className="overflow-x-auto rounded-md border">
        <table className="min-w-full text-sm">
          <thead className="bg-muted/60">
            <tr>
              <th className="px-3 py-2 text-left">ID</th>
              <th className="px-3 py-2 text-left">Student</th>
              <th className="px-3 py-2 text-left">Mentor</th>
              <th className="px-3 py-2 text-left">Subject</th>
              <th className="px-3 py-2 text-left">Date/Time</th>
              <th className="px-3 py-2 text-left">Duration</th>
              <th className="px-3 py-2 text-left">Payment</th>
              <th className="px-3 py-2 text-left">Session</th>
              <th className="px-3 py-2 text-left">Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={9} className="px-3 py-6 text-center text-muted-foreground">
                  Loading bookings...
                </td>
              </tr>
            ) : bookings.length === 0 ? (
              <tr>
                <td colSpan={9} className="px-3 py-6 text-center text-muted-foreground">
                  No bookings found.
                </td>
              </tr>
            ) : (
              bookings.map((booking) => (
                <tr key={booking.id} className="border-t">
                  <td className="px-3 py-2">#{booking.id}</td>
                  <td className="px-3 py-2">{booking.studentName}</td>
                  <td className="px-3 py-2">{booking.mentorName}</td>
                  <td className="px-3 py-2">{booking.subjectName}</td>
                  <td className="px-3 py-2">{new Date(booking.sessionAt).toLocaleString()}</td>
                  <td className="px-3 py-2">{booking.durationMinutes}m</td>
                  <td className="px-3 py-2">
                    <StatusPill
                      status={
                        booking.paymentStatus === "confirmed" ? "confirmed" : "pending"
                      }
                    />
                  </td>
                  <td className="px-3 py-2">
                    <StatusPill
                      status={
                        booking.sessionStatus === "completed"
                          ? "completed"
                          : booking.sessionStatus === "confirmed"
                            ? "confirmed"
                            : "pending"
                      }
                    />
                  </td>
                  <td className="px-3 py-2">
                    <div className="flex flex-wrap gap-2">
                      <Button
                        size="sm"
                        variant="outline"
                        disabled={booking.paymentStatus !== "pending" || actionLoading === booking.id}
                        onClick={() => handleConfirmPayment(booking.id)}
                      >
                        Confirm Payment
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        disabled={booking.sessionStatus !== "confirmed" || actionLoading === booking.id}
                        onClick={() => handleMarkComplete(booking.id)}
                      >
                        Mark Complete
                      </Button>
                      <Button size="sm" onClick={() => openMeetingDialog(booking)} disabled={actionLoading === booking.id}>
                        Add Meeting Link
                      </Button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-between">
        <Button variant="outline" disabled={page <= 0} onClick={() => setPage((p) => p - 1)}>
          Previous
        </Button>
        <span className="text-sm text-muted-foreground">
          Page {page + 1} of {Math.max(totalPages, 1)}
        </span>
        <Button variant="outline" disabled={page + 1 >= totalPages} onClick={() => setPage((p) => p + 1)}>
          Next
        </Button>
      </div>

      <Dialog open={meetingDialogOpen} onOpenChange={setMeetingDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Update Meeting Link</DialogTitle>
            <DialogDescription>Add or update the meeting link for this booking.</DialogDescription>
          </DialogHeader>
          <div className="space-y-3">
            <Input value={meetingLink} onChange={(e) => setMeetingLink(e.target.value)} placeholder="https://meet.google.com/..." />
            <Button onClick={submitMeetingLink} className="w-full" disabled={selectedBookingId == null}>
              Save Meeting Link
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}

