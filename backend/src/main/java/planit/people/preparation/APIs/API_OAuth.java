package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import planit.people.preparation.DAOs.IDAO_GoogleAccount;
import planit.people.preparation.Entities.Entity_GoogleAccount;
import planit.people.preparation.Responses.UserCreationResponse;
import planit.people.preparation.Scheduling.Converter;

@RestController
@RequestMapping(path = "plan-it/oauth",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_OAuth {

    private final IDAO_GoogleAccount idaoGoogleAccount;

    @Autowired
    public API_OAuth(IDAO_GoogleAccount idaoGoogleAccount) {
        this.idaoGoogleAccount = idaoGoogleAccount;
    }

    @GetMapping(path = "planit-user-id/{email}")
    public ResponseEntity<UserCreationResponse> getUserIdFromEmail(@PathVariable String email) {
        try {
            Entity_GoogleAccount account = idaoGoogleAccount.getIdOfUserFromEmail(Converter.decodeURLString(email));
            return new ResponseEntity<>(new UserCreationResponse(
                    account.getThe_user().getUser_id(), account.getId()
            ), HttpStatus.FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
