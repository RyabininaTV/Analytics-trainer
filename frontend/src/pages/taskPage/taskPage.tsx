import { useParams } from "react-router-dom";
import { useGetTask } from "../../hooks/api/useGetTask";
import { tasksTypes } from "../../constants/constants";
import styles from "./taskPage.module.scss";

const TaskPage = () => {
  const { taskId } = useParams();

  const { data: task } = useGetTask(Number(taskId));

  return (
    <article className={styles.taskPage}>
      <h3>{task?.title}</h3>
      <div className={styles.subtitleInfo}>
        <span>
          <span className={styles.infoDescription}>Макс. балл: </span>
          {task?.max_score}
        </span>
        {task?.type && (
          <span>
            <span className={styles.infoDescription}>Тип задания: </span>
            {tasksTypes[task?.type]}
          </span>
        )}
      </div>
      <p className={styles.description}>{task?.description}</p>
    </article>
  );
};

export default TaskPage;
