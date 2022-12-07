import { AxiosResponse } from 'axios';

import { AxiosInstance } from '../../config';
import { EventPresetDetail } from '../calendar/calendar.dto';
import { CreatePresetRequest, CreatePresetResponse, GetPresetsResponse } from './presets.dto';

export const getPresets = (planitUserId: number) =>
  AxiosInstance.get<GetPresetsResponse>(`/plan-it/calendar/users/${planitUserId}/presets`).then(
    (response) => response.data
  );

export const createUserPreset = (planitUserId: number, preset: EventPresetDetail) =>
  AxiosInstance.post<CreatePresetRequest, AxiosResponse<CreatePresetResponse>>(
    `/plan-it/calendar/users/${planitUserId}/presets`,
    preset
  ).then((response) => response.data);

export const updateUserPreset = (planitUserId: number, preset: EventPresetDetail, presetId: number) =>
  AxiosInstance.patch<CreatePresetRequest, AxiosResponse<CreatePresetResponse>>(
    `/plan-it/calendar/users/${planitUserId}/presets/${presetId}`,
    preset
  ).then((response) => response.data);

export const deleteUserPreset = (planitUserId: number, presetId: number) =>
  AxiosInstance.delete<CreatePresetRequest, AxiosResponse<CreatePresetResponse>>(
    `/plan-it/calendar/users/${planitUserId}/presets/${presetId}`
  ).then((response) => response.data);
