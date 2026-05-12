import { useState, type FC } from "react";
import type { TestTaskProps } from "./types";
import { Button, Form } from "antd";
import "./testTask.ant.scss";
import { TaskEmpty } from "../../components/taskPageComponents/taskEmpty/taskEmpty";
import { RadioList } from "../../components/radioList/radioList";
import { useSendAnswer } from "../../hooks/api/useSendAnswer";

const { useForm } = Form;

const TestTask: FC<TestTaskProps> = (props) => {
  const { questionsList, id } = props;

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
            sendAnswer.mutate({
              task_id: id,
              answer: fields.testAnswer,
            });
          }}
        >
          <Form.Item name={"testAnswer"}>
            <RadioList
              disabledReason={disabledReason}
              list={{
                originalList: questionsList,
                forValue: "id",
                forLabel: "option_text",
              }}
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
      )}
    </section>
  );
};

export default TestTask;
