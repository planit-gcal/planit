import { useContext } from 'react';

import { CreateEventForm } from '../../components/CreateEventForm/CreateEventForm';
import { AxiosInstance } from '../../config';
import { CountStateContext } from '../../contexts/PlanitUserContext';
import { EventCreateRequest } from '../../models/event';

export const CreateEventPage = () => {
  const { ownerEmail } = useContext(CountStateContext);

  const onEventSubmit = (result: EventCreateRequest) => {
    console.log('succ: ', result);
    AxiosInstance.post('/plan-it/calendar/new-event', result)
      .then((response) => {
        console.log(response);
      })
      .catch((error) => console.log(error.message));
  };

  return (
    <div>
      <CreateEventForm onSubmit={onEventSubmit} owner={ownerEmail || ''}></CreateEventForm>
    </div>
  );
};
