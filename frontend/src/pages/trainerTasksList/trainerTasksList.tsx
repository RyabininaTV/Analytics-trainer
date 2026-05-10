import { useParams } from "react-router-dom";
import { useGetTrainerTasks } from "../../hooks/api/useGetTrainerTasks";
import { TasksCard } from "../../modules/tasksCard";

const TrainerTasksList = () => {
  const { trainerId } = useParams();

  const { data: tasks } = useGetTrainerTasks(Number(trainerId));

  localStorage.setItem("trainerId", JSON.stringify(trainerId));

  return (
    <div>
      <h2>Задачи</h2>
      {tasks && tasks.length > 0 ? (
        tasks.map((task) => <TasksCard key={task.id} tasksItem={task} />)
      ) : (
        <div>Тесты не найдены!</div>
      )}
    </div>
  );
};

export default TrainerTasksList;
