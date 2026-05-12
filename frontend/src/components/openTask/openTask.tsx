import { useState, type FC } from "react";
import type { OpenTaskProps } from "./types";
import { Button, Form, Input } from "antd";
import "./openTask.ant.scss";
import { TaskEmpty, TaskExplanation } from "../shared/taskPageComponents";

const { useForm } = Form;

const OpenTask: FC<OpenTaskProps> = (props) => {
  const { content } = props;

  const [form] = useForm();
  const [disabled, setDisabled] = useState<boolean>(true);

  return (
    <section>
      {!content ? (
        <TaskEmpty />
      ) : (
        <>
          <TaskExplanation content={content} />
          <Form
            form={form}
            className="openTaskForm"
            name="openTaskForm"
            layout="vertical"
            onChange={(event) => {
              console.log("event: ", event.target.value);

              if (event.target.value.trim()) {
                setDisabled(false);
              } else {
                setDisabled(true);
              }
            }}
            onFinish={(fields) => {
              console.log("fields: ", fields);
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

export default OpenTask;
