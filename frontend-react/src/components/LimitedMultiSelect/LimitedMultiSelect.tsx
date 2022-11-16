import { CloseOutlined, PlusOutlined } from '@ant-design/icons';
import {
  Button,
  Col,
  Divider,
  Form,
  Input,
  Row,
  Select,
  SelectProps,
  Space,
  Steps,
  Switch,
  Tabs,
  TabsProps,
  Tag,
  Typography,
} from 'antd';
import { DefaultOptionType } from 'antd/lib/select';
import { useCallback, useMemo, useState } from 'react';

function toRawValue(optionValue: any) {
  return typeof optionValue === 'object' ? optionValue.value : optionValue;
}

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
