import { useMutation } from "@tanstack/react-query";
import { authApi } from "../../api/authApi";
import type { RegistrationResponse } from "./useRegistrationQuery";
import { getFromLocalStorage } from "../../utils";

export function useLogoutQuery() {
  return useMutation({
    mutationFn: () => {
      const user = getFromLocalStorage<RegistrationResponse>("user");

      if (!user) {
        return Promise.reject(new Error("No refresh token"));
      }

      return authApi.logout({ refreshToken: user?.refresh_token });
    },
    onSuccess: () => {
      localStorage.removeItem("user");
    },
    onError: (error) => {
      console.error("error: ", error);
    },
  });
}
