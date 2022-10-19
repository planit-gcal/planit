export type EventCreateRequest = {
    summary: string,
    location: string,
    description: string,
    attendees: string[],
    start_date: string,
    end_date: string,
    duration: Number,
    owner: string
};