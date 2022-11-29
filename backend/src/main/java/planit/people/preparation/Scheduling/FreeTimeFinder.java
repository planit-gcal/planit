package planit.people.preparation.Scheduling;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FreeTimeFinder {
    public static @Nullable Date getBestStartDate(List<SchedulingInfo> schedulingInfoList, Duration duration) {
        if(schedulingInfoList == null || schedulingInfoList.isEmpty() || duration == null)
        {
            return null;
        }
        ArrayList<ArrayList<Interval>> requiredUsers = new ArrayList<>();
        ArrayList<Interval> notRequiredUsers = new ArrayList<>();
        for (SchedulingInfo schedulingInfo : schedulingInfoList) {
            if (schedulingInfo.isRequired) {
                requiredUsers.add(schedulingInfo.availableIntervals);
            } else {
                notRequiredUsers.addAll(schedulingInfo.availableIntervals);
            }
        }
        List<Interval> mergedRequired = startMergeRequiredUsers(duration, requiredUsers);
        DateTime finalDate = FindBestMatch(mergedRequired, notRequiredUsers, duration);
        if(finalDate == null)
        {
            return null;
        }
        return new Date(finalDate.getMillis());
    }

    private static List<Interval> startMergeRequiredUsers(Duration duration, ArrayList<ArrayList<Interval>> requiredUsers) {
        List<Interval> mergedRequired = new ArrayList<>();
        if(!requiredUsers.isEmpty())
        {
            ArrayList<Interval> firstMerged = requiredUsers.remove(0);
            mergedRequired = mergeRequiredUsers(requiredUsers, duration, firstMerged);
        }
        return mergedRequired;
    }

    private static List<Interval> mergeRequiredUsers(ArrayList<ArrayList<Interval>> ListOfUserIntervals, Duration duration, List<Interval> mergedIntervals) {
        if (ListOfUserIntervals.isEmpty() || mergedIntervals.isEmpty()) {
            return mergedIntervals;
        }
        ArrayList<Interval> userIntervals = ListOfUserIntervals.remove(0);
        return mergeRequiredUsers(ListOfUserIntervals, duration, matchMergedAndUserIntervals(mergedIntervals, userIntervals, duration));
    }

    private static @Nullable DateTime FindBestMatch(List<Interval> requiredIntervals, List<Interval> nonRequiredIntervals, Duration duration) {
        if(requiredIntervals.isEmpty())
        {
            return null;
        }
        if(nonRequiredIntervals.isEmpty())
        {
            return requiredIntervals.get(0).getStart();
        }
        DateTime maxDate = nonRequiredIntervals.get(0).getStart();
        int maxCount = 0;
        for (Interval interval1 : nonRequiredIntervals) {
            if(interval1.toDuration().isShorterThan(duration)) {
                continue;
            }
            if (!DoesIntervalAlignWithRequiredIntervals(requiredIntervals, interval1)) {
                continue;
            }
            DateTime currentDate = interval1.getStart();
            int currentCount = 0;
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

    private static List<Interval> matchMergedAndUserIntervals(List<Interval> intervals, List<Interval> userIntervals, Duration duration) {
        ArrayList<Interval> merged = new ArrayList<>();
        for (Interval interval : intervals) {
            for (Interval userInterval : userIntervals) {
                Interval newInterval = tryMergeIntervals(interval, userInterval, duration);
                if (newInterval != null) {
                    merged.add(newInterval);
                }
            }
        }
        return merged;
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

    private static Interval tryMergeIntervals(Interval interval1, Interval interval2, Duration duration) {
        DateTime startTime = interval1.getStart();
        if (startTime.isBefore(interval2.getStart())) {
            startTime = interval2.getStart();
        }
        DateTime endTime = interval1.getEnd();
        if (endTime.isAfter(interval2.getEnd())) {
            endTime = interval2.getEnd();
        }
        if (startTime.isAfter(endTime)) {
            return null;
        }
        Interval newInterval = new Interval(startTime, endTime);
        if (newInterval.toDuration().isShorterThan(duration)) {
            return null;
        }
        return newInterval;
    }
}
