package planit.people.preparation.Scheduling;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

public class SchedulingInfo {

    public boolean isRequired;
    public List<Interval> availableIntervals;

    public SchedulingInfo(boolean isRequired, List<Interval> availableIntervals) {
        this.isRequired = isRequired;
        this.availableIntervals = availableIntervals;
    }

    public SchedulingInfo() {
    }

    @Override
    public String toString() {
        return "SchedulingInfo{" +
                "isRequired=" + isRequired +
                ", availableIntervals=" + availableIntervals +
                '}';
    }
}
