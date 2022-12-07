import { notification } from 'antd';
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';

import { EventCreateRequest, EventPresetDetail } from '../api/calendar/calendar.dto';
import { createUserPreset, deleteUserPreset, getPresets, updateUserPreset } from '../api/presets/presets.api';
import { PlanitUserContext } from './PlanitUserContext';

type State = {
  presets: EventPresetDetail[];
  deletePreset: (presetId: number) => void;
  createOrUpdatePreset: (event: EventCreateRequest) => void;
};

export const PresetsContext = createContext<State>({} as State);

type PresetsContextProviderProps = { children: React.ReactNode };
export const PresetsContextProvider = ({ children }: PresetsContextProviderProps) => {
  const { userDetails } = useContext(PlanitUserContext);
  const [presets, setPresets] = useState<EventPresetDetail[]>([]);

  const fetchPresets = useCallback(async () => {
    if (!userDetails?.planitUserId) {
      return;
    }

    const fetchedPresets = await getPresets(userDetails.planitUserId);
    setPresets(fetchedPresets);
  }, [userDetails?.planitUserId]);

  useEffect(() => {
    fetchPresets();
  }, [fetchPresets]);

  const deletePreset = useCallback(
    async (presetId: number) => {
      if (!userDetails?.planitUserId) {
        return;
      }

      try {
        await deleteUserPreset(userDetails.planitUserId, presetId);
        await fetchPresets();

        notification.success({ message: 'Preset has been removed!', placement: 'bottom' });
      } catch (e) {
        console.error(e);
      }
    },
    [fetchPresets, userDetails?.planitUserId]
  );

  const createOrUpdatePreset = useCallback(
    async (event: EventCreateRequest) => {
      if (!userDetails?.planitUserId) {
        return;
      }

      if (event.event_preset_detail.event_preset.id_event_preset) {
        try {
          await updateUserPreset(
            userDetails.planitUserId,
            event.event_preset_detail,
            event.event_preset_detail.event_preset.id_event_preset
          );
          await fetchPresets();

          notification.success({ message: 'Preset has been updated!', placement: 'bottom' });
        } catch (e) {
          console.error(e);
        }
      } else {
        try {
          await createUserPreset(userDetails.planitUserId, event.event_preset_detail);
          await fetchPresets();

          notification.success({ message: 'Preset has been created!', placement: 'bottom' });
        } catch (e) {
          console.error(e);
        }
      }
    },
    [fetchPresets, userDetails?.planitUserId]
  );

  const value = useMemo(
    () => ({
      presets,
      deletePreset,
      createOrUpdatePreset,
    }),
    [presets, deletePreset, createOrUpdatePreset]
  );

  return <PresetsContext.Provider value={value}>{children}</PresetsContext.Provider>;
};
