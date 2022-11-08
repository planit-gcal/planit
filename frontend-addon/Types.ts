type Guest = {
    email : string,
    isRequired : boolean
}

type Settings = {
    startDate: Date,
    endDate : Date,
    durationInMinutes : number,
    numberOfEvents : number,
    minTimeBetweenEventsInMinutes : number,
    maxTimeBetweenEventsInMinutes : number,
    breakIntoSmallerEvents : boolean,
    minLengthOfSingleEventInMinutes : number,
    maxLengthOfSingleEventInMinutes : number,
}

type Preset = {
    name : string,
    settings : Settings,
    guests : Guest[]
}

enum error
{
    durationFormat = "durationFormat",
    date = "date",
    email = "email"
}

/*
"number_of_events": 3,
    "min_time_between_events": 999,
    "max_time_between_events": 322,
    "break_into_smaller_events": false,
    "min_length_of_single_event": 23,
    "max_length_of_single_event": 23
},
*/

export interface EventPreset {
    id_event_preset: number;
    name: string;
    break_into_smaller_events: boolean;
    min_length_of_single_event?: any;
    max_length_of_single_event?: any;
}

export interface Guest {
    id_event_guest: number;
    entity_EventPreset?: any;
    email: string;
    obligatory: boolean;
}

export interface PresetAvailability {
    id_preset_availability: number;
    entity_EventPreset?: any;
    day: string;
    start_available_time: string;
    end_available_time: string;
    day_off: boolean;
}

export interface RootObject {
    event_preset: EventPreset;
    guests: Guest[];
    preset_availability: PresetAvailability[];
}