import { AxiosResponse } from 'axios';

import { AxiosInstance } from '../../config';
import { EventCreateRequest, EventCreateResponse } from './calendar.dto';

export const createEvent = (event: EventCreateRequest) =>
  AxiosInstance.post<EventCreateRequest, AxiosResponse<EventCreateResponse>>('/plan-it/calendar/events', event).then(
    (response) => response.data
  );
