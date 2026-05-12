import type { CheckboxOptionType } from "antd/es/checkbox";

export interface RadioListProps<T> {
  list: {
    originalList: T[];
    forValue: keyof T & (T[keyof T] extends string | number ? keyof T : never);
    forLabel: keyof T &
      (T[keyof T] extends CheckboxOptionType["label"] ? keyof T : never);
  };
  value?: unknown; // пропс для FROM antd, передаются автоматически из FORM.ITEM
  onChange?: (value: unknown) => void; // пропс для FROM antd, передаются автоматически из FORM.ITEM
}
