import { CloseOutlined, PlusOutlined } from '@ant-design/icons';
import {Button, Col, Divider, Form, Input, Row, Select, Space, Steps, Switch, Tabs, TabsProps, Typography} from 'antd';
import 'antd/es/date-picker/style/index';
import { add, parse } from 'date-fns';
import { useState } from 'react';

import DatePicker from '../DatePicker/DatePicker';
import TimePicker from '../TimePicker/TimePicker';

type CreateEventFormProps = {
  owner: string;
  onSubmit: (result: unknown) => void;
};

const { TextArea } = Input;

export const CreateEventForm = ({ onSubmit, owner }: CreateEventFormProps) => {
  const [generalForm] = Form.useForm<{
    name: string;
    duration: number;
    event_between: [string, string];
    guests: { email: string; obligatory: boolean }[];
  }>();

  const [activeTabKey, setActiveTabKey] = useState('0');

  const stepItems = [
    { title: 'General' },
    { title: 'Google Event Details' },
    { title: 'Search Details' },
    { title: 'Availability' },
    { title: 'Confirm' },
  ];

  const renderTabBar: TabsProps['renderTabBar'] = () => (
    <>
      <Steps size="small" items={stepItems} current={+activeTabKey} onChange={(e) => setActiveTabKey(`${e}`)} />
      <Divider />
    </>
  );

  const handleColorChange = (value: {value:string, label: React.ReactNode}) => console.log(value);

  const colorOptions = [
    {value: '#DC2127', label: 'Tomato'},
    {value: '#FF887C', label: 'Flamingo'},
    {value: '#FFB878', label: 'Tangerine'},
    {value: '#FBD75B', label: 'Banana'},
    {value: '#7AE7BF', label: 'Sage'},
    {value: '#51B749', label: 'Basil'},
    {value: '#46D6DB', label: 'Peacock'},
    {value: '#5484ED', label: 'Blueberry'},
    {value: '#A4BDFC', label: 'Lavender'},
    {value: '#DBADFF', label: 'Grape'},
    {value: '#E1E1E1', label: 'Graphite'},
  ]

  const items = [
    {
      label: '',
      key: '1',
      children: (
        <Form layout="vertical" form={generalForm}>
          <Row gutter={16}>
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
                initialValue={parse('1:30', 'HH:mm', new Date())}
              >
                <TimePicker style={{ width: '100%' }} format="HH:mm" minuteStep={15} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
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
          <Row gutter={16}>
            <Col span={16}>
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
          <Form layout="vertical" form={generalForm}>
            <Row gutter={16}>
              <Col span={16}>
                <Form.Item name="event_description" label={
                  <>Event description <Typography.Text type='secondary'> (optional)</Typography.Text>:</>
                }>
                  {/*<Text type="secondary">(optional)</Text>*/}
                  <TextArea rows={4} placeholder={'What is the meeting about?'}/>
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col span={16}>
                <Form.Item name="event_color" label={
                  <>Event color{' '}<Typography.Text type='secondary'> (optional)</Typography.Text>:</>
                }>
                  <Select
                    labelInValue
                    defaultValue={{value: '#5484ED', label: 'Blueberry'}}
                    onChange={handleColorChange}
                    options={colorOptions}
                  />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col span={16}>
                <Form.Item name="event_location" label={
                  <>Event location <Typography.Text type='secondary'> (optional)</Typography.Text>:</>
                }>
                  <Input placeholder={'Where are you meeting at?'}/>
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
        <Form.Item name="event_name" label="Number of events" required>
          <Input />
        </Form.Item>
      ),
      forceRender: true,
    },
    {
      label: '',
      key: '4',
      children: (
        <Form.Item name="event_name" label="Exclude" required>
          <Input />
        </Form.Item>
      ),
      forceRender: true,
    },
    {
      label: '',
      key: '5',
      children: (
        <Form.Item name="event_name" label="Confirm" required>
          <Input />
        </Form.Item>
      ),
      forceRender: true,
    },
  ];

  const onNextButton = () => {
    setActiveTabKey((prev) => `${+prev + 1}`);

    generalForm
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
