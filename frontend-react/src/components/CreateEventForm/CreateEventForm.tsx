import { Button, Divider, Form, Input, Space, StepProps, Steps, Tabs, TabsProps } from 'antd';
import { useState } from 'react';
import { act } from 'react-dom/test-utils';

import { EventCreateRequest } from '../../models/event';
import { formatToUTC } from '../../utils/date.utils';
import { AddEmail } from './AddEmail';

type CreateEventFormProps = {
  owner: string;
  onSubmit: (result: EventCreateRequest) => void;
};

export const CreateEventForm = ({ onSubmit, owner }: CreateEventFormProps) => {
  const [form] = Form.useForm<{ name: string; age: number }>();

  const [summary, setSummary] = useState('');
  const [location, setLocation] = useState('');
  const [description, setDescription] = useState('');
  const [attendees, setAttendees] = useState<string[]>([]);
  const [start_date, setStartDate] = useState('');
  const [end_date, setEndDate] = useState('');
  const [duration, setDuration] = useState(0);

  const [activeTabKey, setActiveTabKey] = useState('0');

  const addEmail = (email: string) => {
    setAttendees((prevState) => [...prevState, email]);
  };

  const stepItems = [
    { title: 'General' },
    { title: 'Google Event Details' },
    { title: 'Search Details' },
    { title: 'Availability' },
    { title: 'Confirm' },
  ];

  const renderTabBar: TabsProps['renderTabBar'] = () => (
    <>
      <Steps items={stepItems} current={+activeTabKey} onChange={(e) => setActiveTabKey(`${e}`)} />
      <Divider />
    </>
  );

  const items = [
    {
      label: '',
      key: '1',
      children: (
        <Form.Item name="event_name" label="Event Name" required>
          <Input />
        </Form.Item>
      ),
      forceRender: true,
    },
    {
      label: '',
      key: '2',
      children: (
        <Form.Item name="event_name" label="Description" required>
          <Input />
        </Form.Item>
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
      <Form layout="vertical" style={{ flex: 1 }}>
        <Tabs activeKey={`${+activeTabKey + 1}`} items={items} renderTabBar={renderTabBar} style={{ flex: 1 }} />
      </Form>

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

  return (
    <form>
      <label htmlFor="summary">Event Summary</label>
      <input
        type="text"
        id="summary"
        placeholder="summary"
        value={summary}
        onChange={(e) => setSummary(e.target.value)}
      />
      <label htmlFor="location">Event Location</label>
      <input
        type="text"
        id="location"
        placeholder="location"
        value={location}
        onChange={(e) => setLocation(e.target.value)}
      />
      <label htmlFor="description">Description</label>
      <input
        type="text"
        id="description"
        placeholder="description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <label htmlFor="attendees">Attendees</label>
      <AddEmail emails={attendees} addEmail={addEmail}></AddEmail>
      <label htmlFor="start_date">Start Date</label>
      <input
        type="datetime-local"
        id="start_date"
        placeholder="start date"
        value={start_date}
        onChange={(e) => setStartDate(e.target.value)}
      />
      <label htmlFor="end_date">End Date</label>
      <input
        type="datetime-local"
        id="end_date"
        placeholder="end date"
        value={end_date}
        onChange={(e) => setEndDate(e.target.value)}
      />
      <label htmlFor="duration">Event Duration</label>
      <input
        type="number"
        id="duration"
        placeholder="duration"
        value={duration}
        onChange={(e) => setDuration(+e.target.value)}
      />

      <button
        onClick={(e) => {
          e.preventDefault();
          onSubmit({
            summary,
            location,
            description,
            attendee_emails: attendees,
            start_date: formatToUTC(start_date),
            end_date: formatToUTC(end_date),
            duration,
            owner_email: owner,
          });
        }}
      >
        submit
      </button>
    </form>
  );
};
