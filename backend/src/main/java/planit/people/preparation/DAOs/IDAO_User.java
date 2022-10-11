package planit.people.preparation.DAOs;

import org.springframework.data.repository.CrudRepository;
import planit.people.preparation.Entities.Entity_User;

public interface IDAO_User extends CrudRepository<Entity_User, Long> {

}
