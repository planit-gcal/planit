package planit.people.preparation;

import planit.people.preparation.Entities.*;

import java.sql.Time;

public class TestUtils {

    public static Entity_Guest createGuest(String email, Boolean obligatory) {
        return createGuest(email, obligatory, null);
    }

    public static Entity_Guest createGuest(String email, Boolean obligatory, Long id) {
        Entity_Guest guest = new Entity_Guest(email, obligatory);
        if (id != null) {
            guest.setId_event_guest(id);
        } else {
            guest.setEntity_EventPreset(null);
            guest.setId_event_guest(null);
        }
        return guest;
    }

    public static Entity_PresetAvailability createPresetAvailability(Entity_PresetAvailability.WeekDays day, Time start_available_time, Time end_available_time, Boolean day_off, Long id) {
        Entity_PresetAvailability availability = new Entity_PresetAvailability(day, start_available_time, end_available_time, day_off);
        if (id != null) {
            availability.setId_preset_availability(id);
        }
        return availability;
    }

    public static Entity_PresetAvailability createPresetAvailability(Entity_PresetAvailability.WeekDays day, Time start_available_time, Time end_available_time, Boolean day_off) {
        return createPresetAvailability(day, start_available_time, end_available_time, day_off, null);
    }

    public static Entity_EventPreset createPreset(String name, Boolean break_into_smaller_events, Integer min_length_of_single_event, Integer max_length_of_single_event, Long id) {
        Entity_EventPreset preset = new Entity_EventPreset(
                name, break_into_smaller_events, min_length_of_single_event, max_length_of_single_event
        );
        if (id != null) {
            preset.setId_event_preset(id);
        }
        return preset;
    }

    public static Entity_EventPreset createPreset(String name, Boolean break_into_smaller_events, Integer min_length_of_single_event, Integer max_length_of_single_event) {
        return createPreset(name, break_into_smaller_events, min_length_of_single_event, max_length_of_single_event, null);
    }
}
