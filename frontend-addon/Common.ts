function onHomepage() {
    const check = PropertiesService.getUserProperties().getProperty("Users");
    if (!check) {
        const constUsers = ["Kuba", "Marcin", "Łukasz", "Mustafa"]
        PropertiesService.getUserProperties().setProperty("Users", JSON.stringify(constUsers));
    }
    return createCard();
}

const weekInMs = 6.048e+8;
const usersString = "Users";

function createCard() {

    const card = CardService.newCardBuilder();
    const header = CardService.newCardHeader().setTitle("Create new event");
    const section = CardService.newCardSection();
    const msSinceEpocToday = new Date();
    const array = ["my friends", "work", "family"]

    section.addWidget(
        CardService.newTextInput()
            .setFieldName("Event name")
            .setTitle("Event name")
            .setValue("Planit Event")
    )

    section.addWidget(
        CardService.newDatePicker()
            .setFieldName("Min date")
            .setTitle("Min date")
            .setValueInMsSinceEpoch(msSinceEpocToday.valueOf())
    )

    section.addWidget(
        CardService.newDatePicker()
            .setFieldName("Max date")
            .setTitle("Max date")
            .setValueInMsSinceEpoch(msSinceEpocToday.valueOf() + weekInMs)
    )

    const dropdown = CardService.newSelectionInput()
        .setFieldName("Preset")
        .setTitle("Choose preset")
        .setType(CardService.SelectionInputType.DROPDOWN);
    array.forEach(x => dropdown.addItem(x, x, false))
    section.addWidget(dropdown)

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
                            .setFunctionName("deleteUser")
                    ));


    card.setHeader(header);
    card.addSection(section);
    card.addSection(buildUserSection())
    card.setFixedFooter(fixedFooter);

    return card.build();
}

function buildUserSection(): GoogleAppsScript.Card_Service.CardSection {
    const users = GetProperty<string[]>(usersString);

    const section = CardService.newCardSection()
        .setHeader("Guests")
        .setCollapsible(true)
        .setNumUncollapsibleWidgets(3);

    section.addWidget(
        CardService.newTextInput()
            .setFieldName("new user email")
            .setTitle("add user")
    )

    section.addWidget(
        CardService.newTextButton()
            .setText("add user")
            .setOnClickAction(CardService.newAction()
                .setFunctionName("onAddUser")))

    for (let i = 0; i < users.length; i++) {
        const user = users[i];
        const emptyAction = CardService.newAction()
            .setFunctionName("deleteUser")
            .setParameters({"index": i.toString()});

        section.addWidget(
            CardService.newKeyValue()
                .setContent(user)
                .setOnClickAction(emptyAction)
                .setButton(
                    CardService.newTextButton()
                        .setText("X")
                        .setOnClickAction(emptyAction)
                )
        );
    }

    return section;
}

function deleteUser(e) {
    const userIndex = Number(e['commonEventObject']["parameters"]["index"]);
    const users = GetProperty<string[]>(usersString);
    users.splice(userIndex, 1);
    SetProperty(usersString, users);
    return update();
}

function onAddUser(e) {
    let users = GetProperty<string[]>(usersString);

    const newUser = e['formInputs']["add user"][0];
    users = [newUser, ...users];

    SetProperty(usersString, users);

    return update();
}

function update() {
    const updateNavigation = CardService.newNavigation().popToRoot().updateCard(createCard());
    return CardService.newActionResponseBuilder().setNavigation(updateNavigation).build();
}


