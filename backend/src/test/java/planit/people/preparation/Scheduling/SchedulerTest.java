package planit.people.preparation.Scheduling;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import planit.people.preparation.Entities.Entity_PresetAvailability;

import java.io.Console;
import java.sql.Time;
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
    void regularUseCase() {
        var duration = Duration.standardHours(5);
        var busyTime = new ArrayList<Interval>() {
            {
                // Saturday
                add(new Interval(
                        new DateTime(2000, 1, 1, 10, 30),
                        new DateTime(2000, 1, 1, 13, 0)
                ));
                add(new Interval(
                        new DateTime(2000, 1, 1, 17, 5),
                        new DateTime(2000, 1, 1, 20, 30)
                ));
                // Sunday

                // Monday
                // Getting back home
                add(new Interval(
                        new DateTime(2000, 1, 3, 16, 0),
                        new DateTime(2000, 1, 3, 16, 40)
                ));
                // Gym
                add(new Interval(
                        new DateTime(2000, 1, 3, 18, 5),
                        new DateTime(2000, 1, 3, 20, 12)
                ));
                // Dinner with wife
                add(new Interval(
                        new DateTime(2000, 1, 3, 22, 0),
                        new DateTime(2000, 1, 3, 23, 40)
                ));


                //Tuesday
                // Getting back home
                add(new Interval(
                        new DateTime(2000, 1, 4, 16, 0),
                        new DateTime(2000, 1, 4, 16, 40)
                ));
                //HERE AVAILABLE 16:40 - 22:00

                // Wednesday
                // Getting back home
                add(new Interval(
                        new DateTime(2000, 1, 5, 16, 0),
                        new DateTime(2000, 1, 5, 16, 40)
                ));
                //HERE AVAILABLE 16:40 - 22:00

                // Thursday
                // Getting back home
                add(new Interval(
                        new DateTime(2000, 1, 6, 16, 0),
                        new DateTime(2000, 1, 6, 16, 40)
                ));
                // COLLIDING EVENTS
                // Gym
                add(new Interval(
                        new DateTime(2000, 1, 6, 18, 5),
                        new DateTime(2000, 1, 6, 20, 12)
                ));
                // Gym with bro
                add(new Interval(
                        new DateTime(2000, 1, 6, 18, 0),
                        new DateTime(2000, 1, 6, 20, 30)
                ));
                // Dinner with wife
                add(new Interval(
                        new DateTime(2000, 1, 6, 22, 0),
                        new DateTime(2000, 1, 6, 23, 40)
                ));
                // Dinner with girlfriend
                add(new Interval(
                        new DateTime(2000, 1, 6, 21, 0),
                        new DateTime(2000, 1, 6, 22, 17)
                ));


                // Friday
                // Work time
                // Getting back home
                add(new Interval(
                        new DateTime(2000, 1, 7, 16, 0),
                        new DateTime(2000, 1, 7, 16, 57)
                ));
                // HERE AVAILABLE 16:57 - 21:57

                // Movie
                add(new Interval(
                        new DateTime(2000, 1, 7, 21, 57),
                        new DateTime(2000, 1, 7, 23, 59)
                ));

            }
        };

        var presetAvailabilities = new ArrayList<Entity_PresetAvailability>()
        {
            {
                add(new Entity_PresetAvailability(Entity_PresetAvailability.WeekDays.MONDAY, Time.valueOf("16:00:00"), Time.valueOf("22:00:00"), false));
                add(new Entity_PresetAvailability(Entity_PresetAvailability.WeekDays.TUESDAY, Time.valueOf("16:00:00"), Time.valueOf("22:00:00"), false));
                add(new Entity_PresetAvailability(Entity_PresetAvailability.WeekDays.WEDNESDAY, Time.valueOf("16:00:00"), Time.valueOf("22:00:00"), false));
                add(new Entity_PresetAvailability(Entity_PresetAvailability.WeekDays.THURSDAY, Time.valueOf("16:00:00"), Time.valueOf("22:00:00"), false));
                add(new Entity_PresetAvailability(Entity_PresetAvailability.WeekDays.FRIDAY, Time.valueOf("16:00:00"), Time.valueOf("22:00:00"), false));
                add(new Entity_PresetAvailability(Entity_PresetAvailability.WeekDays.SATURDAY, null, null, true));
                add(new Entity_PresetAvailability(Entity_PresetAvailability.WeekDays.SUNDAY, null, null, true));
            }
        };

        var actual = Scheduler.getAvailableTimeSlots(busyTime, duration, new DateTime(2000, 1, 1, 0, 0), new DateTime(2000, 1, 7, 23, 59), presetAvailabilities);

        var expected = new ArrayList<Interval>()
        {
            {
                add(new Interval(
                        new DateTime(2000, 1, 4, 16, 40),
                        new DateTime(2000, 1, 4, 22, 0)
                ));

                add(new Interval(
                        new DateTime(2000, 1, 5, 16, 40),
                        new DateTime(2000, 1, 5, 22, 0)
                ));

                add(new Interval(
                        new DateTime(2000, 1, 7, 16, 57),
                        new DateTime(2000, 1, 7, 21, 57)
                ));
            }
        };

        actual.forEach(System.out::println);

        assertEquals(actual.size(), expected.size());
        assertEquals(actual.get(0), expected.get(0));
        assertEquals(actual.get(1), expected.get(1));
        assertEquals(actual.get(2), expected.get(2));
    }

}