function createEventJSON(e) {
    const createEventDTO: CreateEventDTO = {
        description: "Event created with PlanIt! how cool",
        duration: getDuration(e),
        end_date: GetProperty<string>(maxDateString),
        event_preset_detail: {
            event_preset: getCurrentPresetFromStorage().event_preset,
            guests: GetProperty<Guest[]>(usersString),
            preset_availability: convertAvailabilities()
        },
        location: "",
        name: GetProperty<string>(eventNameString),
        owner_email: getOwnerEmail(),
        start_date: GetProperty<string>(minDateString),
        summary: ""
    }
    console.log(createEventDTO);
    return createEventDTO;
}

function getDuration(e): number {
    const input = e["formInputs"]["duration"][0];
    const regex = RegExp("^(([0-9]?[0-9]):)?([0-5][0-9])$");
    const groups = regex.exec(input);
    if (!groups) {
        return 0;
    }
    let hoursString = groups[2]
    if (hoursString === undefined) {
        hoursString = "0";
    }
    const minuteString = groups[3]
    return Number(hoursString) * 60 + Number(minuteString);
}

function getOwnerEmail() : string {
    return Session.getActiveUser().getEmail();
}

function convertAvailabilities()
{
    const presetAvailabilities = getCurrentPresetFromStorage().preset_availability;
    presetAvailabilities.forEach(presetAvailability => {
        if(presetAvailability.day_off === false)
        {
            const casted = presetAvailability as PresetAvailabilityNoDayOff
            casted.end_available_time = sliceLastThreeCharacters(casted.end_available_time);
            casted.start_available_time = sliceLastThreeCharacters(casted.start_available_time);
        }
    })
    return presetAvailabilities;
}

function sliceLastThreeCharacters(text : string) : string
{
    if(text.length === 8)
    {
        return text.slice(0, text.length - 3);
    }
    return text;
}