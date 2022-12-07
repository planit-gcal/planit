import { DeleteOutlined } from '@ant-design/icons';
import { Button, Row, Select } from 'antd';
import { DefaultOptionType } from 'antd/lib/select';
import { useContext } from 'react';

import { EventPresetDetail } from '../../api/calendar/calendar.dto';
import { PresetsContext } from '../../contexts/PresetsContext';

type PresetSelectProps = {
  onApplyPreset: (preset: EventPresetDetail) => void;
};

export const PresetSelect = ({ onApplyPreset }: PresetSelectProps) => {
  const { presets, deletePreset } = useContext(PresetsContext);

  const getPresetById = (id: number) => presets.find((p) => p.event_preset.id_event_preset === id)!;

  const onDeleteButtonClick = async (e: React.MouseEvent<Element, MouseEvent>, presetId: number) => {
    e.preventDefault();
    e.stopPropagation();

    deletePreset(presetId);
  };

  const options: DefaultOptionType[] = presets.map((p) => ({
    label: (
      <Row justify={'space-between'}>
        {p.event_preset.name}{' '}
        <Button
          type="text"
          onClick={(e) => onDeleteButtonClick(e, p.event_preset.id_event_preset!)}
          icon={<DeleteOutlined />}
        />
      </Row>
    ),
    value: p.event_preset.id_event_preset,
  }));

  const onSelect = (presetId: number) => {
    onApplyPreset(getPresetById(presetId));
  };

  return (
    <Select style={{ width: '180px' }} size="small" placeholder="Select preset" options={options} onSelect={onSelect} />
  );
};
