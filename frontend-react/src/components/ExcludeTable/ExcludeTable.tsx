import { Form, Table, InputRef, Switch, Input } from 'antd';
import type { FormInstance } from 'antd/es/form';
import React, { useContext, useEffect, useRef, useState } from 'react';

import { Day, Days } from '../../api/calendar/calendar.dto';
import { ExcludeForm } from '../CreateEventForm/models';
import DatePicker from '../DatePicker/DatePicker';

type EditableTableProps = Parameters<typeof Table>[0];

interface DataType {
  key: string;
  name: Day;
  exclude: boolean;
  availability: [string, string];
}

type ColumnTypes = Exclude<EditableTableProps['columns'], undefined>;

type ExcludeTableProps = {
  form: FormInstance<ExcludeForm>;
};

const ExcludeTable = ({ form }: ExcludeTableProps) => {
  return (
    <Form layout="vertical" form={form}>
      <div>todo</div>
    </Form>
  );
};

export default ExcludeTable;
