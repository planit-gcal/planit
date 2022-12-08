import { notification } from 'antd';
import { useContext } from 'react';

import { createEvent } from '../../api/calendar/calendar.api';
import { EventCreateRequest } from '../../api/calendar/calendar.dto';
import { createUserPreset, updateUserPreset } from '../../api/presets/presets.api';
import { CreateEventForm } from '../../components/CreateEventForm/CreateEventForm';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';
import { PresetsContext } from '../../contexts/PresetsContext';

export const CreateEventPage = () => {
  const { userDetails } = useContext(PlanitUserContext);
  const { createOrUpdatePreset } = useContext(PresetsContext);

  const onEventSubmit = async (result: EventCreateRequest) => {
    await createEvent(result);
  };

  return (
    <CreateEventForm
      onSubmit={onEventSubmit}
      onSaveToPreset={createOrUpdatePreset}
      owner={userDetails?.ownerEmail || ''}
    ></CreateEventForm>
  );
};
