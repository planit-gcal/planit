function onHomepage() {
  return createCard();
}

const weekInMs = 6.048e+8;
const constUsers = ["Kuba", "Marcin", "Łukasz", "Mustafa"]

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

  console.log("list passed to method " + listOfUsers)
  card.addSection(buildUserSection(listOfUsers))
  card.addSection(section);
  card.setHeader(header);
  return card.build();
}

function buildUserSection(users : string[]) : GoogleAppsScript.Card_Service.CardSection
{
  console.log("received by method " + users)

  if(users === undefined || users === [])
  {
    console.log("users is undefined")
    users = constUsers;
  }

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

  var json = {}
  json['users'] = JSON.stringify(users);

  console.log(json)
  section.addWidget(
      CardService.newTextButton()
          .setText("add user")
          .setOnClickAction(CardService.newAction()
              .setFunctionName("onAddUser")
              .setParameters(json)))
  console.log("5");

  console.log("at the end of method " + users)

  return section;
}

function onAddUser(e)
{
  const users = JSON.parse(e['parameters']['users']);

  console.log("button received ");
  console.log(e);
  console.log("before add user " + users);

  const newUser = e['formInputs']["add user"][0];
  users.push(newUser)
  e['listOfUsers'] = users;
  console.log("after add users " + users);
  const json = {};
  json['listOfUsers'] = users;
  var refreshNav = CardService.newNavigation().popToRoot().updateCard(createCard(users));
  CardService.newActionResponseBuilder().setNavigation(refreshNav).build();
}