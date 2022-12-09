import { SelectProps, Space } from 'antd';
import { useCallback, useState } from 'react';

import LimitedMultiSelect from '../LimitedMultiSelect/LimitedMultiSelect';

type ColorSelectProps = SelectProps;

const colorOptions = [
  { value: '#DC2127', label: 'Tomato' },
  { value: '#FF887C', label: 'Flamingo' },
  { value: '#FFB878', label: 'Tangerine' },
  { value: '#FBD75B', label: 'Banana' },
  { value: '#7AE7BF', label: 'Sage' },
  { value: '#51B749', label: 'Basil' },
  { value: '#46D6DB', label: 'Peacock' },
  { value: '#5484ED', label: 'Blueberry' },
  { value: '#A4BDFC', label: 'Lavender' },
  { value: '#DBADFF', label: 'Grape' },
  { value: '#E1E1E1', label: 'Graphite' },
];

const tagRender = (value: string, label: string) => {
  return (
    <Space>
      <div style={{ paddingLeft: 8, display: 'flex', alignItems: 'center' }}>
        <svg height="12" width="12">
          <circle cx="6" cy="6" r="6" fill={value} />
        </svg>
      </div>
      {label}
    </Space>
  );
};

const ColorSelect = ({ onChange: originalOnChange, ...selectProps }: ColorSelectProps) => {
  const [open, setOpen] = useState(false);

  const onChange = useCallback<NonNullable<SelectProps['onChange']>>(
    (...args) => {
      setOpen(false);

      originalOnChange?.(...args);
    },
    [originalOnChange]
  );

  return (
    <LimitedMultiSelect
      {...selectProps}
      open={open}
      showSearch={false}
      showArrow
      tagRender={(props) => <>{props.label}</>}
      options={colorOptions.map((e) => ({ ...e, label: tagRender(e.value, e.label) }))}
      maxSelected={1}
      onChange={onChange}
      onDropdownVisibleChange={(open) => setOpen(open)}
    />
  );
};

export default ColorSelect;
