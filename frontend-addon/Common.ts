function onHomepage() {
    SetProperty(eventNameString, "PlanIt Event");
    SetProperty(minDateString, msSinceEpocToday.valueOf());
    SetProperty(maxDateString, msSinceEpocToday.valueOf() + weekInMs);
    SetProperty(durationString, "1:45");
    SetProperty(errorString, []);
    SetProperty(addUserEmailString, "")
    return createCard();
}

const weekInMs = 6.048e+8;
const durationString = "duration";
const eventNameString = "eventName"
const usersString = "Users";
const currentPresetIndexString = "currentPresetIndex";
const minDateString = "startDate";
const maxDateString = "endDate";
const errorString = "inputErrors";
const addUserEmailString = "addUserEmail"
const msSinceEpocToday = new Date();

function createCard() {

    const card = CardService.newCardBuilder();

    card.addSection(
        CardService.newCardSection()
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
    )

    const section = CardService.newCardSection();

    section.addWidget(
        CardService.newTextInput()
            .setFieldName("Event name")
            .setTitle("Event name")
            .setValue(
                GetProperty<string>(eventNameString))
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
                GetProperty<string>(durationString)
            )
            .setOnChangeAction(
                CardService.newAction()
                    .setFunctionName("onDurationChange")
            )
    )

    if (isError(error.durationFormat)) {
        section.addWidget(durationFormatError());
    }

    section.addWidget(
        CardService.newDatePicker()
            .setFieldName("Min date")
            .setTitle("Min date")
            .setValueInMsSinceEpoch(
                GetProperty<number>(minDateString)
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
                GetProperty<number>(maxDateString)
            )
            .setOnChangeAction(
                CardService.newAction()
                    .setFunctionName("onMaxDateChange")
            )
    )

    if (isError(error.date)) {
        section.addWidget(dateError());
    }

    const fixedFooter =
        CardService
            .newFixedFooter()
            .setPrimaryButton(
                CardService
                    .newTextButton()
                    .setText("Create")
                    .setOnClickAction(
                        CardService
                            .newAction()
                            .setFunctionName("onDeleteUser")
                    ));
    section.addWidget(presetDropdown())
    card.addSection(section);
    card.addSection(buildUserSection())
    card.setFixedFooter(fixedFooter);

    return card.build();
}

function buildUserSection(): GoogleAppsScript.Card_Service.CardSection {
    const users = GetProperty<Guest[]>(usersString);

    const section = CardService.newCardSection()
        .setHeader("Guests")

    section.addWidget(
        CardService.newTextInput()
            .setFieldName("new user email")
            .setTitle("add user")
            .setValue(GetProperty<string>(addUserEmailString))
            .setOnChangeAction(
                CardService.newAction()
                    .setFunctionName("onNewUserName")
            )
    )

    if(isError(error.email))
    {
        section.addWidget(emailError());
    }
    else
    {
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
            .setSelected(user.isRequired);

        console.log(`${user.email} ${user.isRequired}`);


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

function presetDropdown() {
    const presets = getPresetsFromStorage();
    const currentIndex = GetProperty<Number>(currentPresetIndexString);

    const presetChangeAction = CardService.newAction()
        .setFunctionName("onPresetChange");

    const dropdown = CardService.newSelectionInput()
        .setFieldName("Preset")
        .setTitle("Choose preset")
        .setType(CardService.SelectionInputType.DROPDOWN)
        .setOnChangeAction(presetChangeAction)

    presets.forEach((x, i) => dropdown.addItem(x.name, i, i === currentIndex))
    return dropdown
}

function update() {
    const updateNavigation = CardService.newNavigation().popToRoot().updateCard(createCard());
    return CardService.newActionResponseBuilder().setNavigation(updateNavigation).build();
}




