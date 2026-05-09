import { type FC } from "react";
import { CardInList } from "../../components/cardInList";
import type { TrainersCardProps } from "./types";
import { TrainersCardView } from "./components/trainersCardView";

const TrainersCard: FC<TrainersCardProps> = (props) => {
  const { trainersItem } = props;

  // здесь будет дополнительная бизнес логика

  return (
    <CardInList url={`/trainers/${trainersItem.id}/tasks`}>
      <TrainersCardView trainersItem={trainersItem} />
    </CardInList>
  );
};

export default TrainersCard;
