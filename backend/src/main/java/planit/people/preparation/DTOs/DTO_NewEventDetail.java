package planit.people.preparation.DTOs;

import java.util.List;

public record DTO_NewEventDetail(
        String summary,
        String location,
        String description,
        String calendar_name,
        List<String> attendees,
        Integer duration) {
}
