package planit.people.preparation.Responses;

import com.google.api.client.util.DateTime;

public record CalendarResponse(DateTime startDate, DateTime endDate) {
}
