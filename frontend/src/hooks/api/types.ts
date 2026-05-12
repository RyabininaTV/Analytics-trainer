export interface TrainerResponse {
  created_at: string;
  description: string;
  difficulty_level: string;
  id: number;
  is_active: boolean;
  title: string;
  updated_at: string;
}

export type AllTrainersResponse = Array<TrainerResponse>;

export interface TaskResponse {
  auto_check_enabled: boolean;
  created_at: string;
  description: string;
  id: number;
  is_active: boolean;
  max_score: number;
  task_type: string;
  title: string;
  trainer_id: number;
  updated_at: string;
}

export type AllTasksResponse = Array<TaskResponse>;

export interface TaskDetailOptions {
  id: number;
  option_text: string;
}

export interface TaskDetailErrorItem {
  id: number;
  fragment_text: string;
}

export interface TaskDetailResponse {
  id: number;
  trainer_id: number;
  trainer_title: string;
  task_type: string;
  title: string;
  description: string;
  max_score: number;
  auto_check_enabled: boolean;
  created_at: string;
  updated_at: string;
  options?: TaskDetailOptions[];
  error_items?: TaskDetailErrorItem[];
  content?: string;
}

export interface SendAnswerBody {
  task_id: number;
  answer: string;
}

export interface SendAnswerResponse {
  attempt_id: number;
  score: number;
  status: "SUBMITTED" | "CHECKED" | "REJECTED";
  task_id: number;
  total_score: number;
  user_id: number;
}

export interface SendAnswerErrorResponse {
  code: string;
  message: string;
}

export interface ProfileResponse {
  id: number;
  email: string;
  username: string;
  role: "USER" | "ADMIN";
  status: "ACTIVE" | "BLOCKED";
  created_at: string;
  updated_at: string;
}

export interface GetAllAttemptsResponse {
  id: number;
  task_id: number;
  started_at: string;
  submitted_at: string;
  status: "SUBMITTED" | "CHECKED" | "REJECTED";
  score: number;
  max_score_snapshot: number;
  is_correct: boolean;
}
