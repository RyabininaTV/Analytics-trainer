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
    await api.post("auth/register/", payload),
  login: async (payload: LoginFieldsType) =>
    await api.post("auth/login/", payload),
  logout: async (payload: LogoutProps) =>
    await api.post("auth/logout/", payload),
};
