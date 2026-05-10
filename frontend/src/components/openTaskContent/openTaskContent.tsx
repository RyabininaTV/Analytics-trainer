import { useState, type FC } from "react";
import type { OpenTaskContentProps } from "./types";
import { Button, Form, Input } from "antd";
import styles from "./openTaskContent.module.scss";
import "./openTaskContent.ant.scss";

const { useForm } = Form;

const OpenTaskContent: FC<OpenTaskContentProps> = (props) => {
  const { content } = props;

  const [form] = useForm();
  const [disabled, setDisabled] = useState<boolean>(true);

  return (
    <section>
      {!content ? (
        <p className={styles.emptyContent}>Задание еще не добавлено!</p>
      ) : (
        <>
          <div className={styles.contentWrapper}>
            <p>Пояснение:</p>
            <p>{content}</p>
          </div>
          <Form
            form={form}
            className="openTaskForm"
            name="openTaskForm"
            layout="vertical"
            onChange={(event) => {
              if (event.target.value.trim()) {
                setDisabled(false);
              } else {
                setDisabled(true);
              }
            }}
          >
            <Form.Item name={"openAnswer"}>
              <Input.TextArea placeholder="Ответ" rows={5} cols={2} />
            </Form.Item>
            <Form.Item>
              <Button
                disabled={disabled}
                htmlType="submit"
                color="primary"
                variant="filled"
              >
                Отправить
              </Button>
            </Form.Item>
          </Form>
        </>
      )}
    </section>
  );
};

export default OpenTaskContent;
