import { useEffect, useState } from "react";
import { useAuth, useUser } from "@clerk/clerk-react";
import { CalendarDays } from "lucide-react";
import { useNavigate } from "react-router";
import { StatusPill } from "@/components/StatusPill";
import { useToast } from "@/components/hooks/use-toast";
import { getMyEnrollments, submitSessionReview } from "@/lib/api";
import type { Enrollment } from "@/types";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";

export default function DashboardPage() {
  const { isLoaded, isSignedIn, getToken } = useAuth();
  const { user } = useUser();
  const { toast } = useToast();
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [reviewDialogOpen, setReviewDialogOpen] = useState(false);
  const [selectedSessionId, setSelectedSessionId] = useState<number | null>(null);
  const [rating, setRating] = useState(5);
  const [reviewText, setReviewText] = useState("");
  const [submittingReview, setSubmittingReview] = useState(false);
  const router = useNavigate();

  const fetchEnrollments = async () => {
    if (!user) return;
    const token = await getToken({ template: "skillmentor-auth" });
    if (!token) return;
    try {
      const data = await getMyEnrollments(token);
      setEnrollments(data);
    } catch (err) {
      toast({
        title: "Failed to fetch enrollments",
        description: err instanceof Error ? err.message : "Unexpected error occurred.",
        variant: "destructive",
      });
    }
  };

  useEffect(() => {
    if (isLoaded && isSignedIn) {
      fetchEnrollments();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoaded, isSignedIn, getToken, user]);

  const openReviewDialog = (sessionId: number) => {
    setSelectedSessionId(sessionId);
    setRating(5);
    setReviewText("");
    setReviewDialogOpen(true);
  };

  const handleSubmitReview = async () => {
    if (!selectedSessionId) return;
    if (!reviewText.trim()) {
      toast({ title: "Review is required", variant: "destructive" });
      return;
    }

    setSubmittingReview(true);
    try {
      const token = await getToken({ template: "skillmentor-auth" });
      if (!token) throw new Error("Authentication token not found.");
      await submitSessionReview(token, selectedSessionId, {
        studentRating: rating,
        studentReview: reviewText.trim(),
      });
      toast({ title: "Review submitted", description: "Thank you for your feedback." });
      setReviewDialogOpen(false);
      await fetchEnrollments();
    } catch (err) {
      toast({
        title: "Failed to submit review",
        description: err instanceof Error ? err.message : "Unexpected error occurred.",
        variant: "destructive",
      });
    } finally {
      setSubmittingReview(false);
    }
  };

  if (!isLoaded) {
    return (
      <div className="container py-10">
        <div className="flex items-center justify-center">
          <div className="text-lg">Loading...</div>
        </div>
      </div>
    );
  }

  if (!isSignedIn) {
    router("/login");
    return null;
  }

  if (!enrollments.length) {
    return (
      <div className="container py-10">
        <h1 className="text-3xl font-bold tracking-tight mb-6">My Courses</h1>
        <p className="text-muted-foreground">No courses enrolled yet.</p>
      </div>
    );
  }

  return (
    <div className="container py-10">
      <h1 className="text-3xl font-bold tracking-tight mb-6">My Courses</h1>
      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {enrollments.map((enrollment) => (
          <div
            key={enrollment.id}
            className="rounded-2xl p-6 relative overflow-hidden bg-linear-to-br from-blue-500 to-blue-600"
          >
            <div className="absolute top-4 right-4">
              <StatusPill status={enrollment.paymentStatus} />
            </div>

            <div className="size-24 rounded-full bg-white/10 mb-4">
              {enrollment.mentorProfileImageUrl ? (
                <img
                  src={enrollment.mentorProfileImageUrl}
                  alt={enrollment.mentorName}
                  className="w-full h-full rounded-full object-cover object-top"
                />
              ) : (
                <div className="w-full h-full rounded-full flex items-center justify-center text-white text-2xl font-bold">
                  {enrollment.mentorName.charAt(0)}
                </div>
              )}
            </div>

            <div className="space-y-1">
              <h2 className="text-xl font-semibold text-white">{enrollment.subjectName}</h2>
              <p className="text-blue-100/80">Mentor: {enrollment.mentorName}</p>
              <div className="flex items-center text-blue-100/80 text-sm mt-2">
                <CalendarDays className="mr-2 h-4 w-4" />
                Session: {new Date(enrollment.sessionAt).toLocaleString()}
              </div>
              <p className="text-blue-100/80 text-sm capitalize">
                Session status: {enrollment.sessionStatus}
              </p>
            </div>

            <div className="mt-4 space-y-2">
              {enrollment.meetingLink && (
                <a
                  href={enrollment.meetingLink}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-block text-sm underline text-white"
                >
                  Join Meeting
                </a>
              )}
              {enrollment.sessionStatus?.toLowerCase() === "completed" && !enrollment.studentReview && (
                <Button size="sm" variant="secondary" onClick={() => openReviewDialog(enrollment.id)}>
                  Write Review
                </Button>
              )}
              {enrollment.studentReview && (
                <p className="text-xs text-blue-100/80">
                  Your review: {enrollment.studentRating}/5 • {enrollment.studentReview}
                </p>
              )}
            </div>
          </div>
        ))}
      </div>

      <Dialog open={reviewDialogOpen} onOpenChange={setReviewDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Write a Review</DialogTitle>
            <DialogDescription>Share your learning experience for this completed session.</DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="rating">Rating (1-5)</Label>
              <Input
                id="rating"
                type="number"
                min={1}
                max={5}
                value={rating}
                onChange={(e) => setRating(Number(e.target.value))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="review">Review</Label>
              <textarea
                id="review"
                value={reviewText}
                onChange={(e) => setReviewText(e.target.value)}
                className="min-h-28 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                placeholder="Write your feedback..."
              />
            </div>
            <Button className="w-full" onClick={handleSubmitReview} disabled={submittingReview}>
              {submittingReview ? "Submitting..." : "Submit Review"}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}

