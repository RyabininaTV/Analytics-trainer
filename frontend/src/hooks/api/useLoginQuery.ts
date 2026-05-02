import { useMutation } from "@tanstack/react-query";
import { authApi, type LoginFieldsType } from "../../api/authApi";
import type { AxiosError } from "axios";
import { useNavigate } from "react-router-dom";

export function useLoginQuery() {
  const navigate = useNavigate();

  return useMutation<void, AxiosError<unknown>, LoginFieldsType>({
    mutationFn: (body) => authApi.login(body).then((res) => res.data),
    onSuccess: (data) => {
      localStorage.setItem("user", JSON.stringify(data));
      navigate("/");
    },
  });
}
