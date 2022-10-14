package planit.people.preparation.Services;

import com.google.api.services.oauth2.model.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import planit.people.preparation.DAOs.IDAO_Google_account;
import planit.people.preparation.DAOs.IDAO_User;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Entities.Entity_Google_account;
import planit.people.preparation.Entities.Entity_User;
import planit.people.preparation.GoogleConnector.GoogleHelper;
import planit.people.preparation.Responses.UserCreationResponse;

import java.io.IOException;
import java.util.Optional;

@Service
public class User_Service {
    private final IDAO_User idao_user;
    private final IDAO_Google_account idao_google_account;

    @Autowired
    public User_Service(IDAO_User idao_user, IDAO_Google_account idao_google_account) {
        this.idao_user = idao_user;
        this.idao_google_account = idao_google_account;
    }

    public Entity_User create_new_user(Entity_User user) {
        try {
            System.out.println("newly Entity_User user: " + user);
            Entity_User entity_user = idao_user
                    .save(new Entity_User(user.getName(), user.getSurname()));
            System.out.println("newly created user: " + entity_user);
            return entity_user;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }

    public UserCreationResponse create_new_google_account(Entity_Google_account google_account) {
        try {
            Entity_Google_account entity_google_account = idao_google_account
                    .save(new Entity_Google_account(google_account.getEmail(), google_account.getThe_user(), google_account.getRefresh_token()));
            System.out.println("newly created user: " + entity_google_account);
            return new UserCreationResponse(String.valueOf(entity_google_account.getThe_user().getUser_id()),String.valueOf(entity_google_account.getId()));
        } catch (Exception e) {
            return null;
        }
    }

    public String getRefreshToken(String google_account) {
        return getGoogleAccount(google_account).getRefresh_token();
    }

    public Entity_Google_account getGoogleAccount(String google_account) {
        Optional<Entity_Google_account> google_accountData = idao_google_account.findById(Long.valueOf(google_account));
        ResponseEntity<Entity_Google_account> responseEntity = google_accountData.map(entity_google_account -> new ResponseEntity<>(entity_google_account, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
        return responseEntity.getBody();
    }

    public UserCreationResponse getGoogleAccountId(DTO_Code dto_code) throws IOException {
        try {
            GoogleHelper google_helper = new GoogleHelper(dto_code.code(), false);
            System.out.println("google helper: " + google_helper);
            Entity_User entity_user;
            Userinfo userinfo = google_helper.getUserInfo();
            System.out.println("google userinfo: " + userinfo);
            if (dto_code.planit_userId() == null) {
                entity_user = create_new_user(new Entity_User(userinfo.getName(), userinfo.getFamilyName()));
                System.out.println("google entity_user: " + entity_user);
            } else {
                entity_user = new Entity_User(Long.valueOf(dto_code.planit_userId()));
                System.out.println("google entity_user2: " + entity_user);
            }
            System.out.println("Entity User: " + entity_user);
            Entity_Google_account google_account = new Entity_Google_account(userinfo.getEmail(), entity_user, google_helper.getRefreshToken());
            return create_new_google_account(google_account);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}
