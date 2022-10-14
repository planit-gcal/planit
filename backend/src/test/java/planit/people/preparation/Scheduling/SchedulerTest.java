package planit.people.preparation.Scheduling;

import org.joda.time.Interval;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

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

        var actual = Scheduler.getAvailableTimeSlotsBetweenDatesOfTotalLength(intervals, Duration.standardDays(7), formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("05/01/2000"), formatter.parseDateTime("07/01/2000")));
        expected.add(new Interval(formatter.parseDateTime("10/01/2000"), formatter.parseDateTime("13/01/2000")));
        expected.add(new Interval(formatter.parseDateTime("15/01/2000"), formatter.parseDateTime("17/01/2000")));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesWithShortDuration() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("05/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.getAvailableTimeSlotsBetweenDatesOfTotalLength(intervals, Duration.standardDays(4), formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("05/01/2000"), formatter.parseDateTime("07/01/2000")));
        expected.add(new Interval(formatter.parseDateTime("10/01/2000"), formatter.parseDateTime("12/01/2000")));

        assert actual != null;
        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesWithTooLongDuration() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("05/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.getAvailableTimeSlotsBetweenDatesOfTotalLength(intervals, Duration.standardDays(10), formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        assertEquals( 0, actual.size());
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesFewMerges() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.getAvailableTimeSlotsBetweenDatesOfTotalLength(intervals, Duration.standardDays(2) ,formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("15/01/2000"), formatter.parseDateTime("17/01/2000")));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesOutOfBounds() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.getAvailableTimeSlotsBetweenDatesOfTotalLength(intervals, Duration.standardDays(5) ,formatter.parseDateTime("20/01/2000"), formatter.parseDateTime("25/01/2000"));

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("20/01/2000"), formatter.parseDateTime("25/01/2000")));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesEmpty() {
        var intervals = new Vector<Interval>();
        var actual = Scheduler.getAvailableTimeSlotsBetweenDatesOfTotalLength(intervals, Duration.standardDays(16) ,formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new Vector<Interval>();
        expected.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000")));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAvailableTimeSlotsBetweenDatesIncorrect() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("15/01/2000")));
        assertThrows(java.lang.IllegalArgumentException.class, () -> {Scheduler.getAvailableTimeSlotsBetweenDatesOfTotalLength(intervals, Duration.standardDays(0) ,formatter.parseDateTime("20/01/2000"), formatter.parseDateTime("1/01/2000"));});
    }

    @Test
    void getOneTimeSlotBetweenDatesOfLengthUsual() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("05/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.getOneTimeSlotBetweenDatesOfLength(intervals, Duration.standardDays(2), formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new Interval(formatter.parseDateTime("05/01/2000"), formatter.parseDateTime("07/01/2000"));

        assertEquals(actual, expected);
    }

    @Test
    void getOneTimeSlotBetweenDatesOfLengthShort() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("05/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.getOneTimeSlotBetweenDatesOfLength(intervals, Duration.standardDays(1), formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new Interval(formatter.parseDateTime("05/01/2000"), formatter.parseDateTime("06/01/2000"));

        assertEquals(actual, expected);
    }

    @Test
    void getOneTimeSlotBetweenDatesOfLengthTooLong() {
        var intervals = new Vector<Interval>();
        intervals.add(new Interval(formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("05/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("07/01/2000"), formatter.parseDateTime("10/01/2000")));
        intervals.add(new Interval(formatter.parseDateTime("13/01/2000"), formatter.parseDateTime("15/01/2000")));

        var actual = Scheduler.getOneTimeSlotBetweenDatesOfLength(intervals, Duration.standardDays(10), formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        assertNull(actual);
    }
}