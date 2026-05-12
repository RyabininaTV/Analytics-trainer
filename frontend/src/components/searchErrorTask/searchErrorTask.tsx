import { useEffect, useState, type FC } from "react";
import type { SearchErrorTaskProps } from "./types";
import { RadioList } from "../shared";
import { Button, Form } from "antd";
import "./searchErrorTask.ant.scss";
import { TaskEmpty, TaskExplanation } from "../shared/taskPageComponents";

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
      <TaskExplanation content={content} />
      <div>
        {!questionsList || questionsList.length === 0 ? (
          <TaskEmpty />
        ) : (
          <Form
            form={form}
            className="searchErrorTaskForm"
            name="searchErrorTaskForm"
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
