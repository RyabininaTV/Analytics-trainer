import type {
  AllTasksResponse,
  AllTrainersResponse,
  GetAllAttemptsResponse,
  ProfileResponse,
  SendAnswerBody,
  SendAnswerResponse,
  TaskDetailResponse,
} from "../hooks/api/types";
import api from "./instance";

export interface RegFieldsType {
  email: string;
  password: string;
  username: string;
}

export type LoginFieldsType = Omit<RegFieldsType, "username">;

export interface LogoutProps {
  refreshToken: string;
}

export const authApi = {
  registration: async (payload: RegFieldsType) =>
    await api.post("auth/register", payload),
  login: async (payload: LoginFieldsType) =>
    await api.post("auth/login", payload),
  logout: async (payload: LogoutProps) =>
    await api.post("auth/logout", payload),
};

export const trainersApi = {
  getAll: async (): Promise<{ data: AllTrainersResponse }> =>
    await api.get("trainers/"),
  getTrainer: async (
    trainerId: number,
  ): Promise<{ data: AllTrainersResponse }> =>
    await api.get(`trainers/${trainerId}`),
  getTrainerTasks: async (
    trainerId: number,
  ): Promise<{ data: AllTasksResponse }> =>
    await api.get(`trainers/${trainerId}/tasks`),
};

export const tasksApi = {
  getTask: async (taskId: number): Promise<{ data: TaskDetailResponse }> =>
    await api.get(`tasks/${taskId}`),
};

export const attemptsApi = {
  sendAnswer: async (
    payload: SendAnswerBody,
  ): Promise<{ data: SendAnswerResponse }> =>
    await api.post("/attempts", payload),
  getAllAttempts: async (): Promise<{ data: GetAllAttemptsResponse[] }> =>
    await api.get("/attempts"),
};

export const profileApi = {
  getProfile: async (): Promise<{ data: ProfileResponse }> =>
    await api.get("/profile"),
};
