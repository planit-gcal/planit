import { AxiosResponse } from 'axios';

import { AxiosInstance } from '../../config';
import { CreateUserRequest, CreateUserResponse, GetUserEmailsResponse } from './users.dto';

export const createNewUser = (googleAuthCode: string) =>
  AxiosInstance.post<CreateUserRequest, AxiosResponse<CreateUserResponse>>('/plan-it/users', {
    code: googleAuthCode,
    planit_user_id: null,
  }).then((response) => response.data);

export const createNewAssignedUser = (googleAuthCode: string, planitUserId: number) =>
  AxiosInstance.post<CreateUserRequest, AxiosResponse<CreateUserResponse>>('/plan-it/users', {
    code: googleAuthCode,
    planit_user_id: planitUserId,
  }).then((response) => response.data);

export const getUserEmails = (planitUserId: number) =>
  AxiosInstance.get<GetUserEmailsResponse>(`/plan-it/users/${planitUserId}/emails`).then((response) => response.data);
