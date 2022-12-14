package planit.people.preparation.DAOs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import planit.people.preparation.Entities.Entity_GoogleAccount;
import planit.people.preparation.Entities.Entity_User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IDAO_GoogleAccount extends CrudRepository<Entity_GoogleAccount, Long> {
    @Query("SELECT new Entity_GoogleAccount(google.id, google.email, google.the_user, google.refresh_token) FROM Entity_GoogleAccount google  WHERE google.email IN  (:emails)")
    List<Entity_GoogleAccount> getEntityGoogleAccountByEmail(@Param("emails") List<String> emails);

    @Query("SELECT new Entity_GoogleAccount (google.id, google.email, google.the_user, google.refresh_token) FROM Entity_GoogleAccount google  WHERE google.the_user IN (:planItUsers)")
    List<Entity_GoogleAccount> getEntityGoogleAccountsByPlanItUsers(@Param("planItUsers") Set<Entity_User> planItUsers);

    @Query("SELECT email FROM Entity_GoogleAccount WHERE the_user.user_id = :userId")
    List<String> getEmailsFromUserId(@Param("userId") Long userId);

    @Query("SELECT google.the_user.user_id FROM Entity_GoogleAccount google WHERE google.email = (:email)")
    Optional<Long> getPlanitUserIdFromEmail(@Param("email") String email);

    @Query("SELECT new Entity_GoogleAccount(ac.id, ac.email, ac.the_user, ac.refresh_token) FROM Entity_GoogleAccount ac WHERE ac.email = :email")
    Entity_GoogleAccount getGoogleAccountFromEmail(@Param("email") String email);
}
