package planit.people.preparation.Scheduling;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for finding the best starting date for the event.
 */
public class BestDateFinder {

    /**
     * Finds the best possible date to schedule the event.
     * The date must match free intervals of all required users and the duration of the event.
     * Additionally, provided date will fit as many non required users as possible.
     * If the date matching all required users or duration could not be found, returns null.
     *
     * @param schedulingInfoList List containing information about users available intervals and whether they're required or not
     * @param duration           The duration of the event
     * @return A starting date of the event to be scheduled or null.
     */
    public static @Nullable Date getBestStartDate(List<SchedulingInfo> schedulingInfoList, Duration duration) {
        if (schedulingInfoList == null || schedulingInfoList.isEmpty() || duration == null) {
            return null;
        }
        List<List<Interval>> requiredUsers = new ArrayList<>();
        List<Interval> notRequiredUsers = new ArrayList<>();
        for (SchedulingInfo schedulingInfo : schedulingInfoList) {
            if (schedulingInfo.isRequired) {
                requiredUsers.add(schedulingInfo.availableIntervals);
            } else {
                notRequiredUsers.addAll(schedulingInfo.availableIntervals);
            }
        }
        List<Interval> mergedRequired = startMergeRequiredUsers(duration, requiredUsers);
        DateTime finalDate = FindBestMatch(mergedRequired, notRequiredUsers, duration);
        if (finalDate == null) {
            return null;
        }
        return new Date(finalDate.getMillis());
    }


    /**
     * Starts recursive method {@link #mergeRequiredUsers(List, Duration, List)}.
     *
     * @param duration      The duration of the event
     * @param requiredUsers List of required users available intervals
     * @return List of intervals available for all required users. Might be empty.
     */
    private static List<Interval> startMergeRequiredUsers(Duration duration, List<List<Interval>> requiredUsers) {
        List<Interval> mergedRequired = new ArrayList<>();
        if (!requiredUsers.isEmpty()) {
            List<Interval> firstMerged = requiredUsers.remove(0);
            mergedRequired = mergeRequiredUsers(requiredUsers, duration, firstMerged);
        }
        return mergedRequired;
    }

    /**
     * Recursively merges the intervals of the required users into a single list of intervals.
     * `mergedIntervals` list stays unchanged or an interval gets removed from it in case that ANY of the users does not match it at all.
     *
     * @param ListOfUserIntervals Information about all required users intervals. This list contains a list of intervals. The nested list represents available calendar of one user, so together it represents list of calendars.
     * @param duration            The duration of the event.
     * @param mergedIntervals     List of already merged intervals to compare to.
     * @return List of intervals available for all required users. Might be empty.
     */
    private static List<Interval> mergeRequiredUsers(List<List<Interval>> ListOfUserIntervals, Duration duration, List<Interval> mergedIntervals) {
        if (ListOfUserIntervals.isEmpty() || mergedIntervals.isEmpty()) {
            return mergedIntervals;
        }
        List<Interval> userIntervals = ListOfUserIntervals.remove(0);
        return mergeRequiredUsers(ListOfUserIntervals, duration, matchMergedAndUserIntervals(mergedIntervals, userIntervals, duration));
    }

    /**
     * Finds the date from required intervals that suits the most non required users.
     *
     * @param requiredIntervals    The list of the available intervals that suit every single required user.
     * @param nonRequiredIntervals Intervals of non required users.
     * @param duration             The duration of the event
     * @return The date from requiredIntervals that matches the most non required users
     */
    private static @Nullable DateTime FindBestMatch(List<Interval> requiredIntervals, List<Interval> nonRequiredIntervals, Duration duration) {
        if (requiredIntervals.isEmpty()) {
            return null;
        }
        if (nonRequiredIntervals.isEmpty()) {
            return requiredIntervals.get(0).getStart();
        }
        DateTime maxDate = nonRequiredIntervals.get(0).getStart();
        int maxCount = 0;
        for (Interval interval : nonRequiredIntervals) {
            if (interval.toDuration().isShorterThan(duration)) {
                continue;
            }
            if (!DoesIntervalAlignWithRequiredIntervals(requiredIntervals, interval)) {
                continue;
            }
            int currentCount = countMatches(interval, nonRequiredIntervals, duration);
            if (currentCount <= maxCount) {
                continue;
            }
            maxCount = currentCount;
            maxDate = interval.getStart();
        }
        return maxDate;
    }

