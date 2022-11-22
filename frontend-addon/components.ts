function presetDropdown() {
    const presets = getPresetsFromStorage();

    const currentIndex = PropertyManager.getProperty<number>(currentPresetIndexString);

    const presetChangeAction = CardService.newAction()
        .setFunctionName("onPresetChange");

    const dropdown = CardService.newSelectionInput()
        .setFieldName("Preset")
        .setTitle("Choose preset")
        .setType(CardService.SelectionInputType.DROPDOWN)
        .setOnChangeAction(presetChangeAction)

    console.log("Presets")
    console.log(presets)

    presets.forEach((x, i) => dropdown.addItem(x.event_preset.name, i, i === currentIndex))
    return dropdown
}

function userSection(): GoogleAppsScript.Card_Service.CardSection {
    const users = PropertyManager.getProperty<Guest[]>(usersString);

    console.log("Users")
    console.log(users)

    const section = CardService.newCardSection()
        .setHeader("Guests")

    section.addWidget(
        CardService.newTextInput()
            .setFieldName("new user email")
            .setTitle("add user")
            .setValue(PropertyManager.getProperty<string>(addUserEmailString))
            .setOnChangeAction(
                CardService.newAction()
                    .setFunctionName("onNewUserName")
            )
    )

    if (ErrorHandler.isError(error.email)) {
        section.addWidget(emailError());
    } else {
        section.addWidget(
            CardService.newTextButton()
                .setText("add user")
                .setOnClickAction(CardService.newAction()
                    .setFunctionName("onAddUser")))

    }


    let icon = CardService.newIconImage()
        .setIcon(CardService.Icon.EMAIL)
        .setAltText('Send an email');

    const cardSectionGrid = CardService.newGrid()
        .setNumColumns(2)
        .addItem(
            CardService.newGridItem()
                .setTitle("User email")
                .setTextAlignment(CardService.HorizontalAlignment.START)
        )
        .addItem(
            CardService.newGridItem()
                .setSubtitle("Required")
                .setTextAlignment(CardService.HorizontalAlignment.END)
        );

    section.addWidget(cardSectionGrid);


    for (let i = 0; i < users.length; i++) {
        const user = users[i];
        const deleteUserAction = CardService.newAction()
            .setFunctionName("onDeleteUser")
            .setParameters({"deleteIndex": i.toString()})

        const checkBox = CardService.newSwitch()
            .setControlType(CardService.SwitchControlType.CHECK_BOX)
            .setFieldName(`isRequired${i}`)
            .setValue(`isRequired${i}`)
            .setSelected(user.obligatory);

        section.addWidget(
            CardService.newDecoratedText()
                .setText(user.email)
                .setBottomLabel('click to remove')
                .setStartIcon(icon)
                .setWrapText(false)
                .setSwitchControl(checkBox)
                .setOnClickAction(deleteUserAction)
        );
    }

    return section;
}

function eventFormSection() {
    const section = CardService.newCardSection();

    section.addWidget(
        CardService.newTextInput()
            .setFieldName("Event name")
            .setTitle("Event name")
            .setValue(
                PropertyManager.getProperty<string>(eventNameString))
            .setOnChangeAction(
                CardService.newAction()
                    .setFunctionName("onEventNameChange")
            )
    )

    section.addWidget(
        CardService.newTextInput()
            .setFieldName("duration")
            .setTitle("Event Duration")
            .setValue(
                PropertyManager.getProperty<string>(durationString)
            )
            .setOnChangeAction(
                CardService.newAction()
                    .setFunctionName("onDurationChange")
            )
    )

    if (ErrorHandler.isError(error.durationFormat)) {
        section.addWidget(durationFormatError());
    }

    section.addWidget(
        CardService.newDatePicker()
            .setFieldName("Min date")
            .setTitle("Min date")
            .setValueInMsSinceEpoch(
                PropertyManager.getProperty<number>(minDateString)
            )
            .setOnChangeAction(
                CardService.newAction()
                    .setFunctionName("onMinDateChange")
            )
    )

    section.addWidget(
        CardService.newDatePicker()
            .setFieldName("Max date")
            .setTitle("Max date")
            .setValueInMsSinceEpoch(
                PropertyManager.getProperty<number>(maxDateString)
            )
            .setOnChangeAction(
                CardService.newAction()
                    .setFunctionName("onMaxDateChange")
            )
    )

    if (ErrorHandler.isError(error.date)) {
        section.addWidget(dateError());
    }

    section.addWidget(presetDropdown())

    return section;
}

function advertisementSection() {
    return CardService.newCardSection()
        .addWidget(
            CardService.newTextParagraph()
                .setText("Need more options or new presets? For those and <b>many other functionalities</b>, visit our website")
        )
        .addWidget(
            CardService.newTextButton()
                .setText("Open PlanIt.com")
                .setOpenLink(
                    CardService.newOpenLink()
                        .setUrl("planit.com")
                        .setOpenAs(CardService.OpenAs.OVERLAY)
                )
        )
}

function footer() {
    return CardService
        .newFixedFooter()
        .setPrimaryButton(
            CardService
                .newTextButton()
                .setText("Create")
                .setOnClickAction(
                    CardService
                        .newAction()
                        .setFunctionName("onCreateButtonPressed")
                ));
}

function signUpSection()
{
    const url = MAINURL;
    const onClose = CardService.OnClose.RELOAD_ADD_ON;
    const openLink = CardService.newOpenLink().setUrl(url).setOnClose(onClose).setOpenAs(CardService.OpenAs.OVERLAY);

    return CardService.newCardSection()
        .setHeader("Sign up")
        .addWidget(
            CardService.newTextButton()
                .setText("Open sign in popup")
                .setOpenLink(openLink)
        )
        .addWidget(
            CardService.newTextParagraph()
                .setText("Log in screen?")
        )
}

function emailError() {
    return ErrorHandler.errorText("This is not valid email format")
}

function durationFormatError() {
    return ErrorHandler.errorText("Duration must be in 00:00 format")
}

function dateError() {
    return ErrorHandler.errorText("End date cannot be before start date")
}