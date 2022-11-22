class InputHandler {
    static onEventNameChange(e) {
        const newName = e['formInputs']["Event name"][0]
        PropertyManager.setProperty(eventNameString, newName);
    }

    static isEmailValid(email: string): boolean {
        return email.endsWith("@gmail.com");
    }

    static onNewUserName(e) {
        let readEmail = ""
        try {
            readEmail = e['formInputs']["new user email"][0];
        } catch (e) {
            return;
        }
        PropertyManager.setProperty(addUserEmailString, readEmail);
        const isValid = InputHandler.isEmailValid(readEmail);
        if (ErrorHandler.UpdateError(error.email, !isValid)) {
            return update();
        }
    }

    static onAddUser(e) {
        let users = PropertyManager.getProperty<Guest[]>(usersString);

        console.log(e);

        let readEmail = ""
        try {
            readEmail = e['formInputs']["new user email"][0];
        } catch (e) {
            return;
        }

        if (!InputHandler.isEmailValid(readEmail)) {
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

    static onDeleteUser(e) {
        const userIndex = Number(e['commonEventObject']["parameters"]["deleteIndex"]);
        const users = PropertyManager.getProperty<string[]>(usersString);
        users.splice(userIndex, 1);
        PropertyManager.setProperty(usersString, users);
        return update();
    }

    static onPresetChange(e) {
        const presetIndex = Number(e["formInputs"]["Preset"]);
        console.log("On preset change")
        console.log({presetIndex})
        const presets = getPresetsFromStorage();
        console.log({presets})
        PropertyManager.setProperty(currentPresetIndexString, presetIndex);
        updateGuests(presets, presetIndex)
        return update();
    }

    static onMinDateChange(e) {
        const minDate = Number(e["formInputs"]["Min date"][0]["msSinceEpoch"]);
        const maxDate = Number(e["formInputs"]["Max date"][0]["msSinceEpoch"]);
        PropertyManager.setProperty(minDateString, minDate);
        if (ErrorHandler.UpdateError(error.date, minDate >= maxDate)) {
            return update()
        }
    }

    static onMaxDateChange(e) {
        const minDate = Number(e["formInputs"]["Min date"][0]["msSinceEpoch"]);
        const maxDate = Number(e["formInputs"]["Max date"][0]["msSinceEpoch"]);
        PropertyManager.setProperty(maxDateString, maxDate);
        if (ErrorHandler.UpdateError(error.date, minDate >= maxDate)) {
            return update()
        }
    }

    static onDurationChange(e) {
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

    static onCreateButtonPressed(e) {
        const finalJson = FinalJsonCreator.createEventJSON(e);
        API.createEvent(finalJson);
    }
}