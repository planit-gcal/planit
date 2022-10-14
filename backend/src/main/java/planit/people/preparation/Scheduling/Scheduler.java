package planit.people.preparation.Scheduling;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

public class Scheduler {

    public static Interval GetOneTimeSlotBetweenDatesOfLength(Vector<Interval> busyTime, Duration duration, DateTime start, DateTime end) {
        Vector<Interval> available = GetAllAvailable(busyTime, start, end);
        return GetFirstIntervalMatchingDuration(available, duration);
    }

    public static Vector<Interval> GetAvailableTimeSlotsBetweenDatesOfTotalLength(Vector<Interval> busyTime, Duration duration, DateTime start, DateTime end) {
        Vector<Interval> available = GetAllAvailable(busyTime, start, end);
        return GetIntervalsOfTotalDuration(available, duration);

    }

    private static Vector<Interval> GetAllAvailable(Vector<Interval> busyTime, DateTime start, DateTime end) {
        var filtered = FilterIntervals(busyTime, start, end);
        filtered.sort(new IntervalStartComparator());
        var merged = MergeSortedIntervals(filtered);
        return GetFreeIntervalsFromMergedIntervals(merged, start, end);
    }

    private static Vector<Interval> GetIntervalsOfTotalDuration(Vector<Interval> available, Duration duration) {
        var totalDuration = Duration.ZERO;
        var fittingDuration = new Vector<Interval>();

        for (Interval interval : available) {
            var currentDuration = totalDuration.plus(interval.toDuration());
            int compareTo = currentDuration.compareTo(duration);
            if (compareTo < 0) {
                fittingDuration.add(interval);
                totalDuration = currentDuration;
            } else if (compareTo == 0) {
                fittingDuration.add(interval);
                return fittingDuration;
            } else {
                var shortenedInterval = new Interval(interval.getStart(), duration.minus(totalDuration));
                fittingDuration.add(shortenedInterval);
                return fittingDuration;
            }
        }
        return new Vector<>();
    }

    private static Vector<Interval> GetFreeIntervalsFromMergedIntervals(Vector<Interval> mergedIntervals, DateTime start, DateTime end) {
        var freeIntervals = new Vector<Interval>();

        Interval lastFree = new Interval(start, end);
        if (mergedIntervals.size() > 0) {
            var firstBusy = mergedIntervals.get(0);
            if (firstBusy.isAfter(start)) {
                var firstFree = new Interval(start, firstBusy.getStart());
                freeIntervals.add(0, firstFree);
            }
            var lastBusy = mergedIntervals.get(mergedIntervals.size() - 1);
            if (lastBusy.isBefore(end)) {
                lastFree = new Interval(lastBusy.getEnd(), end);
            }
        }

        for (int i = 0; i < mergedIntervals.size() - 1; i++) {
            var freeStart = mergedIntervals.get(i).getEnd();
            var freeEnd = mergedIntervals.get(i + 1).getStart();
            freeIntervals.add(new Interval(freeStart, freeEnd));
        }

        freeIntervals.add(lastFree);

        return freeIntervals;
    }

    private static Vector<Interval> MergeSortedIntervals(Vector<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            return intervals;
        }

        var mergedIntervals = new Vector<Interval>();
        mergedIntervals.add(intervals.get(0));

        for (int i = 1; i < intervals.size(); i++) {
            var first = mergedIntervals.remove(mergedIntervals.size() - 1);
            var second = intervals.get(i);
            if (first.overlaps(second)) {
                var merged = merge(first, second);
                mergedIntervals.add(merged);
            } else {
                mergedIntervals.add(first);
                mergedIntervals.add(second);
            }
        }
        return mergedIntervals;
    }

    private static Vector<Interval> FilterIntervals(Vector<Interval> intervals, DateTime start, DateTime end) {
        var clampedIntervals = new Vector<Interval>();
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

    private static Interval GetFirstIntervalMatchingDuration(Vector<Interval> available, Duration duration) {
        for (Interval interval : available) {
            var intervalDuration = interval.toDuration();
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
