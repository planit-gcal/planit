package planit.people.preparation.Scheduling;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.TimePeriod;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Converter {

    public static List<Interval> convertEventsToIntervals(Events events) {
        return events.getItems().stream().map(Converter::convertEventToInterval).toList();
    }

    public static Vector<Interval> covertTimePeriodsToIntervals(List<TimePeriod> timePeriods) {
        return new Vector<>(timePeriods.stream().map(Converter::convertTimePeriodToInterval).toList());
    }

    public static Interval convertEventToInterval(Event event) {
        return new Interval(event.getStart().getDateTime().getValue(), event.getEnd().getDateTime().getValue());
    }

    public static Interval convertTimePeriodToInterval(TimePeriod timePeriod){
        return new Interval(timePeriod.getStart().getValue(), timePeriod.getEnd().getValue());
    }
}
