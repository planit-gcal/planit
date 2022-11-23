import { CloseOutlined, PlusOutlined } from '@ant-design/icons';
import {
  Button,
  Col,
  Divider,
  Form,
  Input,
  Row,
  Space,
  Steps,
  Switch,
  Tabs,
  TabsProps,
  Typography,
  InputNumber,
  Select,
  Slider,
} from 'antd';
import 'antd/es/date-picker/style/index';
import { add, parse } from 'date-fns';
import React, { useState } from 'react';

import ColorSelect from '../ColorSelect/ColorSelect';
import DatePicker from '../DatePicker/DatePicker';
// import ExcludeTable from '../ExcludeTable/ExcludeTable';
import TimePicker from '../TimePicker/TimePicker';
import { ExcludeForm, GeneralForm, GoogleEventForm, SearchForm } from './models';

type CreateEventFormProps = {
  owner: string;
  onSubmit: (result: unknown) => void;
};

const { TextArea } = Input;

export const CreateEventForm = ({ onSubmit, owner }: CreateEventFormProps) => {
  const [generalForm] = Form.useForm<GeneralForm>();
  const [googleEventForm] = Form.useForm<GoogleEventForm>();
  const [searchForm] = Form.useForm<SearchForm>();
  const [excludeForm] = Form.useForm<ExcludeForm>();

  const [activeTabKey, setActiveTabKey] = useState('0');

  const stepItems = [
    { title: 'General' },
    { title: 'Google Event Details' },
    { title: 'Search Details' },
    { title: 'Availability' },
    { title: 'Confirm' },
  ];

  const timeUnitOptions = [
    { value: 1, label: 'Minutes' },
    { value: 60, label: 'Hours' },
    { value: 1440, label: 'Days' },
  ];



  const renderTabBar: TabsProps['renderTabBar'] = () => (
    <>
      <Steps size="small" items={stepItems} current={+activeTabKey} onChange={(e) => setActiveTabKey(`${e}`)} />
      <Divider />
    </>
  );

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

  const items = [
    {
      label: '',
      key: '1',
      children: (
        <Form layout="vertical" form={generalForm}>
          <Row gutter={16} justify="center">
            <Col span={8}>
              <Form.Item name="name" label="Event Name" required initialValue={''}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="duration"
                label="Event Duration"
                required
                initialValue={parse('1:00', 'HH:mm', new Date())}
              >
                <TimePicker style={{ width: '100%' }} format="HH:mm" minuteStep={15} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16} justify="center">
            <Col span={16}>
              <Form.Item
                name="start_end"
                label="Event Between"
                required
                initialValue={[
                  new Date(),
                  add(new Date(), {
                    days: 3,
                  }),
                ]}
              >
                <DatePicker.RangePicker style={{ width: '100%' }} format="eeee, MMMM d" minuteStep={15} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16} justify="center">
            <Col span={16}>
              {/* TODO make this scrollable */}
              <Form.List name="guests" initialValue={[{ email: '', obligatory: true }]}>
                {(fields, { add, remove }) => (
                  <>
                    {fields.length === 0 && <Typography.Text>No guests added yet.</Typography.Text>}

                    {fields.map(({ key, name, ...restField }) => (
                      <Row style={{ width: '100%' }} key={key} gutter={16}>
                        <Col flex={1}>
                          <Form.Item
                            {...restField}
                            name={[name, 'email']}
                            rules={[{ required: true, message: 'Missing email' }]}
                            label={name === 0 ? 'Guest email' : undefined}
                          >
                            <Input />
                          </Form.Item>
                        </Col>

                        <Col span={6}>
                          <Form.Item
                            {...restField}
                            name={[name, 'obligatory']}
                            label={name === 0 ? 'Obligatory?' : undefined}
                            valuePropName="checked"
                          >
                            <Switch defaultChecked style={{ display: 'block' }} />
                          </Form.Item>
                        </Col>

                        <Col span={2}>
                          {/* empty HTML entity */}
                          <Form.Item {...restField} label={name === 0 ? <>&#8203;</> : undefined}>
                            <CloseOutlined onClick={() => remove(name)} />
                          </Form.Item>
                        </Col>
                      </Row>
                    ))}

                    <Form.Item>
                      <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                        Add guest
                      </Button>
                    </Form.Item>
                  </>
                )}
              </Form.List>
            </Col>
          </Row>
        </Form>
      ),
      forceRender: true,
    },
    {
      label: '',
      key: '2',
      children: (
        <Form layout="vertical" form={googleEventForm}>
          <Row gutter={16} justify="center">
            <Col span={16}>
              <Form.Item
                name="event_description"
                label={
                  <Space>
                    {`Event description `}
                    <Typography.Text type="secondary">(optional)</Typography.Text>
                  </Space>
                }
              >
                <TextArea rows={4} placeholder={'What is the meeting about?'} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16} justify="center">
            <Col span={16}>
              <Form.Item
                name="event_color"
                key="event_color"
                shouldUpdate
                getValueProps={(v) => ({ value: [v] })}
                normalize={(v) => (v ? v[0] : v)}
                label={
                  <Space>
                    {`Event color `}
                    <Typography.Text type="secondary">(optional)</Typography.Text>
                  </Space>
                }
                initialValue={'#5484ED'}
              >
                <ColorSelect />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16} justify="center">
            <Col span={16}>
              <Form.Item
                name="event_location"
                label={
                  <Space>
                    {`Event location `}
                    <Typography.Text type="secondary">(optional)</Typography.Text>
                  </Space>
                }
              >
                <Input placeholder={'Where are you meeting at?'} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      ),
      forceRender: true,
    },
    {
      label: '',
      key: '3',
      children: (
        <Form layout="vertical" form={searchForm}>
          <Row gutter={16} justify="center">
            <Col span={8}>
              <Form.Item name="num_of_events" label={'How many events to create?'} required initialValue={1}>
                <InputNumber style={{ width: '100%' }} min={1} max={5} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label={'Time between multiple events'}>
                <Row>
                  <Form.Item name="time_between_events" required initialValue={[30, 60]} noStyle>
                    <Slider style={{ width: 'calc(100% - 115px)' }} range max={60} />
                  </Form.Item>
                  <Form.Item name="time_between_events_unit" initialValue={'Minutes'} noStyle>
                    <Select style={{ width: '95px', marginLeft: '8px' }} options={timeUnitOptions} />
                  </Form.Item>
                </Row>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16} justify="center">
            <Col span={8}>
              <Form.Item
                name="break_event"
                label={"Break events if can't find?"}
                initialValue={true}
                valuePropName="checked"
              >
                <Switch defaultChecked style={{ display: 'block' }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="duration_of_event" label={'Duration of a single event'} initialValue={true}>
                <DatePicker.RangePicker
                  style={{ width: '100%' }}
                  picker="time"
                  format={'HH:mm'}
                  placeholder={['Min duration', 'Max duration']}
                />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      ),
      forceRender: true,
    },
    {
      label: '',
      key: '4',
      children: (
        <Row gutter={16} justify="center">
          <Col span={16}>
            {/*<ExcludeTable form={excludeForm} />*/}
            <Form layout="vertical" form={excludeForm}>

              <Row gutter={16} justify="center">
                <Col span={6}>
                  <h3>Day</h3>
                </Col>
                <Col span={6}>
                  <h3>Exclude</h3>
                </Col>
                <Col span={12}>
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
                                {(() => {
                                  console.log(excludeForm.getFieldsValue([name, 'exclude']))
                                })()}
                                {/*{() => {*/}
                                {/*  return <DatePicker.RangePicker*/}
                                {/*      style={{ width: '100%' }}*/}
                                {/*      picker="time"*/}
                                {/*      format={'HH:mm'}*/}
                                {/*      placeholder={['From', 'To']}*/}
                                {/*      disabled ={excludeForm.getFieldValue([name, 'exclude'])}*/}
                                {/*  />*/}
                                {/*}}*/}
                              </Form.Item>
                            </Col>
                          </Row>
                      ))}
                    </div>
                )}
              </Form.List>
            </Form>
          </Col>
        </Row>
      ),
      forceRender: true,
    },
    {
      label: '',
      key: '5',
      children: (
        <Form.Item name="event_name" label="Exclude" required>
          <Input />
        </Form.Item>
      ),
      forceRender: true,
    },
  ];

  const onNextButton = () => {
    setActiveTabKey((prev) => `${+prev + 1}`);

    excludeForm
      .validateFields()
      .then((val) => {
        console.log(val);
      })
      .catch((reason) => {
        console.log(reason);
      });
  };

  const onBackButton = () => {
    setActiveTabKey((prev) => `${+prev - 1}`);
  };

  const onJumpButton = () => {
    setActiveTabKey('4');
  };

  const onConfirmButton = () => {
    alert('todo');
  };

  const onSaveToPresetButton = () => {
    alert('todo');
  };

  return (
    <div style={{ width: '100%', height: '100%', display: 'flex', gap: 0, flexDirection: 'column' }}>
      <Tabs activeKey={`${+activeTabKey + 1}`} items={items} renderTabBar={renderTabBar} style={{ flex: 1 }} />

      <Divider />

      <Space direction="horizontal" style={{ width: '100%', justifyContent: 'end' }}>
        {activeTabKey === '0' && (
          <>
            <Button onClick={onJumpButton}>Jump to Confirm</Button>
            <Button onClick={onNextButton} type="primary">
              Next
            </Button>
          </>
        )}

        {['1', '2', '3'].includes(activeTabKey) && (
          <>
            <Button onClick={onBackButton}>Back</Button>
            <Button onClick={onNextButton} type="primary">
              Next
            </Button>
          </>
        )}

        {activeTabKey === '4' && (
          <>
            <Button onClick={onSaveToPresetButton} type="dashed">
              Save to preset
            </Button>
            <Button onClick={onBackButton}>Back</Button>
            <Button onClick={onConfirmButton} type="primary">
              Confirm
            </Button>
          </>
        )}
      </Space>
    </div>
  );
};
