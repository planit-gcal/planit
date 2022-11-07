package planit.people.preparation.DAOs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import planit.people.preparation.Entities.Entity_EventPreset;
import planit.people.preparation.Entities.Entity_User;

import java.util.List;
import java.util.Set;

public interface IDAO_EventPreset extends CrudRepository<Entity_EventPreset, Long> {
    @Query("SELECT e.entityUsers FROM Entity_EventPreset e WHERE e.id_event_preset = :EventPresetId")
    List<Entity_User> getEntityUsersByEventPresetId(@Param("EventPresetId") Long eventPresetId);

}
