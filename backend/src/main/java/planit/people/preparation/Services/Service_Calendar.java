package planit.people.preparation.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import planit.people.preparation.DAOs.IDAO_GoogleAccount;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Entities.Entity_GoogleAccount;
import planit.people.preparation.Entities.Entity_User;
import planit.people.preparation.GoogleConnector.GoogleHelper;
import planit.people.preparation.Responses.CalendarResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class Service_Calendar {
    private final IDAO_GoogleAccount idaoGoogleAccount;

    @Autowired
    public Service_Calendar(IDAO_GoogleAccount idao_google_account) {
        this.idaoGoogleAccount = idao_google_account;
    }

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail) throws IOException {
        List<Entity_GoogleAccount> googleAccounts = idaoGoogleAccount.getEntityGoogleAccountByEmail(new ArrayList<>() {
            {
                add(newEventDetail.owner_email());
                addAll(newEventDetail.attendee_emails());
            }
        });
        GoogleHelper google_helper = new GoogleHelper(getOwnerRefreshToken(googleAccounts, newEventDetail.owner_email()), true);
        System.out.println("DTO_NewEventDetail: " + newEventDetail);
        return google_helper.createEvent(newEventDetail, getAttendeeRefreshTokens(googleAccounts));
    }

    private String getOwnerRefreshToken(List<Entity_GoogleAccount> googleAccounts, String ownerEmail) {
        for (Entity_GoogleAccount googleAccount : googleAccounts) {
            if (googleAccount.getEmail().equals(ownerEmail)) {
                return googleAccount.getRefresh_token();
            }
        }
        return null;
    }

    private Set<Entity_User> getPlanItUserIdsFromEmails(List<Entity_GoogleAccount> googleAccountsFromEmail) {
        Set<Entity_User> usersPlanItIds = new HashSet<>();
        for (Entity_GoogleAccount googleAccount : googleAccountsFromEmail) {
            usersPlanItIds.add(googleAccount.getThe_user());
        }
        return usersPlanItIds;
    }

    private Set<String> getAttendeeRefreshTokens(List<Entity_GoogleAccount> googleAccountsFromEmail) {
        Set<Entity_User> usersPlanItIds = getPlanItUserIdsFromEmails(googleAccountsFromEmail);
        Set<String> refreshTokens = new HashSet<>();
        List<Entity_GoogleAccount> googleAccounts = idaoGoogleAccount.getEntityGoogleAccountsByPlanItUsers(usersPlanItIds);
        for (Entity_GoogleAccount googleAccount : googleAccounts) {
            refreshTokens.add(googleAccount.getRefresh_token());
        }
        return refreshTokens;
    }

}
