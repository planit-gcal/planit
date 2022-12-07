package planit.people.preparation.Scheduling;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {

    DateTimeFormatter formatter;
    String yearMonth = "/01/2000";
    private Interval newInterval(int start, int end) {
        return new Interval(formatter.parseDateTime(start + yearMonth), formatter.parseDateTime(end + yearMonth));
    }
    @BeforeEach
    void setUp() {
        formatter = DateTimeFormat.forPattern("dd/MM/YYYY");
    }

    @Test
    void getAllAvailableBetweenDatesUsual() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(1, 5));
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));

        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new ArrayList<>();
        expected.add(newInterval(5, 7));
        expected.add(newInterval(10, 13));
        expected.add(newInterval(15, 17));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAllAvailableBetweenDatesWithShortDuration() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(1, 5));
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));

        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new ArrayList<>();
        expected.add(newInterval(5, 7));
        expected.add(newInterval(10, 12));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAllAvailableBetweenDatesWithTooLongDuration() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(1, 5));
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));

        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        assertEquals(0, actual.size());
    }

    @Test
    void getAllAvailableBetweenDatesFewMerges() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));
        intervals.add(newInterval(1, 15));

        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new ArrayList<>();
        expected.add(newInterval(15, 17));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAllAvailableBetweenDatesOutOfBounds() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));
        intervals.add(newInterval(1, 15));

        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = new ArrayList<>();
        expected.add(newInterval(20, 25));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAllAvailableBetweenDatesEmpty() {
        List<Interval> intervals = new ArrayList<>();
        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));
        var expected = new ArrayList<>();
        expected.add(newInterval(1, 17));

        var actualArray = actual.toArray();
        var expectedArray = expected.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void getAllAvailableBetweenDatesIncorrect() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));
        intervals.add(newInterval(1, 15));
        assertThrows(java.lang.IllegalArgumentException.class, () -> Scheduler.getAllAvailable(intervals, formatter.parseDateTime("20, 1);
    }

    @Test
    void getOneTimeSlotBetweenDatesOfLengthUsual() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(1, 5));
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));

        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        var expected = newInterval(5, 7 / 1 /2000"));

        assertEquals(actual, expected);
    }

    @Test
    void getOneTimeSlotBetweenDatesOfLengthShort() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(1, 5));
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));

        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01, 17/01/2000"));

        var expected = newInterval(5, 6 / 1 /2000"));

        assertEquals(actual, expected);
    }

    @Test
    void getOneTimeSlotBetweenDatesOfLengthTooLong() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(newInterval(1, 5));
        intervals.add(newInterval(7, 10));
        intervals.add(newInterval(13, 15));

        var actual = Scheduler.getAllAvailable(intervals, formatter.parseDateTime("01/01/2000"), formatter.parseDateTime("17/01/2000"));

        assertNull(actual);
    }
}