function isAuthenticated() : boolean
{
    const token = GetProperty<string>(userTokenString);
    return !!token && token !== "";
}

function getId() : string
{
    return GetProperty<string>(userTokenString)
}

function setId(id : string)
{
    SetProperty(usersString, id);
}

function authenticate() : boolean
{
    const userEmail = Session.getActiveUser().getEmail();
    const response = getIdFromMail(userEmail);
    console.log("response")
    console.log(response.toString())
    const loginResponse = JSON.parse(response.toString()) as LoginResponse;
    const id = loginResponse.planit_user_id;
    if(id === null)
    {
        console.log("id is null")
        return false;
    }
    console.log({id})
    SetProperty(userTokenString, id);
    return true;
}

function authenticationCard()
{
    const url = MAINURL;
    const onClose = CardService.OnClose.RELOAD_ADD_ON;
    const openLink = CardService.newOpenLink().setUrl(url).setOnClose(onClose).setOpenAs(CardService.OpenAs.OVERLAY);
    const card = CardService.newCardBuilder();
    const section = CardService.newCardSection()
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
    card.addSection(section)
    return card.build();
}