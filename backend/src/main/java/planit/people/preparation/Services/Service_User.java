package planit.people.preparation.Services;

import com.google.api.services.oauth2.model.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import planit.people.preparation.DAOs.IDAO_GoogleAccount;
import planit.people.preparation.DAOs.IDAO_User;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Entities.Entity_GoogleAccount;
import planit.people.preparation.Entities.Entity_User;
import planit.people.preparation.GoogleConnector.GoogleHelper;
import planit.people.preparation.Responses.UserCreationResponse;

@Service
public class Service_User {
    private final IDAO_User idaoUser;
    private final IDAO_GoogleAccount idaoGoogleAccount;

    @Autowired
    public Service_User(IDAO_User idaoUser, IDAO_GoogleAccount idaoGoogleAccount) {
        this.idaoUser = idaoUser;
        this.idaoGoogleAccount = idaoGoogleAccount;
    }

    public Entity_User create_new_user(Entity_User user) {
        try {
            System.out.println("newly Entity_User user: " + user);
            Entity_User entity_user = idaoUser
                    .save(new Entity_User(user.getName(), user.getSurname()));
            System.out.println("newly created user: " + entity_user);
            return entity_user;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }

    public UserCreationResponse create_new_google_account(Entity_GoogleAccount google_account) {
        try {
            Entity_GoogleAccount entity_google_account = idaoGoogleAccount
                    .save(new Entity_GoogleAccount(google_account.getEmail(), google_account.getThe_user(), google_account.getRefresh_token()));
            System.out.println("newly created user: " + entity_google_account);
            return new UserCreationResponse(entity_google_account.getThe_user().getUser_id(), entity_google_account.getId());
        } catch (Exception e) {
            return null;
        }
    }

    public UserCreationResponse getGoogleAccountId(DTO_Code dto_code) {
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
                entity_user = new Entity_User(dto_code.planit_userId());
                System.out.println("google entity_user2: " + entity_user);
            }
            System.out.println("Entity User: " + entity_user);
            Entity_GoogleAccount google_account = new Entity_GoogleAccount(userinfo.getEmail(), entity_user, google_helper.getRefreshToken());
            return create_new_google_account(google_account);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}
