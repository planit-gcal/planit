import { SelectProps, Space } from 'antd';
import { useCallback, useState } from 'react';

import LimitedMultiSelect from '../LimitedMultiSelect/LimitedMultiSelect';

type ColorSelectProps = SelectProps;

const colorOptions = [
  { value: '#DC2127', label: 'Tomato', id:11 },
  { value: '#FF887C', label: 'Flamingo', id:4 },
  { value: '#FFB878', label: 'Tangerine', id:6 },
  { value: '#FBD75B', label: 'Banana', id:5 },
  { value: '#7AE7BF', label: 'Sage', id:2 },
  { value: '#51B749', label: 'Basil', id:10 },
  { value: '#46D6DB', label: 'Peacock', id:7 },
  { value: '#5484ED', label: 'Blueberry', id:9 },
  { value: '#A4BDFC', label: 'Lavender' , id:1},
  { value: '#DBADFF', label: 'Grape', id:3 },
  { value: '#e1e1e1', label: 'Graphite', id:8 },
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
