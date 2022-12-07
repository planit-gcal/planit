import { Select } from 'antd';
import { DefaultOptionType } from 'antd/lib/select';
import { useContext, useEffect, useState } from 'react';

import { EventPresetDetail } from '../../api/calendar/calendar.dto';
import { getPresets } from '../../api/presets/presets.api';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

type PresetSelectProps = {
  onApplyPreset: (preset: EventPresetDetail) => void;
};

export const PresetSelect = ({ onApplyPreset }: PresetSelectProps) => {
  const { userDetails, setUserDetails } = useContext(PlanitUserContext);
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

  const options: DefaultOptionType[] = presets.map((p) => ({
    label: p.event_preset.name,
    value: p.event_preset.id_event_preset,
  }));

  const onSelect = (presetId: number) => {
    onApplyPreset(presets.find((p) => p.event_preset.id_event_preset === presetId)!);
  };

  return <Select placeholder="Select preset" options={options} onSelect={onSelect} />;
};
