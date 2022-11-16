import { Select, SelectProps } from 'antd';
import { useCallback } from 'react';

type LimitedSelectProps = Omit<SelectProps, 'mode'> & { maxSelected: number };

const LimitedMultiSelect = ({
  maxSelected,
  options: originalOptions,
  onChange: originalOnChange,
  ...selectProps
}: LimitedSelectProps) => {
  const options = originalOptions;

  const onChange = useCallback<NonNullable<SelectProps['onChange']>>(
    (values, option) => {
      const lastValue = values[values.length - 1];
      const lastOption = Array.isArray(option) ? option[option.length - 1] : option;

      originalOnChange?.([lastValue], [lastOption]);
    },
    [originalOnChange]
  );

  return <Select {...selectProps} options={options} mode="multiple" onChange={onChange} />;
};

export default LimitedMultiSelect;
