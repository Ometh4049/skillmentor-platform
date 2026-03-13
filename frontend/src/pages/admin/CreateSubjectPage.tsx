import { useEffect, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { createSubject, getPublicMentors } from "@/lib/api";
import type { Mentor } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/components/hooks/use-toast";
import { useNavigate } from "react-router";

const initialForm = {
  subjectName: "",
  description: "",
  courseImageUrl: "",
  mentorId: "",
};

export default function CreateSubjectPage() {
  const { getToken } = useAuth();
  const { toast } = useToast();
  const navigate = useNavigate();
  const [mentors, setMentors] = useState<Mentor[]>([]);
  const [loadingMentors, setLoadingMentors] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState(initialForm);

  useEffect(() => {
    getPublicMentors(0, 100)
      .then((data) => setMentors(data.content))
      .catch(() => {
        toast({
          title: "Failed to load mentors",
          description: "Please refresh the page and try again.",
          variant: "destructive",
        });
      })
      .finally(() => setLoadingMentors(false));
  }, [toast]);

  const validateForm = () => {
    if (form.subjectName.trim().length < 5) {
      return "Subject name must be at least 5 characters.";
    }
    if (!form.description.trim()) {
      return "Description is required.";
    }
    if (!form.mentorId) {
      return "Please select a mentor.";
    }
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
      await createSubject(token, form);
      toast({
        title: "Subject created",
        description: "The subject has been successfully created.",
      });
      setForm(initialForm);
      navigate("/admin/subjects");
    } catch (err) {
      toast({
        title: "Failed to create subject",
        description: err instanceof Error ? err.message : "Unexpected error occurred.",
        variant: "destructive",
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold">Create Subject</h2>
      <form className="space-y-4 max-w-xl" onSubmit={handleSubmit}>
        <div className="space-y-2">
          <Label htmlFor="subjectName">Subject Name</Label>
          <Input
            id="subjectName"
            value={form.subjectName}
            onChange={(e) => setForm((prev) => ({ ...prev, subjectName: e.target.value }))}
            placeholder="e.g. AWS Developer Associate Prep"
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="description">Description</Label>
          <textarea
            id="description"
            value={form.description}
            onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))}
            className="min-h-28 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            placeholder="Describe this subject in a few sentences"
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="courseImageUrl">Course Image URL</Label>
          <Input
            id="courseImageUrl"
            value={form.courseImageUrl}
            onChange={(e) => setForm((prev) => ({ ...prev, courseImageUrl: e.target.value }))}
            placeholder="https://..."
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="mentorId">Mentor</Label>
          <select
            id="mentorId"
            value={form.mentorId}
            onChange={(e) => setForm((prev) => ({ ...prev, mentorId: e.target.value }))}
            className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
            disabled={loadingMentors}
          >
            <option value="">Select a mentor</option>
            {mentors.map((mentor) => (
              <option key={mentor.id} value={mentor.mentorId}>
                {mentor.firstName} {mentor.lastName} ({mentor.mentorId})
              </option>
            ))}
          </select>
        </div>
        <Button type="submit" disabled={submitting}>
          {submitting ? "Creating..." : "Create Subject"}
        </Button>
      </form>
    </div>
  );
}
