package planit.people.preparation.DAOs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import planit.people.preparation.Entities.Entity_EventPreset;
import planit.people.preparation.Entities.Entity_Guest;

import java.util.List;

public interface IDAO_Guest extends CrudRepository<Entity_Guest, Long> {
    @Query("SELECT new Entity_Guest(g.id_event_guest, g.entity_EventPreset, g.email, g.obligatory) FROM Entity_Guest g WHERE g.entity_EventPreset IN (:EventPresets)")
    List<Entity_Guest> getAllByEntityEventPreset(@Param("EventPresets") List<Entity_EventPreset> eventPresets);
    @Query("SELECT g.id_event_guest FROM Entity_Guest g WHERE g.entity_EventPreset.id_event_preset = :EventPresetId")
    List<Long> getAllIdsForAnEventPreset(@Param("EventPresetId") Long eventPresetId);
}
