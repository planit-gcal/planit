import { AxiosInstance } from '../../config';
import {CreateUserResponse} from "../users/users.dto";
export const getPlanItUserIdFromEmail = (email: string) =>
    AxiosInstance.get<CreateUserResponse>(`/plan-it/oauth/users/${email}`).then((response) => response.data);