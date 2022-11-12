package planit.people.preparation.DAOs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import planit.people.preparation.Entities.Entity_EventPreset;
import planit.people.preparation.Entities.Entity_User;

import java.util.List;
import java.util.Set;

public interface IDAO_User extends CrudRepository<Entity_User, Long> {
    @Query("SELECT u.entity_EventPresets FROM Entity_User u WHERE u.user_id = :planItUserId")
    List<Entity_EventPreset> getAllEventPresetByPlanItId(@Param("planItUserId") Long planItUserId);
    @Query("SELECT new Entity_User(u.user_id, u.name, u.surname) FROM Entity_User u WHERE u.entity_EventPresets IN (:eventPresets)")
    List<Entity_User> getAllByEventPresetInstance(@Param("eventPresets") Set<Entity_EventPreset> eventPresets);

}
