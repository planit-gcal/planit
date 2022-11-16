import { AxiosInstance } from '../../config';
import { CreateUserResponse } from '../users/users.dto';

export const getPlanItUserIdFromEmail = (email: string) =>
  AxiosInstance.get<number>(`/plan-it/users/emails/${email}/planit-user-id`).then((response) => response.data);
