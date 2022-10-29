export type EventCreateRequest = {
  summary: string;
  location: string;
  description: string;
  attendee_emails: string[];
  start_date: string;
  end_date: string;
  duration: number;
  owner_email: string;
};
