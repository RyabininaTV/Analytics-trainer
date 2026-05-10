import type { TaskDetailErrorItem } from "../../hooks/api/types";

export interface SearchErrorTaskProps {
  content: string | undefined;
  questionsList: TaskDetailErrorItem[];
}
