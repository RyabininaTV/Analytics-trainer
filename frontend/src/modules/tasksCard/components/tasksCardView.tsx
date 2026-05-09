import type { FC } from "react";
import type { TasksCardProps } from "../types";
import styles from "./tasksCard.module.scss";
import { tasksTypes } from "../../../constants/constants";

export const TrainersCardView: FC<TasksCardProps> = (props) => {
  const { tasksItem } = props;
  return (
    <>
      <p className={styles.title}>{tasksItem.title}</p>
      <p className={styles.description}>{tasksItem.description}</p>
      <div className={styles.meta}>
        <span
          className={tasksItem.is_active ? styles.active : styles.notActive}
        >
          {tasksItem.is_active ? "Активен" : "Не активен"}
        </span>
        <span>{tasksTypes[tasksItem.task_type]}</span>
        <span>Макс. баллов: {tasksItem.max_score}</span>
      </div>
    </>
  );
};
