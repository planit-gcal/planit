package planit.people.preparation.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public record DTO_NewEventDetail(
        String summary,
        String location,
        String description,
        String calendar_name,
        List<String> attendee_emails,
        String owner_email,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start_date,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end_date,
        Long duration) {
}
