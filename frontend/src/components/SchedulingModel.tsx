import { useEffect, useMemo, useState } from "react";
import { Calendar } from "./ui/calendar";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "./ui/dialog";
import { useNavigate } from "react-router";
import type { Mentor } from "@/types";
import { useToast } from "./hooks/use-toast";

interface SchedulingModalProps {
  isOpen: boolean;
  onClose: () => void;
  mentor: Mentor;
  initialSubjectId?: number;
}

const TIME_SLOTS = [
  "09:00",
  "10:00",
  "11:00",
  "12:00",
  "13:00",
  "14:00",
  "15:00",
  "16:00",
];

export function SchedulingModal({
  isOpen,
  onClose,
  mentor,
  initialSubjectId,
}: SchedulingModalProps) {
  const [date, setDate] = useState<Date>();
  const [selectedTime, setSelectedTime] = useState<string>();
  const [selectedSubjectId, setSelectedSubjectId] = useState<number | undefined>(
    initialSubjectId ?? mentor.subjects[0]?.id,
  );
  const navigate = useNavigate();
  const { toast } = useToast();

  const mentorName = `${mentor.firstName} ${mentor.lastName}`;
  const today = useMemo(() => {
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    return now;
  }, []);
  const subject =
    mentor.subjects.find((item) => item.id === selectedSubjectId) ??
    mentor.subjects[0];

  useEffect(() => {
    if (!isOpen) return;
    setDate(undefined);
    setSelectedTime(undefined);
    setSelectedSubjectId(initialSubjectId ?? mentor.subjects[0]?.id);
  }, [initialSubjectId, isOpen, mentor.subjects]);

  const handleSchedule = () => {
    if (date && selectedTime && subject) {
      const sessionDateTime = new Date(date);
      const [hours, minutes] = selectedTime.split(":");
      sessionDateTime.setHours(
        Number.parseInt(hours),
        Number.parseInt(minutes),
      );

      if (sessionDateTime < new Date()) {
        toast({
          title: "Invalid date/time",
          description: "Please pick a future session date and time.",
          variant: "destructive",
        });
        return;
      }

      const sessionId = `${mentor.id}-${Date.now()}`;
      const searchParams = new URLSearchParams({
        date: sessionDateTime.toISOString(),
        courseTitle: subject.subjectName ?? "",
        mentorName: mentorName,
        mentorId: mentor.mentorId,
        mentorImg: mentor.profileImageUrl ?? "",
        subjectId: String(subject.id ?? ""),
      });
      navigate(`/payment/${sessionId}?${searchParams.toString()}`);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
        <DialogHeader className="flex flex-row items-center justify-center space-y-0">
          <DialogTitle>Schedule this session</DialogTitle>
          <DialogDescription className="sr-only">
            Pick a date and time for your mentoring session with {mentorName}.
          </DialogDescription>
        </DialogHeader>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h4 className="font-medium mb-2">Subject</h4>
            <select
              className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm mb-4"
              value={selectedSubjectId}
              onChange={(e) => setSelectedSubjectId(Number(e.target.value))}
            >
              {mentor.subjects.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.subjectName}
                </option>
              ))}
            </select>
            <h4 className="font-medium mb-2">Choose a date</h4>
            <Calendar
              mode="single"
              selected={date}
              onSelect={setDate}
              disabled={{ before: today }}
              className="rounded-md border"
            />
          </div>
          <div>
            <h4 className="font-medium mb-2">Choose a time</h4>
            <div className="grid grid-cols-2 gap-2">
              {TIME_SLOTS.map((time) => (
                <Button
                  key={time}
                  variant={selectedTime === time ? "default" : "outline"}
                  className="w-full"
                  onClick={() => setSelectedTime(time)}
                >
                  {time}
                </Button>
              ))}
            </div>
          </div>
        </div>
        <div className="flex justify-end gap-2 mt-6">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleSchedule} disabled={!date || !selectedTime || !subject}>
            Save
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
