import { useEffect, useState, type FC } from "react";
import type { TestTaskProps } from "./types";
import { TaskEmpty } from "../shared/taskPageComponents";
import { Button, Form } from "antd";
import { RadioList } from "../shared";
import "./testTask.ant.scss";

const { useForm } = Form;

const TestTask: FC<TestTaskProps> = (props) => {
  const { questionsList } = props;

  const [form] = useForm();

  const [disabled, setDisabled] = useState<boolean>(true);

  useEffect(() => {
    console.log("TEST: ", questionsList);
  }, [questionsList]);

  return (
    <section>
      {!questionsList || questionsList.length === 0 ? (
        <TaskEmpty />
      ) : (
        <Form
          form={form}
          className="testTaskForm"
          name="testTaskForm"
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
          <Form.Item name={"testAnswer"}>
            <RadioList
              list={{
                originalList: questionsList,
                forValue: "id",
                forLabel: "option_text",
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
    </section>
  );
};

export default TestTask;
