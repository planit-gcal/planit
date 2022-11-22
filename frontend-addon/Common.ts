function onHomepage() {
    PropertyManager.resetProperties()
    if (!Authenticator.isAuthenticated() && !Authenticator.authenticate()) {
        return authenticationCard();
    }
    return eventCard();
}


function eventCard() {
    return CardService.newCardBuilder()
        .addSection(Components.advertisementSection())
        .addSection(Components.eventFormSection())
        .addSection(Components.userSection())
        .setFixedFooter(Components.footer())
        .build()
}

function authenticationCard() {
    return CardService.newCardBuilder()
        .addSection(Components.signUpSection())
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




