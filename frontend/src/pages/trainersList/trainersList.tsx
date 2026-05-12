import { useGetAllTrainers } from "../../hooks/api/useGetAllTrainers";
import { TrainersCard } from "../../modules/trainersCard";

const TrainersList = () => {
  const { data: trainers } = useGetAllTrainers();

  return (
    <article>
      <h2>Тренажеры</h2>
      {trainers && trainers.length > 0 ? (
        trainers.map((trainer) => (
          <TrainersCard key={trainer.id} trainersItem={trainer} />
        ))
      ) : (
        <div>Тесты не найдены!</div>
      )}
    </article>
  );
};

export default TrainersList;
