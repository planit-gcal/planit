export type GeneralForm = {
  name: string;
  duration: number;
  event_between: [string, string];
  guests: { email: string; obligatory: boolean }[];
};

export type GoogleEventForm = {
  event_description: string;
  event_location: string;
  event_color: string;
};

export type SearchForm = {
  num_of_events: number;
  time_between_events: [number, number];
  break_event: boolean;
  duration_of_event: [number, number];
  duration_of_event_unit: number;
};

export type ExcludeForm = {
  days: { name: string; exclude: boolean; availability: [number, number] }[];
};
