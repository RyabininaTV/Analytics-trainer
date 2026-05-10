import { useEffect, type FC } from "react";
import type { SearchErrorTaskProps } from "./types";
import styles from "./searchErrorTask.module.scss";

const SearchErrorTask: FC<SearchErrorTaskProps> = (props) => {
  const { content, questionsList } = props;

  useEffect(() => {
    console.log("questionsList: ", questionsList);
  }, [questionsList]);

  return (
    <section>
      <div className={styles.contentWrapper}>
        <p>Пояснение:</p>
        <p>{content}</p>
      </div>
    </section>
  );
};

export default SearchErrorTask;
