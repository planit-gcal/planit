
function empty()
{

}

function onEventNameChange(e)
{
    const newName = e['formInputs']["Event name"][0]
    SetProperty(eventNameString, newName);
}

function onAddUser(e) {
    let users = GetProperty<Guest[]>(usersString);

    console.log(e);

    const newUser : Guest = {
        email : e['formInputs']["new user email"][0],
        isRequired : true,
    };
    users = [newUser, ...users];

    SetProperty(usersString, users);

    return update();
}

function onDeleteUser(e) {
    const userIndex = Number(e['commonEventObject']["parameters"]["deleteIndex"]);
    const users = GetProperty<string[]>(usersString);
    users.splice(userIndex, 1);
    SetProperty(usersString, users);
    return update();
}

function onPresetChange(e)
{
    const presetIndex = Number(e["formInputs"]["Preset"]);
    const presets = getPresets();
    const preset = presets[presetIndex];
    const guests = preset.guests;
    SetProperty(usersString, guests);
    SetProperty(currentPresetIndexString, presetIndex);
    return update();
}

function onMinDateChange(e)
{
    console.log(e);
    const newDate = Number(e["formInputs"]["Min date"][0]["msSinceEpoch"]);
    SetProperty(minDateString, newDate);
}

function onMaxDateChange(e)
{
    console.log(e);
    const newDate = Number(e["formInputs"]["Max date"][0]["msSinceEpoch"]);
    SetProperty(maxDateString, newDate);
}

function onDurationChange(e)
{
    const input = e["formInputs"]["duration"][0];
    console.log(input);
    SetProperty(durationString, input);
}

function onRequiredChange(e)
{
    console.log("e")
    console.log(e)
    const index = Number(e['commonEventObject']["parameters"]["isRequiredIndex"]);
    console.log("index")
    console.log(index.toString())
    const guests = getCurrentPresetFromStorage().guests
    console.log("guests")
    console.log(guests)
    guests[index].isRequired = !guests[index].isRequired;
    console.log("updatedGuests")
    console.log(guests)
    SetProperty(usersString, guests);
    return update();
}