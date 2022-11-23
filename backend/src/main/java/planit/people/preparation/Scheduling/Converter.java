package planit.people.preparation.Scheduling;

import com.google.api.services.calendar.model.TimePeriod;
import org.joda.time.Interval;
import org.springframework.web.util.UriUtils;
import planit.people.preparation.Entities.Entity_PresetAvailability;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Converter {

    public static List<Interval> covertTimePeriodsToIntervals(List<TimePeriod> timePeriods) {
        return new ArrayList<>(timePeriods.stream().map(Converter::convertTimePeriodToInterval).toList());
    }

    public static Interval convertTimePeriodToInterval(TimePeriod timePeriod) {
        return new Interval(timePeriod.getStart().getValue(), timePeriod.getEnd().getValue());
    }

    public static String decodeURLString(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

    public static String encodeString(String value) {
        return UriUtils.encodePath(value, "UTF-8");
    }

    public static Interval convertAvailabilityToInterval(Entity_PresetAvailability availability)
    {
        return new Interval(availability.getStart_available_time().getTime(), availability.getEnd_available_time().getTime());
    }
}
