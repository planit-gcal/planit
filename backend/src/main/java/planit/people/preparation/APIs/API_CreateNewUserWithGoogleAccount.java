package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DAOs.IDAO_Google_account;
import planit.people.preparation.DAOs.IDAO_User;
import planit.people.preparation.DTOs.DTO_User_Google_account;
import planit.people.preparation.Entities.Entity_Google_account;
import planit.people.preparation.Entities.Entity_User;

@RestController
@RequestMapping(path = "plan-it/create-new-user-with-google-account",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_CreateNewUserWithGoogleAccount {

    private final IDAO_User dao_user;
    private final IDAO_Google_account dao_google_account;

    @Autowired
    public API_CreateNewUserWithGoogleAccount(IDAO_User dao_user, IDAO_Google_account dao_google_account) {
        this.dao_user = dao_user;
        this.dao_google_account = dao_google_account;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Entity_User> createNewUserWithGoogle_account(@RequestBody DTO_User_Google_account dto_user_google_account) {
        Entity_User user = dto_user_google_account.entity_user();
        Entity_Google_account google_account = dto_user_google_account.entity_google_account();
        try {
            Entity_User entity_user = dao_user
                    .save(new Entity_User(user.getName(), user.getSurname()));
            dao_google_account.save(new Entity_Google_account(google_account.getRefresh_token(), entity_user, google_account.getEmail()));
            return new ResponseEntity<>(entity_user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
