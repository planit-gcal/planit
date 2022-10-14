package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Services.Calendar_Service;

@RestController
@RequestMapping(path = "plan-it/calendar",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_Calendar {
    private final Calendar_Service calendar_service;

    @Autowired
    public API_Calendar(Calendar_Service calendar_service) {
        this.calendar_service = calendar_service;
    }

    @PostMapping(path = "new-event",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CalendarResponse> createNewEvent(@RequestBody DTO_NewEventDetail newEventDetail) {
        try {
            System.out.println("request event: " + newEventDetail);
            return new ResponseEntity<>(calendar_service.createEvent(newEventDetail), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
}
