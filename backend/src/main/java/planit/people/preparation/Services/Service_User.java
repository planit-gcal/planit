package planit.people.preparation.Services;

import com.google.api.services.oauth2.model.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import planit.people.preparation.DAOs.IDAO_GoogleAccount;
import planit.people.preparation.DAOs.IDAO_User;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Entities.Entity_EventPreset;
import planit.people.preparation.Entities.Entity_GoogleAccount;
import planit.people.preparation.Entities.Entity_User;
import planit.people.preparation.GoogleConnector.GoogleConnector;
import planit.people.preparation.GoogleConnector.GoogleHelper;
import planit.people.preparation.Responses.UserCreationResponse;

import java.util.List;
import java.util.Set;

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
            Entity_User entity_user = idaoUser.save(new Entity_User(user.getName(), user.getSurname()));
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
            Entity_GoogleAccount entity_google_account = idaoGoogleAccount.save(new Entity_GoogleAccount(google_account.getEmail(), google_account.getThe_user(), google_account.getRefresh_token()));
            System.out.println("newly created user: " + entity_google_account);
            return new UserCreationResponse(entity_google_account.getThe_user().getUser_id(), entity_google_account.getId());
        } catch (Exception e) {
            return null;
        }
    }

    public UserCreationResponse createGoogleAccount(DTO_Code dto_code) throws Exception {
        validateCreateGoogleAccountRequest(dto_code);
        GoogleHelper google_helper = new GoogleHelper(dto_code.code(), false);
        System.out.println("google helper: " + google_helper);
        Entity_User entity_user;
        Userinfo userinfo = google_helper.getUserInfo();
        System.out.println("google userinfo: " + userinfo);
        Entity_GoogleAccount existingAccount = idaoGoogleAccount.getGoogleAccountFromEmail(userinfo.getEmail());
        if (existingAccount != null) {
            return updateUserRefreshToken(existingAccount, google_helper.getRefreshToken(), dto_code.planit_user_id());
        }
        if (dto_code.planit_user_id() == null) {
            entity_user = create_new_user(new Entity_User(userinfo.getGivenName(), userinfo.getFamilyName()));
            System.out.println("google entity_user: " + entity_user);
        } else {
            entity_user = new Entity_User(dto_code.planit_user_id());
            System.out.println("google entity_user2: " + entity_user);
        }
        System.out.println("Entity User: " + entity_user);
        Entity_GoogleAccount google_account = new Entity_GoogleAccount(userinfo.getEmail(), entity_user, google_helper.getRefreshToken());
        return create_new_google_account(google_account);
    }

    private UserCreationResponse updateUserRefreshToken(Entity_GoogleAccount existingAccount, String newRT, Long requestPlanItUser) {
        if (requestPlanItUser != null && !existingAccount.getThe_user().getUser_id().equals(requestPlanItUser)) {
            idaoUser.findById(requestPlanItUser).ifPresent(existingAccount::setThe_user);
        }
        existingAccount.setRefresh_token(newRT);
        idaoGoogleAccount.save(existingAccount);
        return new UserCreationResponse(existingAccount.getThe_user().getUser_id(), existingAccount.getId());
    }

    private void validateCreateGoogleAccountRequest(DTO_Code code) throws Exception {
        if (code.code() == null) {
            throw new Exception("DATA VALIDATION ERROR: Code must be provided");
        }else if(code.planit_user_id() != null && !idaoUser.existsById(code.planit_user_id())){
            throw new Exception("DATA VALIDATION ERROR: Provided planit_user_id does NOT exist");
        }
    }

    /**
     * Remove a PlanIt User. the deletion process will be done in the following steps:
     * -Check if the provided PlanIt Id exists, if yes
     * a- retrieve the PlanIt User from DB
     * b- retrieve all Google Accounts that belong to the retrieved PlanIt User
     * c- make a callout to google in order to revoke the RefreshToken for each Google Account
     * d- delete all google account from DB
     * e- remove all Event Preset that belong to the retrieved PlanIt User
     * d- delete the PlanIt User form DB.
     * In case no PlanIt User was found with the provided planItUserId, an error will be thrown.
     *
     * @param planItUserId the id of the PlanIt user who should be removed from the DB.
     * @see GoogleConnector#revokeAccess
     */
    public void revokeUserAccess(Long planItUserId) {
        if (idaoUser.existsById(planItUserId)) {
            Entity_User planItUser = idaoUser.findById(planItUserId).orElse(null);
            assert planItUser != null;
            List<Entity_GoogleAccount> googleAccounts = idaoGoogleAccount.getEntityGoogleAccountsByPlanItUsers(Set.of(planItUser));
            System.out.println("Google Accounts: " + googleAccounts);
            if (googleAccounts != null && !googleAccounts.isEmpty()) {
                for (Entity_GoogleAccount googleAccount : googleAccounts) {
                    GoogleConnector.revokeAccess(googleAccount.getRefresh_token());
                }
                idaoGoogleAccount.deleteAll(googleAccounts);
                List<Entity_EventPreset> eventPresets = idaoUser.getAllEventPresetByPlanItId(planItUserId);
                for (Entity_EventPreset eventPreset : eventPresets) {
                    planItUser.removePreset(eventPreset);
                }
                idaoUser.delete(planItUser);
            }
        } else {
            //TODO THROW USER NOT FOUND ERROR
        }
    }


}
