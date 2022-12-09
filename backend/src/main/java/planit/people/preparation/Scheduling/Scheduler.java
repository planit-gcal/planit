package planit.people.preparation.Scheduling;

import org.jetbrains.annotations.Nullable;
import org.joda.time.*;
import planit.people.preparation.Entities.Entity_PresetAvailability;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class used for finding time intervals fitting provided parameters
 */
public final class Scheduler {

    /**
     * Wraps {@link #getAllAvailable(List, DateTime, DateTime)}.
     * Additionally, filters the free intervals based on:
     * 1. Preset availability (if free time is during non-available time it is disregarded)
     * 2. Duration (if free interval is shorter than duration it is disregarded)
     * @param busyTime Time intervals during which the time is marked as busy.
     * @param duration Actual duration of time the event should take.
     * @param start    The soonest date the event should be scheduled at.
     * @param end      The date that scheduled event should not end after.
     * @return <b>Sorted list</b> of all intervals matching the parameters or empty.
     * @see org.joda.time.Interval
     * @see org.joda.time.Duration
     * @see org.joda.time.DateTime
     */
    public static List<Interval> getAvailableTimeSlots(List<Interval> busyTime, Duration duration, DateTime start, DateTime end, List<Entity_PresetAvailability> availabilities) {
        List<Interval> available = getAllAvailable(busyTime, start, end);
        List<Interval> splitByDay = splitIntervalsByDay(available);
        List<Interval> filteredByAvailability = getAvailableIntervalsBasedOnPresetAvailability(splitByDay, availabilities);
        List<Interval> intervalsOfDuration = filterIntervalsToMatchDuration(filteredByAvailability, duration);
        return intervalsOfDuration;
    }

    /**
     * Converts provided "busy" intervals to free intervals and <b>sorts them</b>.
     * Filters the free intervals based on the start and the end date (time beyond start and end date is disregarded).
     * Merges all overlapping intervals.
     * Sorts all intervals.
     * @param busyTime Time intervals during which the time is marked as busy.
     * @param start    The soonest date the event should be scheduled at.
     * @param end      The latest date the event should be scheduled at.
     * @return <b>Sorted list</b> of all intervals matching the parameters or empty.
     */
    public static List<Interval> getAllAvailable(List<Interval> busyTime, DateTime start, DateTime end) {
        List<Interval> filtered = filterIntervals(busyTime, start, end)
                .sorted(new IntervalStartComparator())
                .collect(Collectors.toList());
        List<Interval> merged = mergeSortedIntervals(filtered);
        List<Interval> free = invertIntervals(merged, start, end);
        return free;
    }

    /**
     * Finds intervals of total length equal to provided duration. Might return multiple intervals of one minute. Might cut last interval short to match duration.
     *
     * @param available List of intervals of <strong>free</strong> time.
     * @param duration  Duration of the event.
     * @return Intervals of total duration equal to duration or empty.
     */
    private static List<Interval> getIntervalsOfTotalDuration(List<Interval> available, Duration duration) {
        Duration totalDuration = Duration.ZERO;
        List<Interval> fittingDuration = new ArrayList<>();

        for (Interval interval : available) {
            Duration currentDuration = totalDuration.plus(interval.toDuration());
            int compareTo = currentDuration.compareTo(duration);
            if (compareTo < 0) {
                fittingDuration.add(interval);
                totalDuration = currentDuration;
            } else if (compareTo == 0) {
                fittingDuration.add(interval);
                return fittingDuration;
            } else {
                Interval shortenedInterval = new Interval(interval.getStart(), duration.minus(totalDuration));
                fittingDuration.add(shortenedInterval);
                return fittingDuration;
            }
        }
        return new ArrayList<>();
    }


    /**
     * "Inverts" a list of intervals provided to get "free" time.
     *
     * @param intervals List of <strong>non-overlapping and sorted</strong> intervals being "busy".
     * @param start
     * @param end
     * @return Inverted list of intervals provided. Might additionally add intervals starting at start or ending at end. Might return empty.
     */
    private static List<Interval> invertIntervals(List<Interval> intervals, DateTime start, DateTime end) {
        if(intervals.isEmpty())
        {
            return List.of(new Interval(start, end));
        }

        var inverted = new ArrayList<Interval>();

        // If the first interval starts after the Start date, we need to "fill" that time with free interval
        var firstIntervalStart = intervals.get(0).getStart();
        if(start.isBefore(firstIntervalStart))
        {
            inverted.add(new Interval(start, firstIntervalStart));
        }
        // If the last interval ends before the End date, we need to "fill" that time with free interval
        var lastIntervalEnd = intervals.get(intervals.size() - 1).getEnd();
        if(end.isAfter(lastIntervalEnd))
        {
            inverted.add(new Interval(lastIntervalEnd, end));
        }

        // Loop that gets two intervals, finds the gap between then and adds that gap as "free" time
        for (int i = 0; i < intervals.size() - 1; i++) {
            Interval previousInterval = intervals.get(i);
            Interval nextInterval = intervals.get(i + 1);
            inverted.add(previousInterval.gap(nextInterval));
        }

        return inverted;
    }


