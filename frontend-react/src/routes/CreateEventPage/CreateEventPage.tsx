import { useContext } from 'react';

import { createEvent } from '../../api/calendar/calendar.api';
import { EventCreateRequest } from '../../api/calendar/calendar.dto';
import { createUserPreset } from '../../api/presets/presets.api';
import { CreateEventForm } from '../../components/CreateEventForm/CreateEventForm';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

export const CreateEventPage = () => {
  const { userDetails } = useContext(PlanitUserContext);

  const onEventSubmit = async (result: EventCreateRequest) => {
    await createEvent(result);
  };

  const onSaveToPreset = async (result: EventCreateRequest) => {
    if (!userDetails?.planitUserId) {
      return;
    }

    await createUserPreset(userDetails.planitUserId, result.event_preset_detail);
  };

  return (
    <CreateEventForm
      onSubmit={onEventSubmit}
      onSaveToPreset={onSaveToPreset}
      owner={userDetails?.ownerEmail || ''}
    ></CreateEventForm>
  );
};
