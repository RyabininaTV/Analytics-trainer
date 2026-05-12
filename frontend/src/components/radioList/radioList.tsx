import { Radio } from "antd";
import type { RadioListProps } from "./types";
import type { CheckboxGroupProps } from "antd/es/checkbox";
import "./radioList.ant.scss";

export const RadioList = <T,>(props: RadioListProps<T>) => {
  const {
    list: { originalList, forLabel, forValue },
    disabled = false,
    disabledReason,
    value,
    onChange,
  } = props;

  const options = originalList.map((listItem) => ({
    value: listItem[forValue],
    label: listItem[forLabel],
    className: `radioCustomStyle ${disabledReason ?? ""}`,
  })) as CheckboxGroupProps<string | number>["options"];

  return (
    <Radio.Group
      disabled={disabled || !!disabledReason}
      optionType="default"
      orientation="vertical"
      value={value}
      onChange={(e) => onChange?.(e.target.value)}
      options={options}
    />
  );
};
