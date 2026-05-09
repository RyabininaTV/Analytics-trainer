import { useSuspenseQuery } from "@tanstack/react-query";
import { trainersApi } from "../../api/authApi";

export function useGetTrainer(trainerId: number | undefined) {
  return useSuspenseQuery({
    queryKey: ["trainers", trainerId],
    queryFn: () =>
      trainerId
        ? trainersApi.getTrainer(trainerId).then((res) => res.data)
        : undefined,
  });
}
