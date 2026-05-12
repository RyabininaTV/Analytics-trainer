import type { FC } from "react";
import styles from "./taskExplanation.module.scss";

interface TaskExplanationProps {
  content: React.ReactNode;
}

export const TaskExplanation: FC<TaskExplanationProps> = (props) => {
  const { content } = props;

  return (
    <div className={styles.contentWrapper}>
      <p>Пояснение:</p>
      <p>{content}</p>
    </div>
  );
};
