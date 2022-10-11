package planit.people.preparation.APIs;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DAOs.IDAO_User;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Entities.Entity_User;
import planit.people.preparation.Responses.PeopleResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "plan-it/user",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_User {
    private final IDAO_User dao_user;

    @Autowired
    public API_User(IDAO_User dao_user) {
        this.dao_user = dao_user;
    }

    /**
     * create a new user and save it in the DB using the save method of CrudRepository interface,
     * in case any error occurrence during the saving process the exception will be caught and returned to the response message
     *
     * @param user the attributes of the user object that needs to be created
     * @return ResponseEntity object that returns the newly created user with its id or an error with an appropriate https status
     * @author Jakub Seredyński
     * @see Entity_User  in order to view what are the expected attributes
     * @see org.springframework.data.repository.CrudRepository#save(Object)
     * @since 1.0
     */
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Entity_User> createUser(@RequestBody Entity_User user) {
        try {
            Entity_User entity_user = dao_user
                    .save(new Entity_User(user.getName(), user.getSurname()));
            return new ResponseEntity<>(entity_user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete an already existing user by its id by using the method deleteById from CrudRepository interface.
     * in case of an error during the deletion process, the error will be caught and returned to the message part of the response
     *
     * @param id the id of the user to be deleted.
     * @return PeopleResponse object that indicates if the user object is deleted successfully or not
     * @author Jakub Seredyński
     * @see org.springframework.data.repository.CrudRepository#deleteById(Object)
     * @since 1.0
     */
    @DeleteMapping(
            path = "{user_id}"
    )
    public ResponseEntity<PeopleResponse> deleteUser(@PathVariable("user_id") long id) {
        try {
            dao_user.deleteById(id);
            return new ResponseEntity<>(new PeopleResponse(true, "Deleted Successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new PeopleResponse(false, "Some Internal Occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get a specific user by its id, in case no user found with the provided Id, NO Content HTTPS Status is returned.
     *
     * @param id the id of the user to be retrieved
     * @return the matched user or NO Content Status
     * @author Jakub Seredyński
     * @see org.springframework.data.repository.CrudRepository#findById(Object)
     * @since 1.0
     */
    @GetMapping(
            path = "{user_id}"
    )
    public ResponseEntity<Entity_User> getUser(@PathVariable("user_id") long id) {
        Optional<Entity_User> Entity_UserData = dao_user.findById(id);
        return Entity_UserData.map(entity_user -> new ResponseEntity<>(entity_user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
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
    public ResponseEntity<List<Entity_User>> getPeople() {
        try {
            List<Entity_User> Entity_Users = new ArrayList<>();
            dao_user.findAll().forEach(Entity_Users::add);
            if (Entity_Users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(Entity_Users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "token", consumes = {MediaType.APPLICATION_JSON_VALUE})

    public Boolean getAccessToken(@RequestBody DTO_Code code) throws IOException {
        System.out.println("code: " + code.code());
        GoogleTokenResponse response = requestRefreshToken(code.code());
        assert response != null;
        System.out.println(response);
        return true;
    }
    static GoogleTokenResponse requestRefreshToken(String s) throws IOException {
        try {
            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                    "937013173995-cmhpl3s97umb1njiseqtk3c049guefi5.apps.googleusercontent.com", "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",
                    s, "http://localhost:3000")
                    .execute();
            System.out.println("response: " + response);
            System.out.println("Refrewsh Access token: " + response.getRefreshToken());
            return response;
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                System.err.println("Error: " + e.getDetails().getError());
                if (e.getDetails().getErrorDescription() != null) {
                    System.err.println(e.getDetails().getErrorDescription());
                }
                if (e.getDetails().getErrorUri() != null) {
                    System.err.println(e.getDetails().getErrorUri());
                }
            } else {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }



}
