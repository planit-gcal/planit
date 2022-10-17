package planit.people.preparation.Scheduling;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scheduler {

    public static Interval getOneTimeSlotBetweenDatesOfLength(List<Interval> busyTime, Duration duration, DateTime start, DateTime end) {
        List<Interval> available = getAllAvailable(busyTime, start, end);
        return getFirstIntervalMatchingDuration(available, duration);
    }

    public static List<Interval> getAvailableTimeSlotsBetweenDatesOfTotalLength(List<Interval> busyTime, Duration duration, DateTime start, DateTime end) {
        List<Interval> available = getAllAvailable(busyTime, start, end);
        return getIntervalsOfTotalDuration(available, duration);

    }

    private static List<Interval> getAllAvailable(List<Interval> busyTime, DateTime start, DateTime end) {
        List<Interval> filtered = filterIntervals(busyTime, start, end);
        filtered.sort(new IntervalStartComparator());
        List<Interval> merged = mergeSortedIntervals(filtered);
        return getFreeIntervalsFromMergedIntervals(merged, start, end);
    }

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

    private static List<Interval> filterIntervals(List<Interval> intervals, DateTime start, DateTime end) {
        List<Interval> clampedIntervals = new ArrayList<>();
        for (Interval current : intervals) {
            if (!current.isBefore(start) && !current.isAfter(end)) {
                clampedIntervals.add(current);
            }
        }
        return clampedIntervals;
    }

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

    private static Interval getFirstIntervalMatchingDuration(List<Interval> available, Duration duration) {
        for (Interval interval : available) {
            Duration intervalDuration = interval.toDuration();
            if (intervalDuration.compareTo(duration) >= 0) {
                return new Interval(interval.getStart(), duration);
            }
        }
        return null;
    }


    public static class IntervalStartComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval x, Interval y) {
            return x.getStart().compareTo(y.getStart());
        }
    }
}
