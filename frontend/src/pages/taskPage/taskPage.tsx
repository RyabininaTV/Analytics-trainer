import { useParams } from "react-router-dom";
import { useGetTask } from "../../hooks/api/useGetTask";
import { tasksTypes } from "../../constants/constants";
import styles from "./taskPage.module.scss";
import { useEffect } from "react";
import { SearchErrorTask } from "../../modules/searchErrorTask";
import { TestTask } from "../../modules/testTask";
import { OpenTask } from "../../modules/openTask";

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

      {task?.task_type === "TEST" && (
        <TestTask id={task.id} questionsList={task?.options} />
      )}

      {task?.task_type === "OPEN" && (
        <OpenTask id={task?.id} content={task?.content} />
      )}
      {task?.task_type === "ERROR_FIND" && (
        <SearchErrorTask
          id={task.id}
          content={task?.content}
          questionsList={task?.error_items}
        />
      )}
    </article>
  );
};

export default TaskPage;
