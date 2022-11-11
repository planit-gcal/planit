package planit.people.preparation.APIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.DTOs.DTO_PresetDetail;
import planit.people.preparation.DTOs.DTO_SharePreset;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Services.Service_Calendar;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "plan-it/calendar", produces = {MediaType.APPLICATION_JSON_VALUE})
public class API_Calendar {
    private final Service_Calendar serviceCalendar;

    @Autowired
    public API_Calendar(Service_Calendar serviceCalendar) {
        this.serviceCalendar = serviceCalendar;
    }

    /**
     * create a new event
     *
     * @param newEventDetail the event details
     * @return HttpStatus.OK if event is created else HttpStatus.BAD_REQUEST
     */
    @PostMapping(path = "events", consumes = {MediaType.APPLICATION_JSON_VALUE})
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
     * @return the new preset detail
     * @see Service_Calendar#upsertPresetDetail(Long, DTO_PresetDetail, Boolean)
     */
    @PostMapping(path = "users/{planit-user-id}/presets", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DTO_PresetDetail> createNewPreset(@PathVariable("planit-user-id") Integer planItUserId, @RequestBody DTO_PresetDetail dtoNewPreset) {
        try {
            System.out.println("createNewPreset request: " + dtoNewPreset);
            return new ResponseEntity<>(serviceCalendar.upsertPresetDetail(Long.valueOf(planItUserId), dtoNewPreset, false), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all Preset Details for a given PlanIt User.
     * Known issue: the time in preset_availability[].end_available_time and .start_available_time in the response, is in the format of HH:mm:ss and not HH:mm as it must be in the request
     * @param planItUserId the PlanIt User Id for whom we need to get their Preset Details
     * @return List of Preset Detail
     */
    @GetMapping(path = "users/{planit-user-id}/presets")
    public ResponseEntity<List<DTO_PresetDetail>> getAllPresetFromPlanItUserId(@PathVariable("planit-user-id") Long planItUserId) {
        try {
            return new ResponseEntity<>(serviceCalendar.getEventPresetsByPlanItUserId(planItUserId), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an already existing EventPreset
     *
     * @param dtoPresetDetail the body of the updated preset detail.
     * @param planItUserId    the id of the PlanIt User who requested the update
     * @return the updated preset detail
     * @see Service_Calendar#upsertPresetDetail(Long, DTO_PresetDetail, Boolean)
     */
    @PatchMapping(path = "users/{planit-user-id}/presets", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DTO_PresetDetail> updatePreset(@RequestBody DTO_PresetDetail dtoPresetDetail, @PathVariable("planit-user-id") Long planItUserId) {
        try {
            System.out.println("request: " + dtoPresetDetail);
            return new ResponseEntity<>(serviceCalendar.upsertPresetDetail(planItUserId, dtoPresetDetail, true), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete an EventPreset instance for a given PlanItUser
     *
     * @param planItUserId the id of the PlanIt user whose instance should be removed.
     * @param presetId     the id of the EventPreset which should be removed.
     * @return HttpStatus.OK if deleted else HttpStatus.BAD_REQUEST
     */
    @DeleteMapping(path = "users/{planit-user-id}/presets/{preset-id}")
    public ResponseEntity<Boolean> deletePreset(@PathVariable("planit-user-id") Long planItUserId, @PathVariable("preset-id") Long presetId) {
        try {
            serviceCalendar.deletePreset(planItUserId, presetId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * share an EventPreset with another PlanIt User
     *
     * @param sharePreset contains the sharing information
     * @return HttpStatus.OK if invite email is sent else HttpStatus.BAD_REQUEST
     */
    @PostMapping(path = "presets/share", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> sharePreset(@RequestBody DTO_SharePreset sharePreset) {
        try {
            serviceCalendar.sharePreset(sharePreset);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * accept the invite to join a preset
     * @apiNote NOT COMPLETE
     * @param eventCode the hash code used to identify the shared event information
     * @return HttpStatus.OK if the user is able to join the preset else HttpStatus.BAD_REQUEST
     */

    @GetMapping(path = "presets/accept/{event-code}")
    public ResponseEntity<Boolean> acceptPresetInvite(@PathVariable("event-code") UUID eventCode) {
        try {
            System.out.println("eventCode: " + eventCode);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
