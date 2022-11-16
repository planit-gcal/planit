enum error {
    durationFormat = "durationFormat",
    date = "date",
    email = "email"
}


type EventPreset = EventPresetBreakIntoSmaller | EventPresetNoBreakIntoSmaller;

type EventPresetNoBreakIntoSmaller = {
    name: string;
    break_into_smaller_events: false;
}

type EventPresetBreakIntoSmaller = {
    name: string;
    break_into_smaller_events: true;
    min_length_of_single_event: number;
    max_length_of_single_event: number;
}


type Guest = {
    email: string;
    obligatory: boolean;
}

type PresetAvailability = PresetAvailabilityDayOff | PresetAvailabilityNoDayOff

type PresetAvailabilityDayOff =
    {
        day: string;
        day_off: true;
    }

type PresetAvailabilityNoDayOff = {
    day: string;
    start_available_time: string;
    end_available_time: string;
    day_off: false;
}

type PresetDetails = {
    event_preset: EventPreset;
    guests: Guest[];
    preset_availability: PresetAvailability[];
}

type CreateEventDTO = {
    name: string;
    summary: string;
    location: string;
    description: string;
    event_preset_detail: PresetDetails;
    owner_email: string;
    start_date: string;
    end_date: string;
    duration: number;
}
