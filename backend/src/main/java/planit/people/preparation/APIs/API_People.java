package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DAOs.IDAO_Person;
import planit.people.preparation.Entities.Entity_Person;
import planit.people.preparation.Responses.PeopleResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * APIs controller class for the entity Person
 *
 * @author Mustafa Alhamoud
 * @version 1.0
 * @see IDAO_Person  in order to view all functionalities used for the porpose of this class
 * @see PeopleResponse in order to view the response object used for some methods that are used in this class
 * @since 1.0
 */
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80", "http://localhost"})
@RestController
@RequestMapping(path = "plan-it/people",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_People {

    private final IDAO_Person dao_person;

    @Autowired
    public API_People(IDAO_Person dao_person) {
        this.dao_person = dao_person;
    }

    /**
     * create a new person and save it in the DB using the save method of CrudRepository interface,
     * in case any error occurrence during the saving process the exception will be caught and returned to the response message
     *
     * @param person the attributes of the person object that needs to be created
     * @return ResponseEntity object that returns the newly created person with its id or an error with an appropriate https status
     * @author Mustafa Alhamoud
     * @see Entity_Person  in order to view what are the expected attributes
     * @see org.springframework.data.repository.CrudRepository#save(Object)
     * @since 1.0
     */
    @PostMapping(
            path = "create-person",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Entity_Person> createPerson(@RequestBody Entity_Person person) {
        try {
            Entity_Person entity_person = dao_person
                    .save(new Entity_Person(person.getGender(), person.getName(), person.getSurname(), person.getAge()));
            return new ResponseEntity<>(entity_person, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete an already existing person by its id by using the method deleteById from CrudRepository interface.
     * in case of an error during the deletion process, the error will be caught and returned to the message part of the response
     *
     * @param id the id of the person to be deleted.
     * @return PeopleResponse object that indicates if the person object is deleted successfully or not
     * @author Mustafa Alhamoud
     * @see org.springframework.data.repository.CrudRepository#deleteById(Object)
     * @since 1.0
     */
    @DeleteMapping(
            path = "{person_id}"
    )
    public ResponseEntity<PeopleResponse> deletePerson(@PathVariable("person_id") long id) {
        try {
            dao_person.deleteById(id);
            return new ResponseEntity<>(new PeopleResponse(true, "Deleted Successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new PeopleResponse(false, "Some Internal Occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get a specific person by its id, in case no person found with the provided Id, NO Content HTTPS Status is returned.
     *
     * @param id the id of the person to be retrieved
     * @return the matched person or NO Content Status
     * @author Mustafa Alhamoud
     * @see org.springframework.data.repository.CrudRepository#findById(Object)
     * @since 1.0
     */
    @GetMapping(
            path = "{person_id}"
    )
    public ResponseEntity<Entity_Person> getPerson(@PathVariable("person_id") long id) {
        Optional<Entity_Person> Entity_PersonData = dao_person.findById(id);
        return Entity_PersonData.map(entity_person -> new ResponseEntity<>(entity_person, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    /**
     * get all people from the DB.
     * in case of any errors, Internal Server Error status will be returned
     *
     * @return a list of people in the DB or No_Content if no people exists
     * @author Mustafa Alhamoud
     * @see org.springframework.data.repository.CrudRepository#findAll()
     * @since 1.0
     */
    @GetMapping
    public ResponseEntity<List<Entity_Person>> getPeople() {
        try {
            List<Entity_Person> Entity_Persons = new ArrayList<>();
            dao_person.findAll().forEach(Entity_Persons::add);
            if (Entity_Persons.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(Entity_Persons, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
