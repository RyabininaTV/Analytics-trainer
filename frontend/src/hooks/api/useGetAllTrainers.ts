import { useSuspenseQuery } from "@tanstack/react-query";
import { trainersApi } from "../../api/authApi";

export function useGetAllTrainers() {
  return useSuspenseQuery({
    queryKey: ["trainers"],
    queryFn: () => trainersApi.getAll().then((res) => res.data),
  });
}
