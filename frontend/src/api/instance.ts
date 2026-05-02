import axios from "axios";
import { getFromLocalStorage } from "../utils";
import type { RegistrationResponse } from "../hooks/api/useRegistrationQuery";

const PUBLIC_ENDPOINTS = ["auth/login", "auth/register"];

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

export default api;
