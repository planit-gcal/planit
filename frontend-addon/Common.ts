function onHomepage() {
    PropertyManager.resetProperties()
    if (!Authenticator.isAuthenticated() && !Authenticator.authenticate()) {
        return authenticationCard();
    }
    return eventCard();
}


function eventCard() {
    return CardService.newCardBuilder()
        .addSection(advertisementSection())
        .addSection(eventFormSection())
        .addSection(userSection())
        .setFixedFooter(footer())
        .build()
}

function authenticationCard() {
    return CardService.newCardBuilder()
        .addSection(signUpSection())
        .build();
}

function update() {
    return CardService
        .newActionResponseBuilder()
        .setNavigation(
            CardService
                .newNavigation()
                .popToRoot()
                .updateCard(eventCard())
        )
        .build();
}




