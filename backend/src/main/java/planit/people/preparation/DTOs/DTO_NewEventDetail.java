package planit.people.preparation.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public record DTO_NewEventDetail(
        String summary,
        String location,
        String description,
        String calendar_name,
        List<String> attendees,
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss z") Date start_date,
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss z")Date end_date,
        Long duration) {
}
