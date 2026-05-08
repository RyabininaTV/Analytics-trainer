import { useEffect } from "react";
import { useGetAllTrainers } from "../../hooks/api/useGetAllTrainers";
import { TrainersCard } from "../../modules/trainersCard";
import { Spin } from "antd";
import styles from "./trainersList.module.scss";

const TrainersList = () => {
  const { data: trainers, isFetching, isLoading } = useGetAllTrainers();

  useEffect(() => {
    console.log("trainers: ", trainers);
  }, [trainers]);

  if (isFetching || isLoading)
    return (
      <div className={styles.spinWrapper}>
        <Spin size="large" />
      </div>
    );

  return (
    <div className={styles.trainersList}>
      {trainers && trainers.length > 0 ? (
        trainers.map((trainer) => (
          <TrainersCard key={trainer.id} trainersItem={trainer} />
        ))
      ) : (
        <div>Данные не найдены!</div>
      )}
    </div>
  );
};

export default TrainersList;
