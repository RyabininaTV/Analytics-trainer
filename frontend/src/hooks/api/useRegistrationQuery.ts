import { useMutation } from "@tanstack/react-query";
import { authApi, type RegFieldsType } from "../../api/authApi";
import type { AxiosError } from "axios";
import type { FormInstance } from "antd";
import { useNavigate } from "react-router-dom";

interface ValidationViolation {
  field: string;
  message: string;
}

interface ValidationErrorResponse {
  title: string;
  status: number;
  violations: ValidationViolation[];
}

export interface RegistrationResponse {
  accessToken: string;
  refreshToken: string;
  user: {
    email: string;
    id: number;
    role: string;
    status: string;
    username: string;
  };
}

export function useRegistrationQuery(form: FormInstance<RegFieldsType>) {
  const navigate = useNavigate();

  return useMutation<
    RegistrationResponse,
    AxiosError<ValidationErrorResponse>,
    RegFieldsType
  >({
    mutationFn: (body) => authApi.registration(body).then((res) => res.data),
    onError: (error) => {
      const violations = error.response?.data?.violations;

      if (violations) {
        violations.forEach((violation) => {
          const fieldName = violation.field
            .split(".")
            .pop() as keyof RegFieldsType;

          form.setFields([
            {
              name: fieldName,
              errors: [violation.message],
            },
          ]);
        });
      }
    },
    onSuccess: (data) => {
      localStorage.setItem("user", JSON.stringify(data));

      navigate("/");
    },
  });
}
