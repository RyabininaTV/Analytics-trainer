import axios from "axios";
import { getFromLocalStorage } from "../utils";
import type { RegistrationResponse } from "../hooks/api/useRegistrationQuery";
import { PUBLIC_ENDPOINTS } from "../constants/constants";

const api = axios.create({
  baseURL: "http://localhost:8080/",
});

// baseURL: "http://141.8.198.205:8080/",

api.interceptors.request.use((config) => {
  const isPublicEndpoint = PUBLIC_ENDPOINTS.some((endpoint) =>
    config.url?.includes(endpoint),
  );

  if (!isPublicEndpoint) {
    const accessToken =
      getFromLocalStorage<RegistrationResponse>("user")?.accessToken;

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
          getFromLocalStorage<RegistrationResponse>("user")?.refreshToken;

        const response = await axios.post("auth/refresh", { refreshToken });
        console.log("response: ", response);

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
