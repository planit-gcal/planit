function onCalendarEventOpen(e: GoogleAppsScript.Addons.CommonEventObject) {
  console.log(e);

  return createCard("My card", true);
  //   var calendar = CalendarApp.getCalendarById(e.calendar.calendarId);
  //   // The event metadata doesn't include the event's title, so using the
  //   // calendar.readonly scope and fetching the event by it's ID.
  //   var event = calendar.getEventById(e.calendar.id);
  //   if (!event) {
  //     // This is a new event still being created.
  //     return createCatCard("A new event! Am I invited?");
  //   }
  //   var title = event.getTitle();
  //   // If neccessary, truncate the title to fit in the image.
  //   title = truncate(title);
  //   return createCatCard(title);
}
