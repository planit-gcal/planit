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

function authenticate()
{
    const userEmail = Session.getActiveUser().getEmail();
    const response = getIdFromMail(userEmail);
    console.log("response")
    console.log(response.toString())
}

function authenticationCard()
{
    const url = ""
    const b = CardService.OnClose.RELOAD_ADD_ON;
    const a = CardService.newOpenLink().setUrl(url).setOnClose(b);
    const card = CardService.newCardBuilder();
    CardService.newCardSection()
        .setHeader("Sign up")
        .addWidget(
            CardService.newTextButton()
                .setText("Open sign in popup")
                .setOpenLink(a)
        )

}