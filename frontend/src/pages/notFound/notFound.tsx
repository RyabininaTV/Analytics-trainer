import { Button, Col } from "antd";
import { Link } from "react-router-dom";
import styles from "./notFound.module.scss";

const NotFound = () => {
  return (
    <article className={styles.notFound}>
      <Col span={4} />
      <Col span={16}>
        <h3>Такая страница не найдена</h3>
        <p>
          <span>Вернуться</span>
          <Link to={"/"}>
            <Button color="primary" variant="text">
              на главную
            </Button>
          </Link>
        </p>
      </Col>
      <Col span={4} />
    </article>
  );
};

export default NotFound;
