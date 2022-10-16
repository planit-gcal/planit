function onHomepage(e) {
  console.log(e);
  // var hour = Number(Utilities.formatDate(new Date(), e.userTimezone.id, 'H'));
  // var message;
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

/**
 * Creates a card with an image of a cat, overlayed with the text.
 * @param {String} text The text to overlay on the image.
 * @param {Boolean} isHomepage True if the card created here is a homepage;
 *      false otherwise. Defaults to false.
 * @return {CardService.Card} The assembled card.
 */
function createCard(text, isHomepage = false, imgUrl = null) {
  // Create a button that changes the cat image when pressed.
  // Note: Action parameter keys and values must be strings.
  var action = CardService.newAction()
    .setFunctionName("onRequestSend")
    .setParameters({ text: text, isHomepage: isHomepage.toString() });
  var button = CardService.newTextButton()
    .setText("Make a request")
    .setOnClickAction(action)
    .setTextButtonStyle(CardService.TextButtonStyle.FILLED);
  var buttonSet = CardService.newButtonSet().addButton(button);

  // Assemble the widgets and return the card.
  var section = CardService.newCardSection().addWidget(buttonSet);
  var card = CardService.newCardBuilder().addSection(section);

  if (!isHomepage) {
    // Create the header shown when the card is minimized,
    // but only when this card is a contextual card. Peek headers
    // are never used by non-contexual cards like homepages.
    var peekHeader = CardService.newCardHeader()
      .setTitle("Contextual Cat")
      .setImageUrl(
        "https://www.gstatic.com/images/icons/material/system/1x/pets_black_48dp.png"
      )
      .setSubtitle(text);
    card.setPeekCardHeader(peekHeader);
  }

  return card.build();
}

/**
 * Callback for the "Change cat" button.
 * @param {Object} e The event object, documented {@link
 *     https://developers.google.com/gmail/add-ons/concepts/actions#action_event_objects
 *     here}.
 * @return {CardService.ActionResponse} The action response to apply.
 */
function onRequestSend(e) {
  console.log(e);
  // Get the text that was shown in the current cat image. This was passed as a
  // parameter on the Action set for the button.
  var text = e.parameters.text;

  // The isHomepage parameter is passed as a string, so convert to a Boolean.
  var isHomepage = e.parameters.isHomepage === "true";

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

  // var jsonObject = JSON.parse(response.getContentText());

  var card = createCard(response.getContentText(), isHomepage);

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
  var navigation = CardService.newNavigation().updateCard(card);
  var actionResponse =
    CardService.newActionResponseBuilder().setNavigation(navigation);
  return actionResponse.build();
}

// deploy a backend to a free hosting
//
