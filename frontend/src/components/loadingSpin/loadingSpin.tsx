import { Spin } from "antd";
import styles from "./loadingSpin.module.scss";
import { type FC } from "react";

const LoadingSpin: FC = () => {
  return (
    <div className={styles.spinWrapper}>
      <Spin size="large" />
    </div>
  );
};

export default LoadingSpin;
