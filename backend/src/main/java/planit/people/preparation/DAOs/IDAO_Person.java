package planit.people.preparation.DAOs;

import org.springframework.data.repository.CrudRepository;
import planit.people.preparation.Entities.Entity_Person;

/**
 * define the functionalities of the Person Entity,
 * the interfaces is extended by the CrudRepository interface which supports all CRUD operations using JPA under the hood.
 *
 * @author Mustafa Alhamoud
 * @version 1.0
 * @see org.springframework.data.repository.CrudRepository in order to view some used methods for this project
 * @since v1.0
 */
public interface IDAO_Person extends CrudRepository<Entity_Person, Long> {

}
