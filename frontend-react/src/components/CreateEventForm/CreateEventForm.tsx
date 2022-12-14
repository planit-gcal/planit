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
} from 'antd';
import 'antd/es/date-picker/style/index';
import { add, format, parse } from 'date-fns';
import React, { useState } from 'react';
import { EventCreateRequest, EventPresetDetail } from '../../api/calendar/calendar.dto';
import { convertFormToRequest, getPartialFormFromPreset } from '../../utils/event.utils';

import ColorSelect from '../ColorSelect/ColorSelect';
import DatePicker from '../DatePicker/DatePicker';
import { PresetCreateModal } from '../PresetCreateModal/PresetCreateModal';
import { PresetSelect } from '../PresetSelect/PresetSelect';
import TimePicker from '../TimePicker/TimePicker';
import { ExcludeForm, GeneralForm, GoogleEventForm } from './models';

type CreateEventFormProps = {
    owner: string;
    onSubmit: (result: EventCreateRequest) => void;
    onSaveToPreset: (result: EventCreateRequest) => void;
};

const { TextArea } = Input;

const { useWatch } = Form;

export const CreateEventForm = ({ onSubmit, onSaveToPreset, owner }: CreateEventFormProps) => {
    const [generalForm] = Form.useForm<GeneralForm>();
    const [googleEventForm] = Form.useForm<GoogleEventForm>();
    const [excludeForm] = Form.useForm<ExcludeForm>();

    const [activeTabKey, setActiveTabKey] = useState('0');
    const [isPresetCreateModalOpen, setIsPresetCreateModalOpen] = useState(false);

    const stepItems = [
        { title: 'General' },
        { title: 'Details' },
        { title: 'Availability' },
        { title: 'Confirm' },
    ];

    const RenderTabBar: TabsProps['renderTabBar'] = () => (
        <>
            <Steps
                size="small"
                items={stepItems}
                current={+activeTabKey}
                onChange={(e) => {
                    if (activeTabKey === '3') {
                        setActiveTabKey(`${e}`);
                    } else {
                        getActiveForm(`${+activeTabKey + 1}`)!
                            .validateFields()
                            .then((val) => {
                                setActiveTabKey(`${e}`);
                            });
                    }
                }}
            />
            <Divider />
        </>
    );

    function toTime(time: string) {
        return parse(time, 'HH:mm', new Date());
    }

    function toDateString(date: Date) {
        return date ? format(date, 'dd MMM yyyy') : '';
    }

    function toTimeString(date:Date){
        return date ? format(date, 'HH:mm') : '';
    }

    const weekDays = [
        { name: 'Monday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
        { name: 'Tuesday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
        { name: 'Wednesday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
        { name: 'Thursday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
        { name: 'Friday', exclude: true, availability: [toTime('08:00'), toTime('16:00')] },
        { name: 'Saturday', exclude: false, availability: [toTime('12:00'), toTime('20:00')] },
        { name: 'Sunday', exclude: false, availability: [toTime('12:00'), toTime('20:00')] },
    ];

    const onApplyPreset = (preset: EventPresetDetail) => {
        const converted = getPartialFormFromPreset(preset);

        generalForm.setFieldsValue(converted.generalForm)
        excludeForm.setFieldsValue(converted.excludeForm)
    }

    const items = [
        {
            label: '',
            key: '1',
            children: (
                <>
                    <Row gutter={16} justify="center">
                        <Col span={16}>
                            <PresetSelect onApplyPreset={onApplyPreset} />
                            <Divider />
                        </Col>
                    </Row>
                    <Form layout="vertical" form={generalForm}>
                        <Row gutter={16} justify="center">
                            <Col span={8}>
                                <Form.Item
                                    name="name"
                                    label="Event Name"
                                    rules={[
                                        {
                                            required: true,
                                            message: 'Please input event name!',
                                        },
                                    ]}
                                >
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
                                    name="event_between"
                                    label="Event Between"
                                    required
                                    initialValue={[
                                        new Date(),
                                        add(new Date(), {
                                            days: 3,
                                        }),
                                    ]}
                                >
                                    <DatePicker.RangePicker
                                        style={{ width: '100%' }}
                                        format="eeee, MMMM d"
                                        minuteStep={15}
                                    />
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row gutter={16} justify="center">
                            <Col span={16}>
                                <Form.List name="guests" initialValue={[{email: '', obligatory: true}]}>
                                    {(fields, { add, remove }) => (
                                        <div>
                                            {fields.length === 0 && <Typography.Text>No guests added yet.</Typography.Text>}
                                            <div style={{flex: 1, overflowY: 'hidden' }}>
                                                <div style={{ height: '100%', overflowY: 'scroll' }}>
                                                    {fields.map(({ key, name, ...restField }) => (
                                                        <Row style={{ width: '100%' }} key={key} gutter={16}>
                                                            <Col flex={1}>
                                                                <Form.Item
                                                                    {...restField}
                                                                    name={[name, 'email']}
                                                                    rules={[{ required: true, message: 'Missing email' }, {type: 'email', message: 'Please enter a valid email!'}]}
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
                                                                <Form.Item
                                                                    {...restField}
                                                                    label={name === 0 ? <>&#8203;</> : undefined}
                                                                >
                                                                    <CloseOutlined onClick={() => remove(name)} />
                                                                </Form.Item>
                                                            </Col>
                                                        </Row>
                                                    ))}
                                                </div>
                                            </div>
                                        <Form.Item>
                                            <Button type="dashed" size='small' onClick={() => add({email:'', obligatory:false})} block icon={<PlusOutlined />}>
                                                Add guest
                                            </Button>
                                        </Form.Item>
                                    </div>
                                )}
                            </Form.List>
                        </Col>
                    </Row>
                </Form>
            </>
            ),
            forceRender: true,
        },
        {
            label: '',
            key: '2',
            children: (
                <Form layout="vertical" form={googleEventForm}>
                    <Row gutter={16} justify="center">
                        <Space align="end" style={{ marginBottom: '8px' }}>
                            <Typography.Title level={3} style={{ margin: 0 }}>
                                {useWatch('name', generalForm)}
                            </Typography.Title>
                            <Typography.Title level={5} style={{ margin: 0 }}>
                                {` `}
                                {toDateString(useWatch('event_between', generalForm)?.[0])}
                                {` `}-{` `}
                                {toDateString(useWatch('event_between', generalForm)?.[1])}
                            </Typography.Title>
                        </Space>
                    </Row>
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
                <Form layout="vertical" form={excludeForm}>
                    <Row gutter={16} justify="center">
                        <Space align="end" style={{ marginBottom: '8px' }}>
                            <Typography.Title level={3} style={{ margin: 0 }}>
                                {useWatch('name', generalForm)}
                            </Typography.Title>
                            <Typography.Title level={5} style={{ margin: 0 }}>
                                {` `}
                                {toDateString(useWatch('event_between', generalForm)?.[0])}
                                {` `}-{` `}
                                {toDateString(useWatch('event_between', generalForm)?.[1])}
                            </Typography.Title>
                        </Space>
                    </Row>
                    <Row gutter={16} justify="center">
                        <Col span={16}>
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
                            <Form.List name="excludeWeekDays" initialValue={weekDays}>
                                {(fields, { add, remove }) => {
                                    return (
                                        <div>
                                            {fields.map((field, name) => (
                                                <Row gutter={8} justify="center" key={field.key}>
                                                    <Form.Item name={[name, 'name']} label="name" noStyle>
                                                        <Input hidden></Input>
                                                    </Form.Item>
                                                    <Col span={6}>
                                                        <Form.Item noStyle shouldUpdate style={{ marginBottom: '8px' }}>
                                                            {() => {
                                                                return (
                                                                    <div>
                                                                        {excludeForm.getFieldValue([
                                                                            'excludeWeekDays',
                                                                            name,
                                                                            'name',
                                                                        ])}
                                                                    </div>
                                                                );
                                                            }}
                                                        </Form.Item>
                                                    </Col>

                                                    <Col span={6}>
                                                        <Form.Item
                                                            name={[name, 'exclude']}
                                                            valuePropName="checked"
                                                            style={{ marginBottom: '8px' }}
                                                        >
                                                            <Switch />
                                                        </Form.Item>
                                                    </Col>

                                                    <Col span={12}>
                                                        <Form.Item noStyle shouldUpdate>
                                                            {() => {
                                                                return (
                                                                    <Form.Item
                                                                        name={[name, 'availability']}
                                                                        style={{ marginBottom: '8px' }}
                                                                    >
                                                                        <DatePicker.RangePicker
                                                                            style={{ width: '100%' }}
                                                                            picker="time"
                                                                            format={'HH:mm'}
                                                                            placeholder={['From', 'To']}
                                                                            allowClear={false}
                                                                            disabled={excludeForm.getFieldValue([
                                                                                'excludeWeekDays',
                                                                                name,
                                                                                'exclude',
                                                                            ])}
                                                                        />
                                                                    </Form.Item>
                                                                );
                                                            }}
                                                        </Form.Item>
                                                    </Col>
                                                </Row>
                                            ))}
                                        </div>
                                    );
                                }}
                            </Form.List>
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
                <div>
                    <Row justify="center">
                        <Space align="end" style={{ marginBottom: '8px' }}>
                            <Typography.Title level={3} style={{ margin: 0 }}>
                                {useWatch('name', generalForm)}
                            </Typography.Title>
                            <Typography.Title level={5} style={{ margin: 0 }}>
                                {` `}
                                {toDateString(useWatch('event_between', generalForm)?.[0])}
                                {` `}-{` `}
                                {toDateString(useWatch('event_between', generalForm)?.[1])}
                            </Typography.Title>
                        </Space>
                    </Row>
                    <Row gutter={16} justify="center" style={{ marginBottom: '8px' }}>
                        <Typography.Text type="secondary" style={{fontSize:'12px'}}>
                            {`You're about to create an event in your and your guests' calendars. Please review your preferences before confirming.`}
                        </Typography.Text>
                    </Row>
                    <Row gutter={16} justify="center">
                        <Col span={20}>
                            <Row>
                                <Col span={8}>
                                    <Space align="end" style={{ marginBottom: 0 }}>
                                        <Typography.Title level={5} style={{marginBottom:0}}>Event duration:</Typography.Title>
                                        <Typography.Text style={{margin:0}}>{toTimeString(useWatch('duration', generalForm))}</Typography.Text>
                                    </Space>
                                    <Typography.Title level={5} style={{marginBottom:0}}>Event guests:</Typography.Title>
                                    <Typography.Text type="secondary" style={{marginBottom:'8px', fontSize:'12px', marginTop:'0px'}}>
                                        Obligatory guests are {` `}
                                        <Typography.Text type="secondary" strong style={{fontSize:'12px'}}>bold</Typography.Text>
                                    </Typography.Text>
                                    {useWatch('guests',generalForm)?.map(({ email, obligatory }) => (
                                        <div key={email}>
                                            <Typography.Text strong={obligatory}>{email}</Typography.Text>
                                        </div>
                                    ))}
                                </Col>
                                <Col span={8}>
                                    <Row>
                                        <Typography.Title level={5} style={{ margin: 0 }}>Description:</Typography.Title>
                                    </Row>
                                    <Row>
                                        <Typography.Paragraph ellipsis={{rows: 4, expandable: false}} style={{ margin: 0 }}>{useWatch('event_description', googleEventForm)}</Typography.Paragraph>
                                    </Row>
                                    <Row>
                                        <Typography.Title level={5} style={{ margin: 0 }}>Location:</Typography.Title>
                                    </Row>
                                    <Row>
                                        <Typography.Paragraph ellipsis={{rows: 1, expandable: false}} style={{ margin: 0 }}>{useWatch('event_location', googleEventForm)}</Typography.Paragraph>
                                    </Row>
                                    <Row>
                                        <Space align="end">
                                            <Typography.Title level={5} style={{ margin: 0 }}>Color:</Typography.Title>
                                            <div style={{ margin: 0 }}>
                                                <svg height="12" width="12">
                                                    <circle cx="6" cy="6" r="6" fill={useWatch('event_color', googleEventForm)} />
                                                </svg>
                                            </div>
                                        </Space>
                                    </Row>
                                </Col>
                                <Col span={8}>
                                    <Typography.Title level={5} style={{marginBottom:'0px'}}>Availability:</Typography.Title>
                                    {useWatch('excludeWeekDays',excludeForm)?.map(({ name, exclude, availability }) => (
                                        <Row key={name}>
                                            {exclude ? (
                                                <Typography.Text delete>{name}</Typography.Text>
                                            ):(
                                                <Typography.Text >{name} {toTimeString(availability[0])} - {toTimeString(availability[1])}</Typography.Text>
                                            )}
                                        </Row>
                                    ))}
                                </Col>
                            </Row>
                        </Col>
                    </Row>
                </div>
            ),
            forceRender: true,
        },
    ];

    const getActiveForm = (tabKey: string) => {
        switch (tabKey) {
            case '1':
                return generalForm;
            case '2':
                return googleEventForm;
            case '3':
                return excludeForm;
        }
    };

    const onBackButton = () => {
        if(activeTabKey === '3') {
            setActiveTabKey((prev) => `${+prev - 1}`);
            return;
        }
        
        getActiveForm(`${+activeTabKey + 1}`)!
            .validateFields()
            .then(() => {
                setActiveTabKey((prev) => `${+prev - 1}`);
            });
    };

    const onNextButton = () => {
        getActiveForm(`${+activeTabKey + 1}`)!
            .validateFields()
            .then(() => {
                setActiveTabKey((prev) => `${+prev + 1}`);
            });
    };

    const onJumpButton = () => {
        getActiveForm(`${+activeTabKey + 1}`)!
            .validateFields()
            .then(() => {
                setActiveTabKey('3');
            });
    };

    const onConfirmButton = async () => {
        const general = await generalForm.validateFields();
        const googleEvent = await googleEventForm.validateFields();
        const exclude = await excludeForm.validateFields();

        onSubmit(convertFormToRequest(owner, general, googleEvent, exclude));
    };

    const onSaveToPresetButton = async () => {
        setIsPresetCreateModalOpen(true);
    };

    return (
        <div style={{ width: '100%', height: '100%', display: 'flex', gap: 0, flexDirection: 'column' }}>
            <PresetCreateModal
                forms={{ generalForm, googleEventForm, excludeForm }}
                owner={owner}
                open={isPresetCreateModalOpen}
                onCancel={() => setIsPresetCreateModalOpen(false)}
                onCreatePreset={(request) => {
                    setIsPresetCreateModalOpen(false);
                    onSaveToPreset(request);
                }}
            />
            
            <Tabs activeKey={`${+activeTabKey + 1}`} items={items} renderTabBar={RenderTabBar} style={{ flex: 1 }} />

            <Divider />

            <Space
                direction="horizontal"
                style={{ width: '100%', justifyContent: 'end', display: activeTabKey === '0' ? undefined : 'none' }}
            >
                <Button onClick={onJumpButton} style={{ minWidth: '85px' }}>
                    Jump to Confirm
                </Button>
                <Button onClick={onNextButton} type="primary" style={{ minWidth: '85px' }}>
                    Next
                </Button>
            </Space>

            <Space
                direction="horizontal"
                style={{
                    width: '100%',
                    justifyContent: 'end',
                    display: ['1', '2'].includes(activeTabKey) ? undefined : 'none',
                }}
            >
                <Button onClick={onBackButton} style={{ minWidth: '85px' }}>
                    Back
                </Button>
                <Button onClick={onNextButton} type="primary" style={{ minWidth: '85px' }}>
                    Next
                </Button>
            </Space>

            <Space
                direction="horizontal"
                style={{ width: '100%', justifyContent: 'end', display: activeTabKey === '3' ? undefined : 'none' }}
            >
                <Button onClick={onSaveToPresetButton} type="dashed">
                    Save to preset
                </Button>
                <Button onClick={onBackButton} style={{ minWidth: '85px' }}>
                    Back
                </Button>
                <Button onClick={onConfirmButton} type="primary" style={{ minWidth: '85px' }}>
                    Confirm
                </Button>
            </Space>
        </div>
    );
};
