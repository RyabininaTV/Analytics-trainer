import axios from "axios";
import { getFromLocalStorage } from "../utils";
import type { RegistrationResponse } from "../hooks/api/useRegistrationQuery";
import { PUBLIC_ENDPOINTS } from "../constants/constants";

const api = axios.create({
  baseURL: "/api",
});

// baseURL: "http://141.8.198.205:8080/",

api.interceptors.request.use((config) => {
  const isPublicEndpoint = PUBLIC_ENDPOINTS.some((endpoint) =>
    config.url?.includes(endpoint),
  );

  if (!isPublicEndpoint) {
    const accessToken =
      getFromLocalStorage<RegistrationResponse>("user")?.access_token;

    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    console.log("originalRequest: ", originalRequest);

    if (error.response?.status === 401 && !originalRequest._retry) {
      try {
        const refreshToken =
          getFromLocalStorage<RegistrationResponse>("user")?.refresh_token;

        const response = await api.post("auth/refresh", {
          refresh_token: refreshToken,
        });
        console.log("response: ", response);
        localStorage.setItem("user", JSON.stringify(response.data));

        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem("user");
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
