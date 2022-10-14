package planit.people.preparation.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Google_Connector.Google_Helper;
import planit.people.preparation.Responses.CalendarResponse;

import java.io.IOException;

@Service
public class Calendar_Service {
    private final User_Service user_service;

    @Autowired
    public Calendar_Service(User_Service user_service) {
        this.user_service = user_service;
    }

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail, String google_id) throws IOException {
        Google_Helper google_helper = new Google_Helper(user_service.getRefreshToken(google_id), true);
        System.out.println("DTO_NewEventDetail: " + newEventDetail);
        //google_helper.getFreeBusy(newEventDetail.start_date(), newEventDetail.end_date());
        return google_helper.createEvent(newEventDetail);
    }

}
