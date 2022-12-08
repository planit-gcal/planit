import { Form, FormInstance, Input, Modal, Radio, Select } from 'antd';
import { DefaultOptionType } from 'antd/lib/select';
import { useContext, useEffect, useState } from 'react';

import { EventCreateRequest } from '../../api/calendar/calendar.dto';
import { PresetsContext } from '../../contexts/PresetsContext';
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
  choice: 'CREATE' | 'UPDATE';
  newPresetName?: string;
  existingPresetId?: number;
};

export const PresetCreateModal = ({ forms, owner, open, onCancel, onCreatePreset }: PresetCreateModalProps) => {
  const { presets } = useContext(PresetsContext);
  const [form] = Form.useForm<FormType>();

  const onCreate = async ({ newPresetName, choice, existingPresetId }: FormType) => {
    const general = await forms.generalForm.validateFields();
    const googleEvent = await forms.googleEventForm.validateFields();
    const exclude = await forms.excludeForm.validateFields();

    const request = convertFormToRequest(owner, general, googleEvent, exclude);

    if (choice === 'CREATE') {
      request.event_preset_detail.event_preset.name = newPresetName!;
      delete request.event_preset_detail.event_preset.id_event_preset;
    } else {
      request.event_preset_detail.event_preset.id_event_preset = existingPresetId;
    }

    onCreatePreset(request);
  };

  const existingPresetsOptions: DefaultOptionType[] = presets.map((p) => ({
    value: p.event_preset.id_event_preset,
    label: p.event_preset.name,
  }));

  return (
    <Modal
      open={open}
      title="Save a preset"
      okText="Save"
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
        <Form.Item name="choice" initialValue={'CREATE'}>
          <Radio.Group>
            <Radio value="CREATE">Create a new preset</Radio>
            <Radio value="UPDATE">Update an existing preset</Radio>
          </Radio.Group>
        </Form.Item>

        <Form.Item shouldUpdate noStyle>
          {() =>
            form.getFieldValue(['choice']) === 'CREATE' ? (
              <Form.Item name="newPresetName" rules={[{ required: true, message: 'Please input the preset name!' }]}>
                <Input placeholder="Name of a new preset" />
              </Form.Item>
            ) : (
              <Form.Item
                name="existingPresetId"
                rules={[{ required: true, message: 'Please select an existing preset!' }]}
              >
                <Select placeholder="Choose existing preset" options={existingPresetsOptions} />
              </Form.Item>
            )
          }
        </Form.Item>
      </Form>
    </Modal>
  );
};
