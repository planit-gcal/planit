package planit.people.preparation.DAOs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import planit.people.preparation.Entities.Entity_Google_account;

import java.util.List;

public interface IDAO_Google_account extends CrudRepository<Entity_Google_account, Long> {
    @Query("SELECT email FROM Entity_Google_account where the_user.user_id = :userId")
    List<String> getEmailsFromUserId(@Param("userId") Long userId);
}
