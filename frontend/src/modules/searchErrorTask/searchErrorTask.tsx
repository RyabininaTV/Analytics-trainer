import { useState, type FC } from "react";
import type { SearchErrorTaskProps } from "./types";
import { Button, Form } from "antd";
import "./searchErrorTask.ant.scss";
import { TaskExplanation } from "../../components/taskPageComponents/taskExplanation/taskExplanation";
import { TaskEmpty } from "../../components/taskPageComponents/taskEmpty/taskEmpty";
import { RadioList } from "../../components/radioList/radioList";
import { useSendAnswer } from "../../hooks/api/useSendAnswer";

const { useForm } = Form;

const SearchErrorTask: FC<SearchErrorTaskProps> = (props) => {
  const { id, content, questionsList } = props;

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
              sendAnswer.mutate({
                task_id: id,
                answer: fields.searchErrorAnswer,
              });
            }}
          >
            <Form.Item name={"searchErrorAnswer"}>
              <RadioList
                disabledReason={disabledReason}
                list={{
                  originalList: questionsList,
                  forValue: "id",
                  forLabel: "fragment_text",
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
      </div>
    </section>
  );
};

export default SearchErrorTask;
