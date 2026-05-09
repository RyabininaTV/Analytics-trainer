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

export interface TaskDetailResponse {
  auto_check: boolean;
  description: string;
  id: number;
  max_score: number;
  title: string;
  trainer_id: number;
  type: string;
}
