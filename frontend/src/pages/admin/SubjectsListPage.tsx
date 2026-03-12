import { useEffect, useState } from "react";
import { Link } from "react-router";
import { useAuth } from "@clerk/clerk-react";
import type { Subject } from "@/types";
import { getAllSubjects } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { useToast } from "@/components/hooks/use-toast";

export default function SubjectsListPage() {
  const { getToken } = useAuth();
  const { toast } = useToast();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadSubjects() {
      try {
        const token = await getToken({ template: "skillmentor-auth" });
        if (!token) throw new Error("Authentication token not found.");
        const data = await getAllSubjects(token);
        setSubjects(data);
      } catch (err) {
        toast({
          title: "Failed to load subjects",
          description: err instanceof Error ? err.message : "Unexpected error occurred.",
          variant: "destructive",
        });
      } finally {
        setLoading(false);
      }
    }

    loadSubjects();
  }, [getToken, toast]);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-2xl font-semibold">Subjects</h2>
        <Link to="/admin/subjects/create">
          <Button>Create Subject</Button>
        </Link>
      </div>

      <div className="overflow-x-auto rounded-md border">
        <table className="min-w-full text-sm">
          <thead className="bg-muted/60">
            <tr>
              <th className="px-3 py-2 text-left">Subject</th>
              <th className="px-3 py-2 text-left">Description</th>
              <th className="px-3 py-2 text-left">Image</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={3} className="px-3 py-6 text-center text-muted-foreground">
                  Loading subjects...
                </td>
              </tr>
            ) : subjects.length === 0 ? (
              <tr>
                <td colSpan={3} className="px-3 py-6 text-center text-muted-foreground">
                  No subjects available yet.
                </td>
              </tr>
            ) : (
              subjects.map((subject) => (
                <tr key={subject.id} className="border-t">
                  <td className="px-3 py-2 font-medium">{subject.subjectName}</td>
                  <td className="px-3 py-2 text-muted-foreground">{subject.description}</td>
                  <td className="px-3 py-2">
                    {subject.courseImageUrl ? (
                      <img
                        src={subject.courseImageUrl}
                        alt={subject.subjectName}
                        className="h-10 w-16 rounded object-cover"
                      />
                    ) : (
                      <span className="text-muted-foreground">-</span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

