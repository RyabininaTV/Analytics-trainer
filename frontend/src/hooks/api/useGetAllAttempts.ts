import { useQuery } from "@tanstack/react-query";
import { attemptsApi } from "../../api/authApi";

export function useGetAllAttempts() {
  return useQuery({
    queryKey: ["getAllAttempts"],
    queryFn: () => attemptsApi.getAllAttempts().then((res) => res.data),
  });
}
