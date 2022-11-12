package planit.people.preparation.DAOs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import planit.people.preparation.Entities.Entity_EventPreset;
import planit.people.preparation.Entities.Entity_PresetAvailability;

import java.util.List;

public interface IDAO_PresetAvailability extends CrudRepository<Entity_PresetAvailability, Long> {
    @Query("SELECT new Entity_PresetAvailability(av.id_preset_availability, av.entity_EventPreset, av.day, av.day_off, av.start_available_time, av.end_available_time) FROM Entity_PresetAvailability av WHERE av.entity_EventPreset IN (:EventPresets)")
    List<Entity_PresetAvailability> getAllByIdEventPreset(@Param("EventPresets") List<Entity_EventPreset> eventPresets);
}
