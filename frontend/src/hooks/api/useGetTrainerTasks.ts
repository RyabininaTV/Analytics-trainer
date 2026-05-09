import { useSuspenseQuery } from "@tanstack/react-query";
import { trainersApi } from "../../api/authApi";

export function useGetTrainerTasks(trainerId: number | undefined) {
  return useSuspenseQuery({
    queryKey: ["trainers", trainerId, "tasks"],
    queryFn: () =>
      trainerId
        ? trainersApi.getTrainerTasks(trainerId).then((res) => res.data)
        : undefined,
  });
}
