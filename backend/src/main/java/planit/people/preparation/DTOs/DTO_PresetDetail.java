package planit.people.preparation.DTOs;

import planit.people.preparation.Entities.Entity_EventPreset;
import planit.people.preparation.Entities.Entity_Guest;
import planit.people.preparation.Entities.Entity_PresetAvailability;

import java.util.List;

public record DTO_PresetDetail(
        Entity_EventPreset event_preset,
        List<Entity_Guest> guests,
        List<Entity_PresetAvailability> preset_availability) {
}