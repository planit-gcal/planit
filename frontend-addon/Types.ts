type Guest = {
    email : string,
    isRequired : boolean,
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
