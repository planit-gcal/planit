import { AxiosInstance } from '../../config';
import { EventCreateRequest } from './calendar.dto';

export const createEvent = (event: EventCreateRequest) =>
  AxiosInstance.post('/plan-it/calendar/events', event).then((response) => response.data);
