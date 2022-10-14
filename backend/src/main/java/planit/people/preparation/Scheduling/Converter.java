package planit.people.preparation.Scheduling;

import com.google.api.services.calendar.model.TimePeriod;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public static List<Interval> covertTimePeriodsToIntervals(List<TimePeriod> timePeriods) {
        return new ArrayList<>(timePeriods.stream().map(Converter::convertTimePeriodToInterval).toList());
    }

    public static Interval convertTimePeriodToInterval(TimePeriod timePeriod) {
        return new Interval(timePeriod.getStart().getValue(), timePeriod.getEnd().getValue());
    }
}
