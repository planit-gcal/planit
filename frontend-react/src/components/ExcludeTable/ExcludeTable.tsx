import {Form, Table, InputRef, Switch, Input, Row, Col} from 'antd';
import type { FormInstance } from 'antd/es/form';
import React, { useContext, useEffect, useRef, useState } from 'react';

import { Day, Days } from '../../api/calendar/calendar.dto';
import { ExcludeForm } from '../CreateEventForm/models';
import DatePicker from '../DatePicker/DatePicker';
import {parse} from "date-fns";

type ExcludeTableProps = {
  form: FormInstance<ExcludeForm>;
};

function toTime(time : string){
    return parse(time,'HH:mm', new Date())
}

const weekDays = [
    { label: 'Monday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
    { label: 'Tuesday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
    { label: 'Wednesday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
    { label: 'Thursday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
    { label: 'Friday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
    { label: 'Saturday', exclude: false, availability: [toTime('12:00'), toTime('20:00')] },
    { label: 'Sunday', exclude: false, availability: [toTime('12:00'), toTime('20:00')] },
];

const ExcludeTable = ({ form }: ExcludeTableProps) => {
  return (
      <Form layout="vertical" form={form}>

          <Row gutter={16} justify="center">
              <Col span={4}>
                  <h3>Day</h3>
              </Col>
              <Col span={4}>
                  <h3>Exclude</h3>
              </Col>
              <Col span={8}>
                  <h3>Availability</h3>
              </Col>
          </Row>
          <Form.List name={'excludeWeekDays'} initialValue={weekDays}>
              {fields => (
                  <div>
                      {fields.map(({key, name, ...restField}) => (
                          <Row gutter={16} justify="center" key={key}>
                              <Col span={6}>
                                  <Form.Item name={[name, 'label']}>
                                      <Input></Input>
                                  </Form.Item>
                              </Col>
                              <Col span={6}>
                                  <Form.Item name={[name, 'exclude']} valuePropName="checked">
                                      <Switch />
                                  </Form.Item>
                              </Col>
                              <Col span={12}>
                                  <Form.Item {...restField} name={[name, 'availability']} shouldUpdate>
                                      {() => {
                                          return <DatePicker.RangePicker
                                              style={{ width: '100%' }}
                                              picker="time"
                                              format={'HH:mm'}
                                              placeholder={['From', 'To']}
                                              disabled ={form.getFieldValue([name, 'exclude'])}
                                          />
                                      }}
                                  </Form.Item>
                              </Col>
                          </Row>
                      ))}
                  </div>
              )}
          </Form.List>
      </Form>
  );
};

export default ExcludeTable;
