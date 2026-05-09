import { type FC } from "react";
import { Link } from "react-router-dom";
import styles from "./cardInList.module.scss";
import "./cardInList.ant.scss";
import type { CardInListProps } from "./types";

const CardInList: FC<CardInListProps> = (props) => {
  const { url, children } = props;
  return (
    <Link
      to={url}
      className={[styles.trainersCard, "trainersCardView"].join(" ")}
    >
      {children}
    </Link>
  );
};

export default CardInList;
