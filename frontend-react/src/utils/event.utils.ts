import { format, parse } from 'date-fns';

import { EventCreateRequest, EventPresetDetail } from '../api/calendar/calendar.dto';
import { ExcludeForm, GeneralForm, GoogleEventForm } from '../components/CreateEventForm/models';

const capitalize = (word: string) => {
  const firstLetter = word.charAt(0);

  const firstLetterCap = firstLetter.toUpperCase();

  const remainingLetters = word.slice(1).toLowerCase();

  return firstLetterCap + remainingLetters;
};

const colors = [
  { value: '#DC2127', id:11 },
  { value: '#FF887C', id:4 },
  { value: '#FFB878', id:6 },
  { value: '#FBD75B', id:5 },
  { value: '#7AE7BF', id:2 },
  { value: '#51B749', id:10 },
  { value: '#46D6DB', id:7 },
  { value: '#5484ED', id:9 },
  { value: '#A4BDFC', id:1},
  { value: '#DBADFF', id:3 },
  { value: '#e1e1e1', id:8 },
];

const getIdByHex = (hex:string) => {
  return colors.find(row => row.value === hex)?.id;
}

const getHexById = (id:number) => {
  return colors.find(row => row.id === id)?.value;
}

export const convertFormToRequest = (
  owner_email: string,
  general: GeneralForm,
  googleEvent: GoogleEventForm,
  exclude: ExcludeForm
): EventCreateRequest => ({
  name: general.name,
  summary: general.name,
  location: googleEvent.event_location || '',
  description: googleEvent.event_description || '',
  color: getIdByHex(googleEvent.event_color) || 9,
  event_preset_detail: {
    event_preset: {
      name: general.name,
      // dummy data because BE doesn't support these fields
      break_into_smaller_events: false,
      min_length_of_single_event: null,
      max_length_of_single_event: null,
      shared_presets: [],
    },
    guests: general.guests.map((g) => ({
      email: g.email,
      obligatory: g.obligatory,
    })),
    preset_availability: exclude.excludeWeekDays.map((d) => ({
      day: d.name.toUpperCase() as any,
      day_off: d.exclude,
      start_available_time: format(d.availability[0], 'HH:mm'),
      end_available_time: format(d.availability[1], 'HH:mm'),
    })),
  },
  owner_email,
  start_date: format(general.event_between[0], 'yyyy-MM-dd HH:mm:ss'),
  end_date: format(general.event_between[1], 'yyyy-MM-dd HH:mm:ss'),
  duration: general.duration.getHours() * 60 + general.duration.getMinutes(),
});

export const getPartialFormFromPreset = (
  preset: EventPresetDetail
): {
  generalForm: Partial<GeneralForm>;
  excludeForm: Partial<ExcludeForm>;
} => {
  const generalForm: Partial<GeneralForm> = {
    name: preset.event_preset.name,
    guests: preset.guests,
  };

  const excludeForm: Partial<ExcludeForm> = {
    excludeWeekDays: preset.preset_availability
      .map((availability) => ({
        name: capitalize(availability.day),
        exclude: availability.day_off,
        availability: [
          parse(availability.start_available_time, 'HH:mm:ss', new Date()),
          parse(availability.end_available_time, 'HH:mm:ss', new Date()),
        ] as [Date, Date],
      }))
      .slice()
      .reverse(),
  };

  return { generalForm, excludeForm };
};
