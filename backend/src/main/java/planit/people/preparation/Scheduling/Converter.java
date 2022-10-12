package planit.people.preparation.Scheduling;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.joda.time.Interval;

import java.util.List;

public class Converter {
    public static List<Interval> convertEventsToIntervals(Events events) {
        return events.getItems().stream().map(Converter::convertEventToInterval).toList();
    }

    public static Interval convertEventToInterval(Event event) {
        return new Interval(event.getStart().getDateTime().getValue(), event.getEnd().getDateTime().getValue());
    }
}
