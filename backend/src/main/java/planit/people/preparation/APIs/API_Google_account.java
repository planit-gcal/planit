package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DAOs.IDAO_Google_account;
import planit.people.preparation.Entities.Entity_Google_account;
import planit.people.preparation.Responses.PeopleResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping(path = "plan-it/google_account",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_Google_account {
    private final IDAO_Google_account dao_google_account;

    @Autowired
    public API_Google_account(IDAO_Google_account dao_google_account) {
        this.dao_google_account = dao_google_account;
    }

    /**
     * create a new google_account and save it in the DB using the save method of CrudRepository interface,
     * in case any error occurrence during the saving process the exception will be caught and returned to the response message
     *
     * @param google_account the attributes of the google_account object that needs to be created
     * @return ResponseEntity object that returns the newly created google_account with its id or an error with an appropriate https status
     * @author Jakub Seredyński
     * @see Entity_Google_account  in order to view what are the expected attributes
     * @see org.springframework.data.repository.CrudRepository#save(Object)
     * @since 1.0
     */
    @PostMapping(
            path = "create-google_account",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Entity_Google_account> createGoogle_account(@RequestBody Entity_Google_account google_account) {
        try {
            Entity_Google_account entity_google_account = dao_google_account
                    .save(new Entity_Google_account(google_account.getRefresh_token(), google_account.getThe_user(), google_account.getEmail()));
            return new ResponseEntity<>(entity_google_account, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete an already existing google_account by its id by using the method deleteById from CrudRepository interface.
     * in case of an error during the deletion process, the error will be caught and returned to the message part of the response
     *
     * @param id the id of the google_account to be deleted.
     * @return PeopleResponse object that indicates if the google_account object is deleted successfully or not
     * @author Jakub Seredyński
     * @see org.springframework.data.repository.CrudRepository#deleteById(Object)
     * @since 1.0
     */
    @DeleteMapping(
            path = "{google_account_id}"
    )
    public ResponseEntity<PeopleResponse> deleteGoogle_account(@PathVariable("google_account_id") long id) {
        try {
            dao_google_account.deleteById(id);
            return new ResponseEntity<>(new PeopleResponse(true, "Deleted Successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new PeopleResponse(false, "Some Internal Occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get a specific google_account by its id, in case no google_account found with the provided id, NO Content HTTPS Status is returned.
     *
     * @param id the id of the google_account to be retrieved
     * @return the matched google_account or NO Content Status
     * @author Jakub Seredyński
     * @see org.springframework.data.repository.CrudRepository#findById(Object)
     * @since 1.0
     */
    @GetMapping(
            path = "{google_account_id}"
    )
    public ResponseEntity<Entity_Google_account> getGoogle_account(@PathVariable("google_account_id") long id) {
        Optional<Entity_Google_account> Entity_Google_accountData = dao_google_account.findById(id);
        return Entity_Google_accountData.map(entity_google_account -> new ResponseEntity<>(entity_google_account, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    /**
     * get all people from the DB.
     * in case of any errors, Internal Server Error status will be returned
     *
     * @return a list of people in the DB or No_Content if no people exists
     * @author Jakub Seredyński
     * @see org.springframework.data.repository.CrudRepository#findAll()
     * @since 1.0
     */
    @GetMapping
    public ResponseEntity<List<Entity_Google_account>> getPeople() {
        try {
            List<Entity_Google_account> Entity_Google_accounts = new ArrayList<>();
            dao_google_account.findAll().forEach(Entity_Google_accounts::add);
            if (Entity_Google_accounts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(Entity_Google_accounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

