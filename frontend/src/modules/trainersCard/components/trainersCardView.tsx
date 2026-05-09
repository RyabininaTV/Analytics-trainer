import type { FC } from "react";
import styles from "./trainersCardView.module.scss";
import type { TrainersCardProps } from "../types";

export const TrainersCardView: FC<TrainersCardProps> = (props) => {
  const { trainersItem } = props;
  return (
    <>
      <p className={styles.title}>{trainersItem.title}</p>
      <p className={styles.description}>{trainersItem.description}</p>
      <div className={styles.meta}>
        <span
          className={trainersItem.is_active ? styles.active : styles.notActive}
        >
          {trainersItem.is_active ? "Активен" : "Не активен"}
        </span>
        <span className={styles[trainersItem.difficulty_level]}>
          {trainersItem.difficulty_level}
        </span>
      </div>
    </>
  );
};
