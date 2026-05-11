import { useEffect, useState, type FC } from "react";
import type { SearchErrorTaskProps } from "./types";
import { RadioList } from "../shared";
import { Button, Form } from "antd";
import styles from "./searchErrorTask.module.scss";
import "./searchErrorTask.ant.scss";

const { useForm } = Form;

const SearchErrorTask: FC<SearchErrorTaskProps> = (props) => {
  const { content, questionsList } = props;

  const [form] = useForm();

  const [disabled, setDisabled] = useState<boolean>(true);

  useEffect(() => {
    console.log("questionsList: ", questionsList);
  }, [questionsList]);

  return (
    <section>
      <div className={styles.contentWrapper}>
        <p>Пояснение:</p>
        <p>{content}</p>
      </div>
      <div>
        {!questionsList || questionsList.length === 0 ? (
          <p className={styles.emptyContent}>Задание еще не добавлено!</p>
        ) : (
          <Form
            form={form}
            className="searchErrorTask"
            name="searchErrorTask"
            onChange={(event) => {
              if (event.target.value) {
                setDisabled(false);
              } else {
                setDisabled(true);
              }
            }}
            onFinish={(fields) => {
              console.log("fields: ", fields);
            }}
          >
            <Form.Item name={"searchErrorAnswer"}>
              <RadioList
                list={{
                  originalList: questionsList,
                  forValue: "id",
                  forLabel: "fragment_text",
                }}
              />
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
        )}
      </div>
    </section>
  );
};

export default SearchErrorTask;
