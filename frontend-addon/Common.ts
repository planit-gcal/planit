function onHomepage(e: GoogleAppsScript.Addons.CommonEventObject) {
  console.log(e);
  // const hour = Number(Utilities.formatDate(new Date(), e.userTimezone.id, 'H'));
  // const message;
  // if (hour >= 6 && hour < 12) {
  //   message = 'Good morning';
  // } else if (hour >= 12 && hour < 18) {
  //   message = 'Good afternoon';
  // } else {
  //   message = 'Good night';
  // }
  // message += ' ' + e.hostApp;
  return createCard("xd", true);
}

function createCard(text: string, isHomepage = false) {
  // Create a button that changes the cat image when pressed.
  // Note: Action parameter keys and values must be strings.
  // const action = CardService.newAction()
  //   .setFunctionName("onRequestSend")
  //   .setParameters({ text, isHomepage: isHomepage.toString() });
  // const button = CardService.newTextButton()
  //   .setText("Make a request")
  //   .setOnClickAction(action)
  //   .setTextButtonStyle(CardService.TextButtonStyle.FILLED);

  const action = CardService.newAuthorizationAction().setAuthorizationUrl(
    "https://planit-custom-domain.loca.lt/"
  );
  const button = CardService.newTextButton()
    .setText("Authorize")
    .setAuthorizationAction(action);

  const buttonSet = CardService.newButtonSet().addButton(button);

  // Assemble the widgets and return the card.
  const section = CardService.newCardSection().addWidget(buttonSet);
  const card = CardService.newCardBuilder().addSection(section);

  if (!isHomepage) {
    // Create the header shown when the card is minimized,
    // but only when this card is a contextual card. Peek headers
    // are never used by non-contexual cards like homepages.
    const peekHeader = CardService.newCardHeader()
      .setTitle("Contextual Cat")
      .setImageUrl(
        "https://www.gstatic.com/images/icons/material/system/1x/pets_black_48dp.png"
      )
      .setSubtitle(text);
    card.setPeekCardHeader(peekHeader);
  }

  return card.build();
}

function onRequestSend(e: GoogleAppsScript.Addons.CommonEventObject) {
  console.log(e);
  // Get the text that was shown in the current cat image. This was passed as a
  // parameter on the Action set for the button.
  const text = e.parameters.text;

  // The isHomepage parameter is passed as a string, so convert to a Boolean.
  const isHomepage = e.parameters.isHomepage === "true";

  // Create a new card with the same text.

  const config: GoogleAppsScript.URL_Fetch.URLFetchRequestOptions = {
    muteHttpExceptions: true,
    method: "get",
    headers: {
      "Bypass-Tunnel-Reminder": "1",
    },
  };
  let response = UrlFetchApp.fetch(
    "https://planit-custom-domain.loca.lt/",
    config
  );

  const contentText = response.getContentText();
  console.log(contentText);

  console.log(ScriptApp.getOAuthToken());

  // const jsonObject = JSON.parse(response.getContentText());

  const card = createCard(response.getContentText(), isHomepage);

  // const startDate = new Date(Date.now());
  // CalendarApp.createEvent(
  //   "new event",
  //   startDate,
  //   new Date(
  //     startDate.getFullYear(),
  //     startDate.getMonth(),
  //     startDate.getDate(),
  //     startDate.getHours() + 1
  //   )
  // );

  ScriptApp.getService().getUrl();

  // Create an action response that instructs the add-on to replace
  // the current card with the new one.
  const navigation = CardService.newNavigation().updateCard(card);
  const actionResponse =
    CardService.newActionResponseBuilder().setNavigation(navigation);
  return actionResponse.build();
}

// deploy a backend to a free hosting
//
