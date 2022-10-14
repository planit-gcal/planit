package planit.people.preparation.APIs;

import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DAOs.IDAO_Google_account;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Responses.UserCreationResponse;
import planit.people.preparation.Services.User_Service;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

@RestController
@RequestMapping(path = "plan-it/user",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_User {
    private final User_Service user_service;
    private final IDAO_Google_account idaoGoogleAccount;

    @Autowired
    public API_User(User_Service user_service, IDAO_Google_account idaoGoogleAccount) {
        this.user_service = user_service;
        this.idaoGoogleAccount = idaoGoogleAccount;
    }

    @PostMapping(path = "token", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserCreationResponse> createNewUser(@RequestBody DTO_Code code) {
        System.out.println("dto code: " + code);
        try {
            return new ResponseEntity<>(user_service.getGoogleAccountId(code), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "getAllEmails/{userId}")
    public ResponseEntity<List<String>> getAllEmailsForEmail(@PathVariable Long userId) {
        try {
            return new ResponseEntity<>(idaoGoogleAccount.getEmailsFromUserId(userId), HttpStatus.FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
