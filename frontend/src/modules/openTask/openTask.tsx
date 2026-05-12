import { useState, type FC } from "react";
import type { OpenTaskProps } from "./types";
import { Button, Form, Input } from "antd";
import "./openTask.ant.scss";
import { TaskEmpty } from "../../components/taskPageComponents/taskEmpty/taskEmpty";
import { TaskExplanation } from "../../components/taskPageComponents/taskExplanation/taskExplanation";
import { useSendAnswer } from "../../hooks/api/useSendAnswer";

const { useForm } = Form;

const OpenTask: FC<OpenTaskProps> = (props) => {
  const { id, content } = props;

  const [form] = useForm();
  const [disabled, setDisabled] = useState<boolean>(true);

  const sendAnswer = useSendAnswer();

  const disabledDueToRejectedAnswer =
    sendAnswer.error?.response?.data?.code === "INVALID_ANSWER" &&
    sendAnswer.error?.response?.data?.message ===
      "Task already completed correctly" &&
    sendAnswer.error?.response?.status === 400;

  const disabledReason =
    sendAnswer.data?.status ??
    (disabledDueToRejectedAnswer ? "ERROR" : undefined);

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
              if (event.target.value.trim()) {
                setDisabled(false);
              } else {
                setDisabled(true);
              }
            }}
            onFinish={(fields) => {
              sendAnswer.mutate({
                task_id: id,
                answer: fields.openAnswer,
              });
            }}
          >
            <Form.Item name={"openAnswer"}>
              <Input.TextArea
                disabled={!!disabledReason}
                className={disabledReason ?? ""}
                placeholder="Ответ"
                rows={5}
                cols={2}
              />
            </Form.Item>
            <Form.Item>
              <Button
                disabled={disabled || !!disabledReason}
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
