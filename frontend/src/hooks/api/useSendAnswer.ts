import { useMutation } from "@tanstack/react-query";
import { attemptsApi } from "../../api/authApi";
import type {
  SendAnswerBody,
  SendAnswerErrorResponse,
  SendAnswerResponse,
} from "./types";
import type { AxiosError } from "axios";

export function useSendAnswer() {
  return useMutation<
    SendAnswerResponse,
    AxiosError<SendAnswerErrorResponse>,
    SendAnswerBody
  >({
    mutationFn: (body) => attemptsApi.sendAnswer(body).then((res) => res.data),
  });
}
