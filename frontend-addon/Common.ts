function onHomepage() {
  var check = PropertiesService.getUserProperties().getProperty("Users");
  if(!check)
  {
    const constUsers = ["Kuba", "Marcin", "Łukasz", "Mustafa"]
    PropertiesService.getUserProperties().setProperty("Users", JSON.stringify(constUsers));
  }
  return createCard();
}

const weekInMs = 6.048e+8;

function createCard(listOfUsers : string[] = []) {
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

  card.addSection(buildUserSection())
  card.addSection(section);
  card.setHeader(header);
  return card.build();
}

function buildUserSection() : GoogleAppsScript.Card_Service.CardSection
{

  // if(users === undefined || users.length === 0)
  // {
  //   console.log("users is undefined")
  //   users = constUsers;
  // }

  const beforeJson = PropertiesService.getUserProperties().getProperty("Users");
  const users = JSON.parse(beforeJson) as string[];
  console.log("received by method " + users)

  const section = CardService.newCardSection()
      .setHeader("Guests")
      .setCollapsible(true)

  for (let i = 0; i < users.length; i++){
    const user = users[i];
    console.log(user)
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
          .setOnClickAction(CardService.newAction()
              .setFunctionName("onAddUser")))

  console.log("at the end of method " + users)

  return section;
}

function onAddUser(e)
{
  const beforeJson = PropertiesService.getUserProperties().getProperty("Users");
  const users = JSON.parse(beforeJson);

  console.log("before add user " + users);

  const newUser = e['formInputs']["add user"][0];
  users.push(newUser)

  console.log("after add users " + users);

  PropertiesService.getUserProperties().setProperty("Users", JSON.stringify(users));

  var refreshNav = CardService.newNavigation().popToRoot().updateCard(createCard());
  return CardService.newActionResponseBuilder().setNavigation(refreshNav).build();
}
