package planit.people.preparation.Scheduling;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class BestDateFinderTest {

    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/YYYY");
    String yearMonth = "/01/2000";
    Duration duration = Duration.standardDays(4);
    SchedulingInfo requiredUser1 = new SchedulingInfo();
    SchedulingInfo requiredUser2 = new SchedulingInfo();
    SchedulingInfo requiredUser3 = new SchedulingInfo();

    SchedulingInfo nonRequiredUser1 = new SchedulingInfo();
    SchedulingInfo nonRequiredUser2 = new SchedulingInfo();
    SchedulingInfo nonRequiredUser3 = new SchedulingInfo();

    private Interval newInterval(int start, int end) {
        return new Interval(formatter.parseDateTime(start + yearMonth), formatter.parseDateTime(end + yearMonth));
    }

    @BeforeEach
    void setUp() {
        requiredUser1.isRequired = true;
        requiredUser1.availableIntervals = new ArrayList<>() {
            {
                add(newInterval(1, 9));
                add(newInterval(9, 10));
                add(newInterval(15, 20));
                add(newInterval(20, 25));
                add(newInterval(29, 30));
            }
        };

        requiredUser2.isRequired = true;
        requiredUser2.availableIntervals = new ArrayList<>() {
            {
                add(newInterval(1, 7));
                add(newInterval(9, 10));
                add(newInterval(13, 20));
                add(newInterval(21, 22));
            }
        };

        requiredUser3.isRequired = true;
        requiredUser3.availableIntervals = new ArrayList<>() {
            {
                add(newInterval(1, 12));
                add(newInterval(14, 29));
            }
        };

        nonRequiredUser1.isRequired = false;
        nonRequiredUser1.availableIntervals = new ArrayList<>() {
            {

                add(newInterval(1, 3));
                add(newInterval(5, 6));
                add(newInterval(9, 10));
                add(newInterval(16, 20));
                add(newInterval(20, 25));
                add(newInterval(29, 30));

            }
        };

        nonRequiredUser2.isRequired = false;
        nonRequiredUser2.availableIntervals = new ArrayList<>() {
            {

                add(newInterval(1, 3));
                add(newInterval(5, 6));
                add(newInterval(9, 10));
                add(newInterval(15, 20));
                add(newInterval(20, 25));
                add(newInterval(29, 30));

            }
        };

        nonRequiredUser3.isRequired = false;
        nonRequiredUser3.availableIntervals = new ArrayList<>() {
            {
                add(newInterval(14, 30));
            }
        };

    }

    @Test
    void getRequiredAvailableIntervals() {
        var actual = BestDateFinder.getBestStartDate(Arrays.asList(requiredUser1, requiredUser2, requiredUser3, nonRequiredUser1, nonRequiredUser2, nonRequiredUser3), duration);
        var expected = formatter.parseDateTime("16" + yearMonth);
        assert actual != null;
        Assertions.assertEquals(expected.toDate().toString(), actual.toString());
    }

    @Test
    void onlyOneRequiredTestWithNonMatchingNonRequired() {
        var actual = BestDateFinder.getBestStartDate(Arrays.asList(requiredUser3, nonRequiredUser1, nonRequiredUser2, nonRequiredUser3), requiredUser3.availableIntervals.get(0).toDuration());
        var expected = requiredUser3.availableIntervals.get(0).getStart().toDate();
        assert actual != null;
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void noMatchesTest()
    {
        var pickyUser = new SchedulingInfo();
        pickyUser.isRequired = true;
        pickyUser.availableIntervals = new ArrayList<>(){
            {
                add(newInterval(13, 14));
            }
        };
        var actual = BestDateFinder.getBestStartDate(Arrays.asList(requiredUser3, pickyUser, nonRequiredUser1, nonRequiredUser2, nonRequiredUser3), requiredUser3.availableIntervals.get(0).toDuration());
        Assertions.assertNull(actual);
    }

    @Test
    void emptyNonRequiredUsersTest() {
        var actual = BestDateFinder.getBestStartDate(Collections.singletonList(requiredUser3), requiredUser3.availableIntervals.get(0).toDuration());
        var expected = requiredUser3.availableIntervals.get(0).getStart().toDate();
        assert actual != null;
        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void emptyRequiredUsersTest() {
        var found = BestDateFinder.getBestStartDate(Arrays.asList(nonRequiredUser1, nonRequiredUser2, nonRequiredUser3), duration);
        Assertions.assertNull(found);
    }

    @Test
    void nullDurationTest() {
        var found = BestDateFinder.getBestStartDate(Arrays.asList(requiredUser1, nonRequiredUser1, nonRequiredUser2, nonRequiredUser3), null);
        Assertions.assertNull(found);
    }

}
