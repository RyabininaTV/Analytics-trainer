import { type FC } from "react";
import { TrainersCardView } from "../../components/trainersCardView";
import type { TrainersCardProps } from "../../pages/trainersList/types";

const TrainersCard: FC<TrainersCardProps> = (props) => {
  const { trainersItem } = props;

  return <TrainersCardView key={trainersItem.id} trainersItem={trainersItem} />;
};

export default TrainersCard;
