package planit.people.preparation.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import planit.people.preparation.DAOs.IDAO_Google_account;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Entities.Entity_Google_account;
import planit.people.preparation.Entities.Entity_User;
import planit.people.preparation.GoogleConnector.GoogleHelper;
import planit.people.preparation.Responses.CalendarResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class Calendar_Service {
    private final IDAO_Google_account idao_google_account;

    @Autowired
    public Calendar_Service(IDAO_Google_account idao_google_account) {
        this.idao_google_account = idao_google_account;
    }

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail) throws IOException {
        List<Entity_Google_account> googleAccounts = idao_google_account.getEntityGoogleAccountByEmail(new ArrayList<>() {
            {
                add(newEventDetail.owner());
                addAll(newEventDetail.attendees());
            }
        });
        GoogleHelper google_helper = new GoogleHelper(getOwnerRefreshToken(googleAccounts, newEventDetail.owner()), true);
        System.out.println("DTO_NewEventDetail: " + newEventDetail);
        return google_helper.createEvent(newEventDetail, getRefreshTokenForUsers(googleAccounts));
    }
    private String getOwnerRefreshToken(List<Entity_Google_account> googleAccounts, String ownerEmail) {
        for (Entity_Google_account googleAccount : googleAccounts) {
            if (googleAccount.getEmail().equals(ownerEmail)) {
                return googleAccount.getRefresh_token();
            }
        }
        return null;
    }

    private Set<Entity_User> getPlanItUserIdFromEmail(List<Entity_Google_account> googleAccountsFromEmail) {
        Set<Entity_User> usersPlanItIds = new HashSet<>();
        for (Entity_Google_account googleAccount : googleAccountsFromEmail) {
            usersPlanItIds.add(googleAccount.getThe_user());
        }
        return usersPlanItIds;
    }

    private Set<String> getRefreshTokenForUsers(List<Entity_Google_account> googleAccountsFromEmail) {
        Set<Entity_User> usersPlanItIds = getPlanItUserIdFromEmail(googleAccountsFromEmail);
        Set<String> refreshTokens = new HashSet<>();
        List<Entity_Google_account> googleAccounts = idao_google_account.getEntityGoogleAccountsByPlanItUsers(usersPlanItIds);
        for (Entity_Google_account googleAccount : googleAccounts) {
            refreshTokens.add(googleAccount.getRefresh_token());
        }
        return refreshTokens;
    }

}
