package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.DTOs.DTO_PresetDetail;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Services.Service_Calendar;

import java.util.List;

@RestController
@RequestMapping(path = "plan-it/calendar",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_Calendar {
    private final Service_Calendar serviceCalendar;

    @Autowired
    public API_Calendar(Service_Calendar serviceCalendar) {
        this.serviceCalendar = serviceCalendar;
    }

    @PostMapping(path = "new-event",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CalendarResponse> createNewEvent(@RequestBody DTO_NewEventDetail newEventDetail) {
        try {
            System.out.println("request event: " + newEventDetail);
            return new ResponseEntity<>(serviceCalendar.createEvent(newEventDetail), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Create a new Preset Detail for a given PlanIt User
     *
     * @param planItUserId the PlanIt User Id for whom a new Preset Detail will be created.
     * @param dtoNewPreset the body of the new Preset Detail
     * @return the status code of the creation process.
     */
    @PostMapping(path = "new-preset/{planit-user-id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> createNewPreset(@PathVariable("planit-user-id") Integer planItUserId, @RequestBody DTO_PresetDetail dtoNewPreset) {
        try {
            System.out.println("request: " + dtoNewPreset);
            serviceCalendar.upsertPresetDetail(Long.valueOf(planItUserId), dtoNewPreset, false);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all Preset Details for a given PlanIt User.
     *
     * @param planItUserId the PlanIt User Id for whom we need to get their Preset Details
     * @return List of Preset Detail
     */
    @GetMapping(path = "getAllPresets/{planit-user-id}")
    public ResponseEntity<List<DTO_PresetDetail>> getAllPresetFromPlanItUserId(@PathVariable("planit-user-id") Long planItUserId) {
        try {
            return new ResponseEntity<>(serviceCalendar.getEventPresetsByPlanItUserId(planItUserId), HttpStatus.FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * NOT COMPLETE
     *
     * @param dtoPresetDetail the body of the updated preset detail.
     * @return Status OK
     */
    @PatchMapping
            (path = "update-preset", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> updateNewPreset(@RequestBody DTO_PresetDetail dtoPresetDetail) {
        try {
            System.out.println("request: " + dtoPresetDetail);
            serviceCalendar.upsertPresetDetail(null, dtoPresetDetail, true);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
