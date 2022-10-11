package planit.people.preparation.Scheduling;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Vector;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SchedulerTest {

    DateTimeFormatter formatter;
    @BeforeEach
    void setUp() {
        formatter = DateTimeFormat.forPattern("dd/MM/YYYY");
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesUsual() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("05/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.GetAvailableTimeSlotsBetweenDates(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));
        var actualArray = actual.toArray();

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("05/01/2000"), formatter.parseDateTime("07/01/2000")));
        expected.add(new Interval(formatter.parseDateTime("10/01/2000"), formatter.parseDateTime("13/01/2000")));
        expected.add(new Interval(formatter.parseDateTime("15/01/2000"), formatter.parseDateTime("17/01/2000")));

        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesFewMerges() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.GetAvailableTimeSlotsBetweenDates(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));
        var actualArray = actual.toArray();

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("15/01/2000"), formatter.parseDateTime("17/01/2000")));

        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesOutOfBounds() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.GetAvailableTimeSlotsBetweenDates(intervals, formatter.parseDateTime("20/01/2000"), formatter.parseDateTime("25/01/2000"));
        var actualArray = actual.toArray();

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("20/01/2000"), formatter.parseDateTime("25/01/2000")));

        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesEmpty() {
        var intervals = new Vector<Interval>();
        var actual = Scheduler.GetAvailableTimeSlotsBetweenDates(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));
        var actualArray = actual.toArray();

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000")));
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }
}