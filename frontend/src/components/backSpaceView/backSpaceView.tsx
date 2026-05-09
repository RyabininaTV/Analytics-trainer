import { type FC } from "react";
import type { BackSpaceViewProps } from "./types";
import { Button } from "antd";
import { Link } from "react-router-dom";
import styles from "./backSpaceView.module.scss";

const BackSpaceView: FC<BackSpaceViewProps> = (props) => {
  const { linksList } = props;

  return (
    <div className={styles.backSpaceWrapper}>
      <p>Вернуться на:</p>
      {linksList.map((link) => {
        const url = typeof link.url === "string" ? link.url : link.url(1);

        return (
          <Link key={url} to={url} className={styles.backSpaceLink}>
            <Button color="primary" variant="text">
              <span>{"<"}</span>
              {link.title}
            </Button>
          </Link>
        );
      })}
    </div>
  );
};

export default BackSpaceView;
