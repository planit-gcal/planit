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

export type ExcludeForm = {
  excludeWeekDays: { name: string; exclude: boolean; availability: [Date, Date] }[];
};
