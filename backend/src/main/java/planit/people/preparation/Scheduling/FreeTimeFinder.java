package planit.people.preparation.Scheduling;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.ReadableDateTime;

import java.util.ArrayList;
import java.util.List;

public class FreeTimeFinder {
    private static ReadableDateTime getRequiredAvailableIntervals(List<SchedulingInfo> schedulingInfoList, Duration duration) {
        var requiredUsers = new ArrayList<ArrayList<Interval>>();
        var notRequiredUsers = new ArrayList<Interval>();
        for (SchedulingInfo schedulingInfo : schedulingInfoList) {
            if (schedulingInfo.isRequired) {
                requiredUsers.add(schedulingInfo.availableIntervals);
            } else {
                notRequiredUsers.addAll(schedulingInfo.availableIntervals);
            }
        }
        var firstMerged = requiredUsers.remove(0);
        var mergedRequired = mergeRequiredUsers(requiredUsers, duration, firstMerged);
        var finalDate = FindBestMatch(mergedRequired, notRequiredUsers, duration);
        return finalDate;
    }

    private static DateTime FindBestMatch(List<Interval> requiredIntervals, List<Interval> nonRequiredIntervals, Duration duration) {
        var maxDate = nonRequiredIntervals.get(0).getStart();
        var maxCount = 0;
        for (Interval interval1 : nonRequiredIntervals) {
            if(interval1.toDuration().isShorterThan(duration)) {
                continue;
            }
            if (!DoesIntervalAlignWithRequiredIntervals(requiredIntervals, interval1)) {
                continue;
            }
            var currentDate = interval1.getStart();
            var currentCount = 0;
            for (Interval interval2 : nonRequiredIntervals) {
                if (!currentDate.plus(duration.getMillis()).isBefore(interval2.getEnd())) {
                    continue;
                }
                if (!interval1.getStart().isAfter(interval2.getStart())) {
                    continue;
                }
                currentCount++;
                if (currentCount <= maxCount) {
                    continue;
                }
                maxCount = currentCount;
                maxDate = currentDate;
            }
        }
        return maxDate;
    }

    private static Boolean DoesIntervalAlignWithRequiredIntervals(List<Interval> requiredIntervals, Interval interval) {
        for (Interval required : requiredIntervals)
        {
            if(required.contains(interval))
            {
                return true;
            }
        }
        return false;
    }


    private static List<Interval> mergeRequiredUsers(ArrayList<ArrayList<Interval>> ListOfUserIntervals, Duration duration, List<Interval> mergedIntervals) {
        if (ListOfUserIntervals.isEmpty() || mergedIntervals.isEmpty()) {
            return mergedIntervals;
        }
        var userIntervals = ListOfUserIntervals.remove(0);
        return mergeRequiredUsers(ListOfUserIntervals, duration, matchMergedAndUserIntervals(mergedIntervals, userIntervals, duration));
    }

    private static List<Interval> matchMergedAndUserIntervals(List<Interval> intervals, List<Interval> userIntervals, Duration duration) {
        var merged = new ArrayList<Interval>();
        for (Interval interval : intervals) {
            for (Interval userInterval : userIntervals) {
                var newInterval = tryMergeIntervals(interval, userInterval, duration);
                if (newInterval != null) {
                    merged.add(newInterval);
                }
            }
        }
        return merged;
    }

    private static Interval tryMergeIntervals(Interval interval1, Interval interval2, Duration duration) {
        var startTime = interval1.getStart();
        if (startTime.isBefore(interval2.getStart())) {
            startTime = interval2.getStart();
        }
        var endTime = interval1.getEnd();
        if (endTime.isAfter(interval2.getEnd())) {
            endTime = interval2.getEnd();
        }
        if (startTime.isAfter(endTime)) {
            return null;
        }
        var newInterval = new Interval(startTime, endTime);
        if (newInterval.toDuration().isShorterThan(duration)) {
            return null;
        }
        return newInterval;
    }
}
