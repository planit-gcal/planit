import { Form, FormInstance, Input, Modal } from 'antd';
import { useEffect, useState } from 'react';

import { EventCreateRequest } from '../../api/calendar/calendar.dto';
import { convertFormToRequest } from '../../utils/event.utils';
import { ExcludeForm, GeneralForm, GoogleEventForm } from '../CreateEventForm/models';

type PresetCreateModalProps = {
  forms: {
    generalForm: FormInstance<GeneralForm>;
    googleEventForm: FormInstance<GoogleEventForm>;
    excludeForm: FormInstance<ExcludeForm>;
  };
  open: boolean;
  owner: string;
  onCancel: () => void;
  onCreatePreset: (request: EventCreateRequest) => void;
};

type FormType = {
  name: string;
};

export const PresetCreateModal = ({ forms, owner, open, onCancel, onCreatePreset }: PresetCreateModalProps) => {
  const [form] = Form.useForm<FormType>();

  const onCreate = async ({ name }: FormType) => {
    const general = await forms.generalForm.validateFields();
    const googleEvent = await forms.googleEventForm.validateFields();
    const exclude = await forms.excludeForm.validateFields();

    const request = convertFormToRequest(owner, general, googleEvent, exclude);
    request.event_preset_detail.event_preset.name = name;

    onCreatePreset(request);
  };

  return (
    <Modal
      open={open}
      title="Create a new preset"
      okText="Create"
      cancelText="Cancel"
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={() => {
        form
          .validateFields()
          .then((values) => {
            form.resetFields();
            onCreate(values);
          })
          .catch((info) => {
            console.log('Validate Failed:', info);
          });
      }}
    >
      <Form form={form} layout="vertical" name="form_in_modal">
        <Form.Item
          name="name"
          label="Preset name"
          rules={[{ required: true, message: 'Please input the preset name!' }]}
        >
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  );
};
