import type { FC } from "react";
import { Link } from "react-router-dom";
import type { TrainersCardProps } from "../../pages/trainersList/types";
import styles from "./trainersCardView.module.scss";
import "./trainersCardView.ant.scss";

const TrainersCardView: FC<TrainersCardProps> = (props) => {
  const { trainersItem } = props;
  return (
    <Link to={`trainers/${trainersItem.id}`} className={styles.trainersCard}>
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
    </Link>
  );
};

export default TrainersCardView;
