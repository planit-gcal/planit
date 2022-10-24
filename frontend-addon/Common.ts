function onHomepage(e) {
  return createCard();
}

const weekInMs = 6.048e+8;
const users = ["Kuba", "Marcin", "Łukasz", "Mustafa"]

function createCard() {
  var card = CardService.newCardBuilder();
  var header = CardService.newCardHeader().setTitle("Create new event");

  var section = CardService.newCardSection();

  section.addWidget(
      CardService.newTextInput()
          .setFieldName("Event name")
          .setTitle("Event name")
          .setValue("Planit Event")
  )

  const msSinceEpocToday = new Date();

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

  const array = ["my friends", "work", "family"]

  array.forEach(x => dropdown.addItem(x, x, false))

  section.addWidget(dropdown)


  card.addSection(section);
  card.setHeader(header);
  return card.build();
}

function buildUserSection(users : [string]) : GoogleAppsScript.Card_Service.CardSection
{
  const section = CardService.newCardSection()
      .setHeader("Guests")
      .setCollapsible(true)

  for (let i = 0; i < users.length; i++){
    const user = users[i];
    section.addWidget(
        CardService.newTextParagraph().setText(user)
    )
  }

  section.addWidget(
      CardService.newTextInput()
          .setFieldName("new user email")
          .setFieldName("add user")
          .setHint("user@gmail.com")
  )

  section.addWidget(
      CardService.newTextButton()
          .setText("add user")
          .setOnClickAction()
  )

  return section;
}