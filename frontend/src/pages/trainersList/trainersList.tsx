import { useEffect } from "react";
import { useGetAllTrainers } from "../../hooks/api/useGetAllTrainers";
import { TrainersCard } from "../../modules/trainersCard";

const TrainersList = () => {
  const { data: trainers } = useGetAllTrainers();

  useEffect(() => {
    console.log("trainers: ", trainers);
  }, [trainers]);

  return (
    <div>
      {trainers && trainers.length > 0 ? (
        trainers.map((trainer) => (
          <TrainersCard key={trainer.id} trainersItem={trainer} />
        ))
      ) : (
        <div>Тесты не найдены!</div>
      )}
    </div>
  );
};

export default TrainersList;
