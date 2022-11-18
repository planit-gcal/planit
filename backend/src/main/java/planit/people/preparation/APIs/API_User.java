package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DAOs.IDAO_GoogleAccount;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Responses.UserCreationResponse;
import planit.people.preparation.Services.Service_User;

import java.util.List;

@RestController
@RequestMapping(path = "plan-it/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_User {
    private final Service_User serviceUser;
    private final IDAO_GoogleAccount idaoGoogleAccount;

    @Autowired
    public API_User(Service_User serviceUser, IDAO_GoogleAccount idaoGoogleAccount) {
        this.serviceUser = serviceUser;
        this.idaoGoogleAccount = idaoGoogleAccount;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserCreationResponse> createNewUser(@RequestBody DTO_Code code) {
        System.out.println("dto code: " + code);
        try {
            return new ResponseEntity<>(serviceUser.getGoogleAccountId(code), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "{planit-user-id}/emails")
    public ResponseEntity<List<String>> getAllEmailsForEmail(@PathVariable("planit-user-id") Long userId) {
        try {
            return new ResponseEntity<>(idaoGoogleAccount.getEmailsFromUserId(userId), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Remove a PlanIt User from DB.
     *
     * @param planItUserId the id of the PlanIt user who should be removed from the DB.
     * @return HttpStatus Ok if a PlanIt user is deleted successfully, else Not Found.
     * @see Service_User#revokeUserAccess(Long)
     */
    @DeleteMapping(path = "{planit-user-id}")
    public ResponseEntity<Boolean> revokeUserAccess(@PathVariable("planit-user-id") Long planItUserId) {
        try {
            serviceUser.revokeUserAccess(planItUserId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
