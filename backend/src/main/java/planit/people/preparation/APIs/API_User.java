package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Responses.UserCreationResponse;
import planit.people.preparation.Services.User_Service;

@RestController
@RequestMapping(path = "plan-it/user",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_User {
    private final User_Service user_service;

    @Autowired
    public API_User(User_Service user_service) {
        this.user_service = user_service;
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


}
