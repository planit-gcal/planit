package planit.people.preparation.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public record DTO_NewEventDetail(
        String summary,
        String location,
        String description,
        List<String> attendee_emails,
        String owner_email,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start_date,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end_date,
        Boolean break_into_smaller_events,
        Integer min_length_of_single_event,
        Integer max_length_of_single_event,
        String preset,
        Long duration) {
}
