package planit.people.preparation.DAOs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import planit.people.preparation.Entities.Entity_EventPreset;
import planit.people.preparation.Entities.Entity_User;

import java.util.List;

public interface IDAO_User extends CrudRepository<Entity_User, Long> {
    @Query("SELECT u.entity_EventPresets FROM Entity_User u WHERE u.user_id = :planItUserId")
    List<Entity_EventPreset> getAllEventPresetByPlanItId(@Param("planItUserId") Long planItUserId);

}
