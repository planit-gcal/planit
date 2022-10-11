package planit.people.preparation.Scheduling;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.*;

public class Scheduler {

    public Vector<Interval> GetAvailableTimeSlotsBetweenDates(Vector<Interval> busyTime, DateTime start, DateTime end) {
        var filtered = FilterIntervals(busyTime, start, end);
        filtered.sort(new IntervalStartComparator());
        var merged = MergeSortedIntervals(filtered);
        return GetFreeIntervalsFromMergedIntervals(merged, start, end);
    }

    private Vector<Interval> GetFreeIntervalsFromMergedIntervals(Vector<Interval> mergedIntervals, DateTime start, DateTime end) {
        var freeIntervals = new Vector<Interval>();

        Interval lastFree = null;
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

        if (lastFree != null) {
            freeIntervals.add(lastFree);
        }

        return freeIntervals;
    }

    private Vector<Interval> MergeSortedIntervals(Vector<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            throw new NullPointerException("sortedIntervals cannot be empty");
        }

        var mergedIntervals = new Vector<Interval>();
        mergedIntervals.add(intervals.get(0));

        for (int i = 1; i < intervals.size() - 1; i++) {
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

    private Vector<Interval> FilterIntervals(Vector<Interval> intervals, DateTime start, DateTime end) {
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

    public static class IntervalStartComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval x, Interval y) {
            return x.getStart().compareTo(y.getStart());
        }
    }
}