    /**
     * Counts how many intervals match with current interval.
     * Intervals match if:
     * 1. Their duration is not shorter than duration;
     * 2. Second interval starts not after the start of the first one.
     *
     * @param intervalToCheckAgainst Interval that we are counting the matches for.
     * @param nonRequiredIntervals   List of intervals that will be matched and counted.
     * @param duration               The duration of the event.
     * @return Integer representing the number of intervals matching.
     */
    private static int countMatches(Interval intervalToCheckAgainst, List<Interval> nonRequiredIntervals, Duration duration) {
        DateTime currentDate = intervalToCheckAgainst.getStart();
        int currentCount = 0;
        for (Interval interval2 : nonRequiredIntervals) {
            if (!currentDate.plus(duration.getMillis()).isBefore(interval2.getEnd())) {
                continue;
            }
            if (!intervalToCheckAgainst.getStart().isAfter(interval2.getStart())) {
                continue;
            }
            currentCount++;
        }
        return currentCount;
    }

    /**
     * This method attempts to merge each interval in the intervals list with each interval in the userIntervals list.
     * The resulting intervals must have a duration greater than or equal to the specified duration.
     * The method returns a list of the successfully merged intervals.
     *
     * @param intervals     The list of intervals to merge.
     * @param userIntervals The list of intervals to merge with.
     * @param duration      The minimum duration that the resulting intervals must have.
     * @return A list of the successfully merged intervals.
     */
    private static List<Interval> matchMergedAndUserIntervals(List<Interval> intervals, List<Interval> userIntervals, Duration duration) {
        ArrayList<Interval> merged = new ArrayList<>();
        for (Interval interval : intervals) {
            for (Interval userInterval : userIntervals) {
                Interval newInterval = tryClampInterval(interval, userInterval, duration);
                if (newInterval != null) {
                    merged.add(newInterval);
                }
            }
        }
        return merged;
    }

    /**
     * Determines whether the given interval is contained within any of the intervals in the requiredIntervals list.
     *
     * @param requiredIntervals The list of intervals to check against.
     * @param interval          The interval to test.
     * @return true if the given interval is contained within any of the intervals in the requiredIntervals list, false otherwise.
     */
    private static Boolean DoesIntervalAlignWithRequiredIntervals(List<Interval> requiredIntervals, Interval interval) {
        for (Interval required : requiredIntervals) {
            if (required.contains(interval)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to clamp the given intervalToClamp to the bounds of the given intervalToClampAgainst such that the resulting interval has a duration greater than or equal to the specified duration. If the intervalToClamp cannot be clamped within the bounds of the intervalToClampAgainst or the resulting interval is shorter than the specified duration, then this method returns null.
     * @param intervalToClampAgainst The interval to clamp against.
     * @param intervalToClamp The interval to clamp.
     * @param duration The minimum duration that the resulting interval must have.
     * @return The clamped interval, or null if the intervalToClamp cannot be clamped within the bounds of the `intervalToClampAgainst` or the resulting interval is shorter than the specified duration.
     */
    private static Interval tryClampInterval(Interval intervalToClampAgainst, Interval intervalToClamp, Duration duration) {
        DateTime startTime = intervalToClampAgainst.getStart();
        if (startTime.isBefore(intervalToClamp.getStart())) {
            startTime = intervalToClamp.getStart();
        }
        DateTime endTime = intervalToClampAgainst.getEnd();
        if (endTime.isAfter(intervalToClamp.getEnd())) {
            endTime = intervalToClamp.getEnd();
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
