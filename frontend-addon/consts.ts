const defaultPreset: PresetDetails =
    {
        "event_preset": {
            "name": "PlanIt Event",
            "break_into_smaller_events": false,
        },
        "guests": [],
        "preset_availability": [
            {
                "day": "MONDAY",
                "start_available_time": "08:00",
                "end_available_time": "20:00",
                "day_off": false
            },
            {
                "day": "TUESDAY",
                "start_available_time": "08:00",
                "end_available_time": "20:00",
                "day_off": false
            },
            {
                "day": "WEDNESDAY",
                "start_available_time": "08:00",
                "end_available_time": "20:00",
                "day_off": false
            },
            {
                "day": "THURSDAY",
                "start_available_time": "08:00",
                "end_available_time": "20:00",
                "day_off": false
            },
            {
                "day": "FRIDAY",
                "start_available_time": "08:00",
                "end_available_time": "20:00",
                "day_off": false
            },
            {
                "day": "SATURDAY",
                "start_available_time": "08:00",
                "end_available_time": "20:00",
                "day_off": false
            },
            {
                "day": "SUNDAY",
                "start_available_time": "08:00",
                "end_available_time": "20:00",
                "day_off": false
            }
        ]
    }

const MAINURL = "https://planit-custom-domain.loca.lt"
// const MAINURL = "https://tidy-turtle-22.loca.lt"
// const MAINURL = "http://localhost:8080"
const weekInMs = 6.048e+8;
const durationString = "duration";
const eventNameString = "eventName"
const usersString = "Users";
const currentPresetIndexString = "currentPresetIndex";
const minDateString = "startDate";
const maxDateString = "endDate";
const errorString = "inputErrors";
const addUserEmailString = "addUserEmail";
const presetString = "Presets";
const msSinceEpocToday = new Date();
