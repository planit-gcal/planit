import { useContext } from 'react';

import { createEvent } from '../../api/calendar/calendar.api';
import { EventCreateRequest } from '../../api/calendar/calendar.dto';
import { CreateEventForm } from '../../components/CreateEventForm/CreateEventForm';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

export const CreateEventPage = () => {
  const { userDetails } = useContext(PlanitUserContext);

  const onEventSubmit = async (result: unknown) => {
    console.log('succ: ', result);

    await createEvent(result as EventCreateRequest);
  };

  return <CreateEventForm onSubmit={onEventSubmit} owner={userDetails?.ownerEmail || ''}></CreateEventForm>;
};
