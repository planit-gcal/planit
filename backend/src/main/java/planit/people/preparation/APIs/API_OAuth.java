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

@RestController
@RequestMapping(path = "plan-it/oauth",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_OAuth {

    private final IDAO_GoogleAccount idaoGoogleAccount;

    @Autowired
    public API_OAuth(IDAO_GoogleAccount idaoGoogleAccount) {
        this.idaoGoogleAccount = idaoGoogleAccount;
    }

    //TODO: Security Issue, this MUST be changed
    @GetMapping(path = "/emails/{email}/planit-user-id")
    public ResponseEntity<String> getUserIdFromEmail(@PathVariable String email) {
        try {
            return new ResponseEntity<>(idaoGoogleAccount.getIdOfUserFromEmail(email), HttpStatus.FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
