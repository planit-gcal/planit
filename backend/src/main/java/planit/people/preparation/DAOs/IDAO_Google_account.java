package planit.people.preparation.DAOs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import planit.people.preparation.Entities.Entity_Google_account;
import planit.people.preparation.Entities.Entity_User;

import java.util.List;
import java.util.Set;

public interface IDAO_Google_account extends CrudRepository<Entity_Google_account, Long> {
    @Query("SELECT new Entity_Google_account(google.id, google.email, google.the_user, google.refresh_token) FROM Entity_Google_account google  WHERE google.email IN  (:emails)")
    List<Entity_Google_account> getEntityGoogleAccountByEmail(@Param("emails") List<String> emails);

    @Query("SELECT new Entity_Google_account (google.id, google.email, google.the_user, google.refresh_token) FROM Entity_Google_account google  WHERE google.the_user IN (:planItUsers)")
    List<Entity_Google_account> getEntityGoogleAccountsByPlanItUsers(@Param("planItUsers") Set<Entity_User> planItUsers);
}
