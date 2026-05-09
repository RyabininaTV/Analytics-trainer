import { useSuspenseQuery } from "@tanstack/react-query";
import { tasksApi } from "../../api/authApi";

export function useGetTask(taskId: number | undefined) {
  return useSuspenseQuery({
    queryKey: ["task", taskId],
    queryFn: () =>
      taskId ? tasksApi.getTask(taskId).then((res) => res.data) : undefined,
  });
}
