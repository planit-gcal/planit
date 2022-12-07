import { DeleteOutlined } from '@ant-design/icons';
import { Button, notification, Row, Select } from 'antd';
import { DefaultOptionType } from 'antd/lib/select';
import { useContext, useEffect, useState } from 'react';

import { EventPresetDetail } from '../../api/calendar/calendar.dto';
import { deleteUserPreset, getPresets } from '../../api/presets/presets.api';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

type PresetSelectProps = {
  onApplyPreset: (preset: EventPresetDetail) => void;
};

export const PresetSelect = ({ onApplyPreset }: PresetSelectProps) => {
  const { userDetails } = useContext(PlanitUserContext);
  const [presets, setPresets] = useState<EventPresetDetail[]>([]);

  useEffect(() => {
    const fetchPresets = async () => {
      if (!userDetails?.planitUserId) {
        return;
      }

      const fetchedPresets = await getPresets(userDetails.planitUserId);
      setPresets(fetchedPresets);
    };

    fetchPresets();
  }, []);

  const getPresetById = (id: number) => presets.find((p) => p.event_preset.id_event_preset === id)!;

  const onDeleteButtonClick = async (e: React.MouseEvent<Element, MouseEvent>, presetId: number) => {
    e.preventDefault();
    e.stopPropagation();

    if (!userDetails?.planitUserId) {
      return;
    }

    try {
      await deleteUserPreset(userDetails.planitUserId, presetId);

      notification.success({ message: 'Preset has been removed!', placement: 'bottom' });

      const fetchedPresets = await getPresets(userDetails.planitUserId);
      setPresets(fetchedPresets);
    } catch (e) {
      console.error(e);
    }
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
