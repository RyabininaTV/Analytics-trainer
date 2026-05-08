import { useQuery } from "@tanstack/react-query";
import { trainersApi } from "../../api/authApi";

export function useGetAllTrainers() {
  return useQuery({
    queryKey: ["trainers"],
    queryFn: () => trainersApi.getAll().then((res) => res.data),
    staleTime: 5 * 60 * 1000,
  });
}
