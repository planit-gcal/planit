import { formatInTimeZone } from 'date-fns-tz';

export const formatToUTC = (date: string) => {
  return formatInTimeZone(new Date(date), 'UTC', 'yyyy-MM-dd HH:mm:ss');
};