    /**
     * Merges provided intervals. "Merge" means join any overlapping intervals together, leaving no overlaps. Provided interval list must be <strong>sorted</strong>.
     *
     * @param intervals list of <strong>sorted</strong> intervals
     * @return merged list of intervals
     */
    private static List<Interval> mergeSortedIntervals(List<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            return intervals;
        }

        List<Interval> mergedIntervals = new ArrayList<>();
        mergedIntervals.add(intervals.get(0));

        for (int i = 1; i < intervals.size(); i++) {
            Interval first = mergedIntervals.remove(mergedIntervals.size() - 1);
            Interval second = intervals.get(i);
            if (first.overlaps(second)) {
                Interval merged = merge(first, second);
                mergedIntervals.add(merged);
            } else {
                mergedIntervals.add(first);
                mergedIntervals.add(second);
            }
        }
        return mergedIntervals;
    }


    /**
     * Filters the given intervals so that only the intervals between start and end are returned. If any interval goes beyond boundaries, it will be cut.
     *
     * @param intervals List of intervals to filter.
     * @param start     The date before which no event can start.
     * @param end       The date after which no event can end.
     * @return List of intervals starting on or after start and ending before or at end. Might return empty.
     */
    private static Stream<Interval> filterIntervals(List<Interval> intervals, DateTime start, DateTime end) {
        return intervals.stream()
                .filter(current -> !current.isBefore(start) && !current.isAfter(end));
    }


    /**
     * Joins two intervals into one. <strong>Order of arguments does not matter.</strong>
     *
     * @param first  interval to merge
     * @param second interval to merge
     * @return Interval starting at earliest date from provided intervals and ending at latest date from provided intervals.
     */
    private static Interval merge(Interval first, Interval second) {
        if (first.contains(second)) {
            return first;
        }
        if (second.contains(first)) {
            return second;
        }
        if (first.getStart().isBefore(second.getStart())) {
            return new Interval(first.getStart(), second.getEnd());
        } else {
            return new Interval(second.getStart(), first.getEnd());
        }
    }

    /**
     * Returns an interval of matching duration. Cuts the interval short if needed. Returns null if not found
     *
     * @param available List of "free" intervals too chose from.
     * @param duration  The duration of event.
     * @return An interval from list of exact duration or null.
     * @see #splitInterval(Interval, Duration)
     */
    private static List<Interval> filterIntervalsToMatchDuration(List<Interval> available, Duration duration) {
        return available.stream().filter(interval -> !interval.toDuration().isShorterThan(duration)).toList();
    }

    private static List<Interval> getAvailableIntervalsBasedOnPresetAvailability(List<Interval> freeIntervals, List<Entity_PresetAvailability> availabilities) {
        var availableIntervals = new ArrayList<Interval>();
        var mappedAvailabilities = mapAvailabilitiesToDaysOfWeek(availabilities);
        for (Interval freeInterval : freeIntervals) {
            var dayOfWeek = freeInterval.getStart().dayOfWeek().get();
            var availabilityForDay = mappedAvailabilities.getOrDefault(dayOfWeek, null);
            // If is day off
            if (availabilityForDay == null) {
                continue;
            }
            var fittingInterval = tryFitIntervalIntoAvailability(freeInterval, availabilityForDay);
            if (fittingInterval != null) {
                availableIntervals.add(fittingInterval);
            }
        }
        return availableIntervals;
    }

    /**
     * Divides list of intervals into intervals such that no interval is starting at a different day than it is ending.
     * If an interval is starting at monday and finishing at wednesday, it will be split into three intervals
     * @param freeIntervals list of intervals that will be split
     * @return List of intervals not crossing midnight
     */
    private static List<Interval> splitIntervalsByDay(List<Interval> freeIntervals) {
        var freeIntervalsSplitByDay = new ArrayList<Interval>();
        for (Interval interval :
                freeIntervals) {
            while (interval.getStart().getDayOfWeek() != interval.getEnd().getDayOfWeek())
            {
                var start = interval.getStart();
                var endOfDay = new DateTime(start.getYear(), start.getMonthOfYear(), start.getDayOfMonth(), 23, 59, 59);
                var newInterval = new Interval(interval.getStart(), endOfDay);
                freeIntervalsSplitByDay.add(newInterval);
                interval = new Interval(endOfDay.plusSeconds(1), interval.getEnd());
            }
            freeIntervalsSplitByDay.add(interval);
        }
        return freeIntervalsSplitByDay;
    }

    /**
     * Attempts to fit free interval in to availability for the day.
     * Only takes {@link LocalTime} into consideration.
     * If the free interval overlaps the availability but exceeds it, it will be cut to not exceed availability
     * @param freeInterval The interval that will be checked to match availability and possibly clamped to fit it
     * @param availabilityForDay The interval that will be checked against. Stays unchanged.
     * @return Interval that does not exceed available time or null
     */
    private static @Nullable Interval tryFitIntervalIntoAvailability(Interval freeInterval, Interval availabilityForDay) {

        var intervalStartTime = LocalTime.fromDateFields(freeInterval.getStart().toDate());
        var intervalEndTime = LocalTime.fromDateFields(freeInterval.getEnd().toDate());
        var availableStartTime = LocalTime.fromDateFields(availabilityForDay.getStart().toDate());
        var availableEndTime = LocalTime.fromDateFields(availabilityForDay.getEnd().toDate());

        var newStart = freeInterval.getStart();
        var newEnd = freeInterval.getEnd();

        if(intervalStartTime.isBefore(availableStartTime))
        {
            newStart = new DateTime(newStart.getYear(), newStart.getMonthOfYear(), newStart.getDayOfMonth(), availableStartTime.getHourOfDay(), availableStartTime.getMinuteOfHour(), availableStartTime.getSecondOfMinute());
        }
        if(intervalEndTime.isAfter(availableEndTime))
        {
            newEnd = new DateTime(newEnd.getYear(), newEnd.getMonthOfYear(), newEnd.getDayOfMonth(), availableEndTime.getHourOfDay(), availableEndTime.getMinuteOfHour(), availableEndTime.getSecondOfMinute());
        }
        if(newStart.isBefore(newEnd))
        {
            return new Interval(newStart, newEnd);
        }
        return null;
    }


    /**
     * Check if the time portion of the free interval overlaps with the preset availability interval. 
     * 
     * @param availabilityInterval event availability preset interval 
     * @param dayInterval free interval 
     * @return true iff the start time of the free interval is the same or after the start time of the preset availability interval AND the end time of the free interval is the same or before the end time of the preset availability interval 
     */
    private static Boolean doesTimeOverlap(Interval availabilityInterval, Interval dayInterval) {
        var compareTimeAfter = DateTimeComparator.getTimeOnlyInstance().compare(availabilityInterval.getStart(), dayInterval.getStart());
        var compareTimeBefore = DateTimeComparator.getTimeOnlyInstance().compare(availabilityInterval.getEnd(), dayInterval.getEnd());
        return compareTimeAfter <= 0 && compareTimeBefore >= 0;
    }

    private static Map<Integer, Interval> mapAvailabilitiesToDaysOfWeek(List<Entity_PresetAvailability> availabilities) {
        HashMap<Integer, Interval> map = new HashMap<Integer, Interval>();
        for (Entity_PresetAvailability availability :
                availabilities) {
            if(availability.getDay_off()){
                continue;
            }
            switch (availability.getDay()) {
                case MONDAY ->
                        map.putIfAbsent(DateTimeConstants.MONDAY, Converter.convertAvailabilityToInterval(availability));
                case TUESDAY ->
                        map.putIfAbsent(DateTimeConstants.TUESDAY, Converter.convertAvailabilityToInterval(availability));
                case WEDNESDAY ->
                        map.putIfAbsent(DateTimeConstants.WEDNESDAY, Converter.convertAvailabilityToInterval(availability));
                case THURSDAY ->
                        map.putIfAbsent(DateTimeConstants.THURSDAY, Converter.convertAvailabilityToInterval(availability));
                case FRIDAY ->
                        map.putIfAbsent(DateTimeConstants.FRIDAY, Converter.convertAvailabilityToInterval(availability));
                case SATURDAY ->
                        map.putIfAbsent(DateTimeConstants.SATURDAY, Converter.convertAvailabilityToInterval(availability));
                case SUNDAY ->
                        map.putIfAbsent(DateTimeConstants.SUNDAY, Converter.convertAvailabilityToInterval(availability));
                default -> {
                }
            }
        }
        return map;
    }

    private static class IntervalStartComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval x, Interval y) {
            return x.getStart().compareTo(y.getStart());
        }
    }

    /**
     * Slice an interval into a list of intervals splitted by the duration
     * 
     * @param interval the interval to be sliced
     * @param duration the duration to be used to determine the size of each slice. 
     * @return List<Interval> a list of sliced intervals. 
     */
    private static List<Interval> splitInterval(Interval interval, Duration duration) {
        DateTime startTime = interval.getStart();
        DateTime endTime = interval.getEnd();
        List<Interval> result = new ArrayList<>();
        while (true) {
            if (interval.toDuration().compareTo(duration) >= 0) {
                result.add(new Interval(startTime, duration));
                startTime = startTime.plus(duration.toDuration());
                interval = new Interval(startTime, endTime);
            } else {
                break;
            }
        }
        return result;
    }
}
