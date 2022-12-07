package planit.people.preparation.Scheduling;

import org.joda.time.*;
import planit.people.preparation.Entities.Entity_PresetAvailability;

import java.util.*;

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
        List<Interval> availableOfDuration = filterIntervalsToMatchDuration(available, duration);
        return getAvailableIntervalsBasedOnPresetAvailability(availableOfDuration, availabilities);
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
        List<Interval> filtered = filterIntervals(busyTime, start, end);
        filtered.sort(new IntervalStartComparator());
        List<Interval> merged = mergeSortedIntervals(filtered);
        return getFreeIntervalsFromMergedIntervals(merged, start, end);
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
     * @param mergedIntervals List of <strong>non-overlapping and sorted</strong> intervals being "busy".
     * @param start           The soonest date the event should be scheduled at. <strong>When inverting, interval starting from this date will be created if possible.</strong>
     * @param end             The latest date the event should be scheduled at. <strong>When inverting, interval ending at this date will be created if possible.</strong>
     * @return Inverted list of intervals provided. Might additionally add intervals starting at start or ending at end. Might return empty.
     */
    private static List<Interval> getFreeIntervalsFromMergedIntervals(List<Interval> mergedIntervals, DateTime start, DateTime end) {
        List<Interval> freeIntervals = new ArrayList<>();

        Interval lastFree = new Interval(start, end);
        if (mergedIntervals.size() > 0) {
            Interval firstBusy = mergedIntervals.get(0);
            if (firstBusy.isAfter(start)) {
                Interval firstFree = new Interval(start, firstBusy.getStart());
                freeIntervals.add(0, firstFree);
            }
            Interval lastBusy = mergedIntervals.get(mergedIntervals.size() - 1);
            if (lastBusy.isBefore(end)) {
                lastFree = new Interval(lastBusy.getEnd(), end);
            }
        }

        for (int i = 0; i < mergedIntervals.size() - 1; i++) {
            DateTime freeStart = mergedIntervals.get(i).getEnd();
            DateTime freeEnd = mergedIntervals.get(i + 1).getStart();
            freeIntervals.add(new Interval(freeStart, freeEnd));
        }

        freeIntervals.add(lastFree);

        return freeIntervals;
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
    private static List<Interval> filterIntervals(List<Interval> intervals, DateTime start, DateTime end) {
        List<Interval> clampedIntervals = new ArrayList<>();
        for (Interval current : intervals) {
            if (!current.isBefore(start) && !current.isAfter(end)) {
                clampedIntervals.add(current);
            }
        }
        return clampedIntervals;
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
        if (first.isBefore(second)) {
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
     * @see Schedular#splitInterval(Interval, Duration)
     */
    private static List<Interval> filterIntervalsToMatchDuration(List<Interval> available, Duration duration) {
        var filtered = new ArrayList<Interval>();
        for (Interval interval : available) {
            filtered.addAll(splitInterval(interval, duration));
        }
        return filtered;
    }

    private static List<Interval> getAvailableIntervalsBasedOnPresetAvailability(List<Interval> freeIntervals, List<Entity_PresetAvailability> availabilities) {
        var availableIntervals = new ArrayList<Interval>();
        var mappedAvailabilities = mapAvailabilitiesToDaysOfWeek(availabilities);
        for (Interval freeInterval : freeIntervals) {
            var dayOfWeek = freeInterval.getStart().dayOfWeek().get();
            var availabilityForDay = mappedAvailabilities.get(dayOfWeek);
            if (doesTimeOverlap(availabilityForDay, freeInterval)) {
                availableIntervals.add(freeInterval);
            }
        }
        return availableIntervals;
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
