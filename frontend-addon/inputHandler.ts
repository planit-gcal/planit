function onEventNameChange(e) {
    const newName = e['formInputs']["Event name"][0]
    PropertyManager.setProperty(eventNameString, newName);
}

function isEmailValid(email: string): boolean {
    return email.endsWith("@gmail.com") || email.endsWith("@student.pwr.edu.pl");
}

function onNewUserName(e) {
    let readEmail = ""
    try {
        readEmail = e['formInputs']["new user email"][0];
    } catch (e) {
        return;
    }
    PropertyManager.setProperty(addUserEmailString, readEmail);
    const isValid = isEmailValid(readEmail);
    if (ErrorHandler.UpdateError(error.email, !isValid)) {
        return update();
    }
}

function onAddUser(e) {
    let users = PropertyManager.getProperty<Guest[]>(usersString);

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

    PropertyManager.setProperty(usersString, users);

    return update();
}

function onDeleteUser(e) {
    const userIndex = Number(e['commonEventObject']["parameters"]["deleteIndex"]);
    const users = PropertyManager.getProperty<string[]>(usersString);
    users.splice(userIndex, 1);
    PropertyManager.setProperty(usersString, users);
    return update();
}

function onPresetChange(e) {
    const presetIndex = Number(e["formInputs"]["Preset"]);
    console.log("On preset change")
    console.log({presetIndex})
    const presets = Storage.getPresets();
    console.log({presets})
    PropertyManager.setProperty(currentPresetIndexString, presetIndex);
    Storage.updateGuests(presets, presetIndex)
    return update();
}

function onMinDateChange(e) {
    const minDate = Number(e["formInputs"]["Min date"][0]["msSinceEpoch"]);
    const maxDate = Number(e["formInputs"]["Max date"][0]["msSinceEpoch"]);
    PropertyManager.setProperty(minDateString, minDate);
    if (ErrorHandler.UpdateError(error.date, minDate >= maxDate)) {
        return update()
    }
}

function onMaxDateChange(e) {
    const minDate = Number(e["formInputs"]["Min date"][0]["msSinceEpoch"]);
    const maxDate = Number(e["formInputs"]["Max date"][0]["msSinceEpoch"]);
    PropertyManager.setProperty(maxDateString, maxDate);
    if (ErrorHandler.UpdateError(error.date, minDate >= maxDate)) {
        return update()
    }
}

function onDurationChange(e) {
    const input = e["formInputs"]["duration"][0];
    console.log(input);
    PropertyManager.setProperty(durationString, input);

    const regex = RegExp("^(([0-9]?[0-9]):)?([0-5][0-9])$");
    const groups = regex.exec(input);
    console.log("groups")
    console.log(groups)

    if (ErrorHandler.UpdateError(error.durationFormat, !groups)) {
        return update();
    }
}

function onCreateButtonPressed(e) {
    const finalJson = FinalJsonCreator.createEventJSON(e);
    API.createEvent(finalJson);
}
