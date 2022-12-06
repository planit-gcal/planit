package planit.people.preparation.Scheduling;

import com.google.api.services.calendar.model.TimePeriod;
import org.joda.time.Interval;
import org.springframework.web.util.UriUtils;
import planit.people.preparation.Entities.Entity_PresetAvailability;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    public static Interval convertAvailabilityToInterval(Entity_PresetAvailability availability) {
        return new Interval(availability.getStart_available_time().getTime(), availability.getEnd_available_time().getTime());
    }

    public static Map<String, Map<Long, List<Interval>>> convertFutureMapToIntervalMap(Map<String, Map<Long, List<CompletableFuture<List<Interval>>>>> futureMap) {
        Map<String, Map<Long, List<Interval>>> result = new HashMap<>();
        for (String key : futureMap.keySet()) {
            result.put(key, new HashMap<>());
            for (Long id : futureMap.get(key).keySet()) {
                List<Interval> intervals = new ArrayList<>();
                futureMap.get(key).get(id).forEach(v -> {
                    try {
                        intervals.addAll(v.get());
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
                result.get(key).put(id, intervals);
            }
        }
        return result;
    }

    public static List<SchedulingInfo> convertIntervalMapToListOfSchedulingInfo(Map<String, Map<Long, List<Interval>>> freeIntervalsForAllUsers) {
        List<SchedulingInfo> result = new ArrayList<>();
        for (String key : freeIntervalsForAllUsers.keySet()) {
            for (Long id : freeIntervalsForAllUsers.get(key).keySet()) {
                if (key.equals("required")) {
                    result.add(new SchedulingInfo(true, freeIntervalsForAllUsers.get(key).get(id)));
                } else {
                    result.add(new SchedulingInfo(false, freeIntervalsForAllUsers.get(key).get(id)));
                }
            }
        }
        return result;
    }
}
