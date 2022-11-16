function empty() {

}

function onEventNameChange(e) {
    const newName = e['formInputs']["Event name"][0]
    SetProperty(eventNameString, newName);
}

function isEmailValid(email: string): boolean {
    return email.endsWith("@gmail.com");
}

function onNewUserName(e) {
    let readEmail = ""
    try {
        readEmail = e['formInputs']["new user email"][0];
    } catch (e) {
        return;
    }
    SetProperty(addUserEmailString, readEmail);
    const isValid = isEmailValid(readEmail);
    if (UpdateError(error.email, !isValid)) {
        return update();
    }
}

function onAddUser(e) {
    let users = GetProperty<Guest[]>(usersString);

    console.log(e);

    let readEmail = ""
    try {
        readEmail = e['formInputs']["new user email"][0];
    } catch (e) {
        return;
    }

    if (!isEmailValid(readEmail)) {
        return;
    }

    const newUser: Guest = {
        email: readEmail,
        obligatory: true
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

function onPresetChange(e) {
    const presetIndex = Number(e["formInputs"]["Preset"]);
    const presets = getPresetsFromStorage();
    SetProperty(currentPresetIndexString, presetIndex);
    updateGuests(presets, presetIndex)
    return update();
}

function onMinDateChange(e) {
    const minDate = Number(e["formInputs"]["Min date"][0]["msSinceEpoch"]);
    const maxDate = Number(e["formInputs"]["Max date"][0]["msSinceEpoch"]);
    SetProperty(minDateString, minDate);
    if (UpdateError(error.date, minDate >= maxDate)) {
        return update()
    }
}

function onMaxDateChange(e) {
    const minDate = Number(e["formInputs"]["Min date"][0]["msSinceEpoch"]);
    const maxDate = Number(e["formInputs"]["Max date"][0]["msSinceEpoch"]);
    SetProperty(maxDateString, maxDate);
    if (UpdateError(error.date, minDate >= maxDate)) {
        return update()
    }
}

function onDurationChange(e) {
    const input = e["formInputs"]["duration"][0];
    console.log(input);
    SetProperty(durationString, input);

    const regex = RegExp("^(([0-9]?[0-9]):)?([0-5][0-9])$");
    const groups = regex.exec(input);
    console.log("groups")
    console.log(groups)

    if (UpdateError(error.durationFormat, !groups)) {
        return update();
    }
}

function onRequiredChange(e) {
    console.log("e")
    console.log(e)
    const index = Number(e['commonEventObject']["parameters"]["isRequiredIndex"]);
    console.log("index")
    console.log(index.toString())
    const guests = getCurrentPresetFromStorage().guests
    console.log("guests")
    console.log(guests)
    guests[index].obligatory = !guests[index].obligatory;
    console.log("updatedGuests")
    console.log(guests)
    SetProperty(usersString, guests);
    return update();
}

function onCreateButtonPressed(e)
{
    const finalJson = createEventJSON(e);
    createEvent(finalJson);
}