import type { TaskDetailErrorItem } from "../../hooks/api/types";

export interface SearchErrorTaskProps {
  id: number;
  content: string | undefined;
  questionsList: TaskDetailErrorItem[] | undefined;
}
