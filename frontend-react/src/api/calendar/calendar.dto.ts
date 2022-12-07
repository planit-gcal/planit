export const Days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'] as const;

export type Day = typeof Days[number];
type EventPreset = {
  id_event_preset?: number;
  name: string;
  break_into_smaller_events: boolean;
  min_length_of_single_event: number | null; // in minutes
  max_length_of_single_event: number | null; // in minutes
  shared_presets: []; // dunno what's that
};
type Guest = { email: string; obligatory: boolean };
type PresetAvailability = {
  day: Day;
  start_available_time: string; // "09:00:00"
  end_available_time: string; // "16:00:00"
  day_off: boolean;
};

export type EventPresetDetail = {
  event_preset: EventPreset;
  guests: Guest[];
  preset_availability: PresetAvailability[];
};

export type EventCreateRequest = {
  name: string;
  summary: string;
  location: string;
  description: string;
  event_preset_detail: EventPresetDetail;
  owner_email: string;
  start_date: string; // '2022-01-10 12:00:00'
  end_date: string; // '2022-02-10 12:00:00'
  duration: number; // in minutes
};
