package planit.people.preparation.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import planit.people.preparation.Entities.Entity_Guest;

import java.util.Date;
import java.util.List;

public record DTO_NewEventDetail(
        String name,
        String summary,
        String location,
        String description,
        DTO_PresetDetail event_preset_detail,
        String owner_email,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start_date,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end_date,
        Long duration) {
}
