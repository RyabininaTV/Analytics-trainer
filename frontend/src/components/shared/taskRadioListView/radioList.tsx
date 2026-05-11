import { Radio } from "antd";
import type { RadioListProps } from "./types";
import type { CheckboxGroupProps } from "antd/es/checkbox";
import "./radioList.ant.scss";

export const RadioList = <T,>(props: RadioListProps<T>) => {
  const {
    list: { originalList, forLabel, forValue },
  } = props;

  const options = originalList.map((listItem) => ({
    value: listItem[forValue],
    label: listItem[forLabel],
    className: "radioCustomStyle",
  })) as CheckboxGroupProps<string | number>["options"];

  return (
    <Radio.Group
      optionType="default"
      orientation="vertical"
      options={options}
    />
  );
};
