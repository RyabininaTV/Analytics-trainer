import { useParams } from "react-router-dom";
import { useGetTask } from "../../hooks/api/useGetTask";
import { tasksTypes } from "../../constants/constants";
import styles from "./taskPage.module.scss";
import { useEffect } from "react";
import { OpenTaskContent } from "../../components/openTaskContent";
import { SearchErrorTask } from "../../components/searchErrorTask";

const TaskPage = () => {
  const { taskId } = useParams();

  const { data: task } = useGetTask(Number(taskId));

  useEffect(() => {
    console.log("task: ", task);
  }, [task]);

  return (
    <article className={styles.taskPage}>
      <h3>{task?.title}</h3>
      <div className={styles.subtitleInfo}>
        <span>
          <span className={styles.infoDescription}>Макс. балл: </span>
          {task?.max_score}
        </span>
        {task?.task_type && (
          <span>
            <span className={styles.infoDescription}>Тип задания: </span>
            {tasksTypes[task?.task_type]}
          </span>
        )}
      </div>
      <p className={styles.description}>{task?.description}</p>
      {task?.task_type === "OPEN" && (
        <OpenTaskContent content={task?.content} />
      )}
      {task?.task_type === "ERROR_FIND" && (
        <SearchErrorTask
          content={task?.content}
          questionsList={task?.error_items}
        />
      )}
    </article>
  );
};

export default TaskPage;
