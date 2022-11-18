function GetProperty<T>(name : string) : T
{
   const unparsed = PropertiesService.getUserProperties().getProperty(name);
    return JSON.parse(unparsed);
}

function SetProperty(name : string, value : unknown)
{
    const parsed = JSON.stringify(value);
    PropertiesService.getUserProperties().setProperty(name, parsed);
}

function resetProperties()
{
    SetProperty(eventNameString, "PlanIt Event");
    SetProperty(minDateString, msSinceEpocToday.valueOf());
    SetProperty(maxDateString, msSinceEpocToday.valueOf() + weekInMs);
    SetProperty(durationString, "1:45");
    SetProperty(errorString, []);
    SetProperty(addUserEmailString, "");
    SetProperty(presetString, Array.of(defaultPreset));
    SetProperty(usersString, []);
    SetProperty(currentPresetIndexString, 0)
}