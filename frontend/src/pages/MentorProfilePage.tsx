import { useEffect, useState } from "react";
import { Link, useParams } from "react-router";
import { useAuth } from "@clerk/clerk-react";
import type { MentorProfile } from "@/types";
import { getMentorProfile } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { SchedulingModal } from "@/components/SchedulingModel";
import { SignupDialog } from "@/components/SignUpDialog";

export default function MentorProfilePage() {
  const { mentorId } = useParams();
  const { isSignedIn } = useAuth();
  const [profile, setProfile] = useState<MentorProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSignupDialogOpen, setIsSignupDialogOpen] = useState(false);
  const [isSchedulingModalOpen, setIsSchedulingModalOpen] = useState(false);
  const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(null);

  useEffect(() => {
    if (!mentorId) return;
    getMentorProfile(mentorId)
      .then((data) => setProfile(data))
      .catch((err) => setError(err instanceof Error ? err.message : "Failed to load mentor profile."))
      .finally(() => setLoading(false));
  }, [mentorId]);

  const handleBookSubject = (subjectId: number) => {
    if (!isSignedIn) {
      setIsSignupDialogOpen(true);
      return;
    }
    setSelectedSubjectId(subjectId);
    setIsSchedulingModalOpen(true);
  };

  if (loading) {
    return <div className="container py-10 text-muted-foreground">Loading mentor profile...</div>;
  }

  if (error || !profile) {
    return (
      <div className="container py-10 space-y-3">
        <p className="text-destructive">{error ?? "Mentor profile not found."}</p>
        <Link to="/">
          <Button variant="outline">Back to Mentors</Button>
        </Link>
      </div>
    );
  }

  const mentorName = `${profile.firstName} ${profile.lastName}`;

  return (
    <div className="container py-10 space-y-8">
      <Link to="/" className="text-sm text-muted-foreground hover:underline">
        ← Back to Mentors
      </Link>

      <section className="grid gap-6 md:grid-cols-[220px_1fr_auto] items-start">
        <div className="rounded-2xl overflow-hidden border">
          {profile.profileImageUrl ? (
            <img src={profile.profileImageUrl} alt={mentorName} className="h-56 w-full object-cover object-top" />
          ) : (
            <div className="h-56 w-full flex items-center justify-center bg-muted text-4xl font-bold">
              {profile.firstName.charAt(0)}
            </div>
          )}
        </div>
        <div className="space-y-2">
          <h1 className="text-4xl font-bold">{mentorName}</h1>
          <p className="text-lg">{profile.title}</p>
          <p className="text-muted-foreground">
            {profile.profession} • {profile.company}
          </p>
          <p className="text-sm text-muted-foreground">Since {profile.startYear}</p>
          <div className="flex flex-wrap gap-2 text-sm">
            {profile.isCertified && (
              <span className="rounded-full bg-green-100 px-3 py-1 text-green-800">Certified Mentor</span>
            )}
            <span className="rounded-full bg-yellow-100 px-3 py-1 text-yellow-800">
              {profile.positiveReviews}% positive reviews
            </span>
            <span className="rounded-full bg-blue-100 px-3 py-1 text-blue-800">
              {profile.averageRating ? `${profile.averageRating}/5` : "No ratings yet"} ({profile.reviewsCount} reviews)
            </span>
          </div>
        </div>
        <Button onClick={() => handleBookSubject(profile.subjects[0]?.id ?? 0)} disabled={!profile.subjects.length}>
          Schedule Session
        </Button>
      </section>

      <section>
        <h2 className="text-2xl font-semibold mb-2">About</h2>
        <p className="text-muted-foreground">{profile.bio}</p>
      </section>

      <section>
        <h2 className="text-2xl font-semibold mb-4">Statistics</h2>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <Card className="p-4">
            <p className="text-sm text-muted-foreground">Students Taught</p>
            <p className="text-2xl font-bold">{profile.totalEnrollments ?? profile.sessionsCount}</p>
          </Card>
          <Card className="p-4">
            <p className="text-sm text-muted-foreground">Experience</p>
            <p className="text-2xl font-bold">{profile.experienceYears} years</p>
          </Card>
          <Card className="p-4">
            <p className="text-sm text-muted-foreground">Subjects</p>
            <p className="text-2xl font-bold">{profile.subjectsCount}</p>
          </Card>
          <Card className="p-4">
            <p className="text-sm text-muted-foreground">Average Rating</p>
            <p className="text-2xl font-bold">{profile.averageRating ?? "-"}</p>
          </Card>
        </div>
      </section>

      <section>
        <h2 className="text-2xl font-semibold mb-4">Subjects Taught</h2>
        <div className="grid gap-4 md:grid-cols-2">
          {profile.subjects.map((subject) => (
            <Card key={subject.id} className="p-4 space-y-3">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <h3 className="text-lg font-semibold">{subject.subjectName}</h3>
                  <p className="text-sm text-muted-foreground">{subject.description}</p>
                </div>
                {subject.courseImageUrl && (
                  <img src={subject.courseImageUrl} alt={subject.subjectName} className="size-16 rounded-md object-cover" />
                )}
              </div>
              <p className="text-sm text-muted-foreground">{subject.enrollmentCount ?? 0} students enrolled</p>
              <Button onClick={() => handleBookSubject(subject.id)}>Book This Subject</Button>
            </Card>
          ))}
        </div>
      </section>

      <section>
        <h2 className="text-2xl font-semibold mb-4">Recent Reviews</h2>
        {!profile.reviews.length ? (
          <p className="text-muted-foreground">No reviews yet.</p>
        ) : (
          <div className="space-y-3">
            {profile.reviews.map((review) => (
              <Card key={review.sessionId} className="p-4">
                <div className="flex items-center justify-between">
                  <p className="font-medium">{review.studentName}</p>
                  <p className="text-sm text-muted-foreground">{new Date(review.sessionAt).toLocaleDateString()}</p>
                </div>
                <p className="text-sm text-yellow-700">{`Rating: ${review.rating}/5`}</p>
                <p className="text-sm text-muted-foreground">{review.review}</p>
              </Card>
            ))}
          </div>
        )}
      </section>

      <SignupDialog isOpen={isSignupDialogOpen} onClose={() => setIsSignupDialogOpen(false)} />
      <SchedulingModal
        isOpen={isSchedulingModalOpen}
        onClose={() => setIsSchedulingModalOpen(false)}
        mentor={profile}
        initialSubjectId={selectedSubjectId ?? undefined}
      />
    </div>
  );
}

