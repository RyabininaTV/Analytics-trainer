import { type FC } from "react";
import { Button } from "antd";
import { Link, useLocation } from "react-router-dom";
import styles from "./backSpaceView.module.scss";
import { backSpaceLinks } from "../../constants/constants";
import { getFromLocalStorage } from "../../utils";

const BackSpaceView: FC = () => {
  const location = useLocation();

  // const linksList = activeBackSpaceInPage["taskPage"];

  const linksList = backSpaceLinks.find((link) =>
    link.matchLink.test(location.pathname),
  )?.goBack;

  const trainerBackId = getFromLocalStorage<number>("trainerId");

  if (!linksList) return null;

  return (
    <div className={styles.backSpaceWrapper}>
      <p>Вернуться на:</p>
      {linksList.map((link) => {
        const url =
          typeof link.url === "string"
            ? link.url
            : trainerBackId
              ? link.url(trainerBackId)
              : "/";

        return (
          <Link key={url} to={url} className={styles.backSpaceLink}>
            <Button color="primary" variant="filled">
              {link.title}
            </Button>
          </Link>
        );
      })}
    </div>
  );
};

export default BackSpaceView;
