import { useEffect } from "react";
import { useParams } from "react-router-dom";
import { useGetTrainerTasks } from "../../hooks/api/useGetTrainerTasks";
import { TasksCard } from "../../modules/tasksCard";

const TrainerTasksList = () => {
  const { trainerId } = useParams();

  const { data: tasks } = useGetTrainerTasks(Number(trainerId));

  useEffect(() => {
    console.log("trainerId: ", trainerId);
  }, [trainerId]);
  useEffect(() => {
    console.log("tasks: ", tasks);
  }, [tasks]);

  return (
    <div>
      {tasks && tasks.length > 0 ? (
        tasks.map((task) => <TasksCard key={task.id} tasksItem={task} />)
      ) : (
        <div>Тесты не найдены!</div>
      )}
    </div>
  );
};

export default TrainerTasksList;
