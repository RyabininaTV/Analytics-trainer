import type { FC } from "react";
import styles from "./taskEmpty.module.scss";

interface TaskEmptyProps {
  text?: string;
}

export const TaskEmpty: FC<TaskEmptyProps> = (props) => {
  const { text = "Задание еще не добавлено!" } = props;

  return <p className={styles.emptyContent}>{text}</p>;
};
