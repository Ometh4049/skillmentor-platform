import { useMemo, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { createMentor } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/components/hooks/use-toast";
import { ShieldCheck } from "lucide-react";
import { Link } from "react-router";

const initialForm = {
  mentorId: "",
  firstName: "",
  lastName: "",
  email: "",
  phoneNumber: "",
  title: "",
  profession: "",
  company: "",
  experienceYears: 0,
  bio: "",
  profileImageUrl: "",
  positiveReviews: 95,
  totalEnrollments: 0,
  isCertified: false,
  startYear: String(new Date().getFullYear()),
};

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function CreateMentorPage() {
  const { getToken } = useAuth();
  const { toast } = useToast();
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState(initialForm);
  const [createdMentorId, setCreatedMentorId] = useState<string | null>(null);

  const mentorName = useMemo(() => `${form.firstName} ${form.lastName}`.trim(), [form.firstName, form.lastName]);

  const validateForm = () => {
    if (!form.mentorId.trim()) return "Mentor ID is required (Clerk user ID recommended).";
    if (!form.firstName.trim()) return "First name is required.";
    if (!form.lastName.trim()) return "Last name is required.";
    if (!form.email.trim() || !emailRegex.test(form.email)) return "A valid email is required.";
    if (!form.startYear.trim()) return "Start year is required.";
    return null;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const error = validateForm();
    if (error) {
      toast({ title: "Validation error", description: error, variant: "destructive" });
      return;
    }

    setSubmitting(true);
    try {
      const token = await getToken({ template: "skillmentor-auth" });
      if (!token) throw new Error("Authentication token not found.");

      const created = await createMentor(token, {
        ...form,
        experienceYears: Number(form.experienceYears || 0),
        positiveReviews: Number(form.positiveReviews || 0),
        totalEnrollments: Number(form.totalEnrollments || 0),
      });
      setCreatedMentorId(created?.mentorId ?? form.mentorId);

      toast({
        title: "Mentor created",
        description: "Mentor profile has been successfully created.",
      });
      setForm(initialForm);
    } catch (err) {
      toast({
        title: "Failed to create mentor",
        description: err instanceof Error ? err.message : "Unexpected error occurred.",
        variant: "destructive",
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
      <form className="space-y-4" onSubmit={handleSubmit}>
        <h2 className="text-2xl font-semibold">Create Mentor</h2>
        {createdMentorId && (
          <div className="rounded-md border border-green-200 bg-green-50 p-3 text-sm text-green-800">
            Mentor created successfully.{" "}
            <Link to={`/mentors/${createdMentorId}`} className="font-medium underline">
              View mentor profile
            </Link>
          </div>
        )}
        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <Label htmlFor="mentorId">Mentor ID</Label>
            <Input id="mentorId" value={form.mentorId} onChange={(e) => setForm((p) => ({ ...p, mentorId: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input id="email" type="email" value={form.email} onChange={(e) => setForm((p) => ({ ...p, email: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="firstName">First Name</Label>
            <Input id="firstName" value={form.firstName} onChange={(e) => setForm((p) => ({ ...p, firstName: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="lastName">Last Name</Label>
            <Input id="lastName" value={form.lastName} onChange={(e) => setForm((p) => ({ ...p, lastName: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="phone">Phone Number</Label>
            <Input id="phone" value={form.phoneNumber} onChange={(e) => setForm((p) => ({ ...p, phoneNumber: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="title">Title</Label>
            <Input id="title" value={form.title} onChange={(e) => setForm((p) => ({ ...p, title: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="profession">Profession</Label>
            <Input id="profession" value={form.profession} onChange={(e) => setForm((p) => ({ ...p, profession: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="company">Company</Label>
            <Input id="company" value={form.company} onChange={(e) => setForm((p) => ({ ...p, company: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="experienceYears">Experience Years</Label>
            <Input
              id="experienceYears"
              type="number"
              min={0}
              value={form.experienceYears}
              onChange={(e) => setForm((p) => ({ ...p, experienceYears: Number(e.target.value) }))}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="startYear">Start Year</Label>
            <Input id="startYear" value={form.startYear} onChange={(e) => setForm((p) => ({ ...p, startYear: e.target.value }))} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="profileImageUrl">Profile Image URL</Label>
            <Input
              id="profileImageUrl"
              value={form.profileImageUrl}
              onChange={(e) => setForm((p) => ({ ...p, profileImageUrl: e.target.value }))}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="positiveReviews">Positive Reviews (%)</Label>
            <Input
              id="positiveReviews"
              type="number"
              min={0}
              max={100}
              value={form.positiveReviews}
              onChange={(e) => setForm((p) => ({ ...p, positiveReviews: Number(e.target.value) }))}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="totalEnrollments">Total Enrollments</Label>
            <Input
              id="totalEnrollments"
              type="number"
              min={0}
              value={form.totalEnrollments}
              onChange={(e) => setForm((p) => ({ ...p, totalEnrollments: Number(e.target.value) }))}
            />
          </div>
        </div>
        <div className="space-y-2">
          <Label htmlFor="bio">Bio</Label>
          <textarea
            id="bio"
            value={form.bio}
            onChange={(e) => setForm((p) => ({ ...p, bio: e.target.value }))}
            className="min-h-28 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
          />
        </div>
        <label className="flex items-center gap-2 text-sm">
          <input
            type="checkbox"
            checked={form.isCertified}
            onChange={(e) => setForm((p) => ({ ...p, isCertified: e.target.checked }))}
          />
          Certified mentor
        </label>
        <Button type="submit" disabled={submitting}>
          {submitting ? "Creating..." : "Create Mentor"}
        </Button>
      </form>

      <div className="space-y-3">
        <h3 className="text-lg font-semibold">Preview</h3>
        <Card className="p-4 space-y-3">
          <div className="flex items-center gap-3">
            {form.profileImageUrl ? (
              <img src={form.profileImageUrl} alt={mentorName || "Mentor"} className="size-12 rounded-full object-cover object-top" />
            ) : (
              <div className="size-12 rounded-full bg-muted flex items-center justify-center text-lg font-semibold">
                {(form.firstName || "M").charAt(0)}
              </div>
            )}
            <div>
              <h4 className="font-semibold">{mentorName || "Mentor Name"}</h4>
              <p className="text-sm text-muted-foreground">{form.title || "Mentor Title"}</p>
            </div>
          </div>
          <p className="text-sm">{form.bio || "Mentor bio preview..."}</p>
          <p className="text-sm text-muted-foreground">
            {form.company || "Company"} • {form.profession || "Profession"}
          </p>
          <p className="text-sm text-muted-foreground">Tutor since {form.startYear || "-"}</p>
          {form.isCertified && (
            <div className="inline-flex items-center gap-1 rounded-full bg-green-100 px-2 py-1 text-xs text-green-800">
              <ShieldCheck className="size-3" />
              Certified
            </div>
          )}
        </Card>
      </div>
    </div>
  );
}
