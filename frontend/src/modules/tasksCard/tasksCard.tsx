import type { FC } from "react";
import { CardInList } from "../../components/cardInList";
import type { TasksCardProps } from "./types";
import { TrainersCardView } from "./components/tasksCardView";

const TasksCard: FC<TasksCardProps> = (props) => {
  const { tasksItem } = props;

  return (
    <CardInList url={`/tasks/${tasksItem.id}`}>
      <TrainersCardView tasksItem={tasksItem} />
    </CardInList>
  );
};

export default TasksCard;
