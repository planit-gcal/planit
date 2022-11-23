package planit.people.preparation.Scheduling;

import org.joda.time.Interval;

import java.util.ArrayList;

public class SchedulingInfo {
    public String planitUserId;
    public boolean isRequired;
    public ArrayList<Interval> availableIntervals;
}
