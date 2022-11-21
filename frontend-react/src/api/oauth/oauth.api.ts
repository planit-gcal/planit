import { AxiosInstance } from '../../config';

export const getPlanItUserIdFromEmail = (email: string) =>
  AxiosInstance.get<number | null>(`/plan-it/users/emails/${email}/planit-user-id`).then((response) => response.data);
