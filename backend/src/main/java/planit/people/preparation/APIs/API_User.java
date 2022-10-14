package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Responses.UserCreationResponse;
import planit.people.preparation.Services.Service_User;

@RestController
@RequestMapping(path = "plan-it/user",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_User {
    private final Service_User serviceUser;

    @Autowired
    public API_User(Service_User serviceUser) {
        this.serviceUser = serviceUser;
    }

    @PostMapping(path = "token", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserCreationResponse> createNewUser(@RequestBody DTO_Code code) {
        System.out.println("dto code: " + code);
        try {
            return new ResponseEntity<>(serviceUser.getGoogleAccountId(code), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
