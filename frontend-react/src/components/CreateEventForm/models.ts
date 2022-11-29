export type GeneralForm = {
  name: string;
  duration: Date;
  event_between: [Date, Date];
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
  duration_of_event: [Date, Date];
  duration_of_event_unit: number;
};

export type ExcludeForm = {
  excludeWeekDays: { name: string; exclude: boolean; availability: [Date, Date] }[];
};
