package planit.people.preparation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import planit.people.preparation.APIs.API_Calendar;
import planit.people.preparation.DAOs.*;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.DTOs.DTO_PresetDetail;
import planit.people.preparation.DTOs.DTO_SharePreset;
import planit.people.preparation.Entities.*;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Services.Service_Calendar;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(API_Calendar.class)
@AutoConfigureRestDocs(outputDir = "src/main/java/planit/people/preparation/API_Documentation/snippets/calendar")

public class CalendarWebLayerTest {
    @MockBean
    IDAO_GoogleAccount idaoGoogleAccount;
    @MockBean
    IDAO_User idaoUser;
    @MockBean
    IDAO_EventPreset idaoEventPreset;
    @MockBean
    IDAO_PresetAvailability idaoPresetAvailability;
    @MockBean
    IDAO_Guest idaoGuest;
    @MockBean
    Service_Calendar serviceCalendar;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createNewPresetDetailTest() throws Exception {
        Entity_EventPreset preset = TestUtils.createPreset("Test 1", false, null, null);
        List<Entity_Guest> guests = new ArrayList<>() {
            {
                add(TestUtils.createGuest("test@gmail.com", true));
                add(TestUtils.createGuest("test2@gmail.com", true));
                add(TestUtils.createGuest("test3@gmail.com", true));
            }
        };
        List<Entity_PresetAvailability> availabilities = new ArrayList<>() {
            {
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.THURSDAY, null, null, false));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.MONDAY, null, null, true));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.SATURDAY, null, null, true));
            }
        };
        DTO_PresetDetail request = new DTO_PresetDetail(
                preset,
                guests,
                availabilities
        );
        preset.setId_event_preset(1L);
        for (int i = 0; i < guests.size(); i++) {
            guests.get(i).setId_event_guest(i+1L);
            availabilities.get(i).setId_preset_availability(i+1L);
        }
        DTO_PresetDetail response  = new DTO_PresetDetail(
                preset,
                guests,
                availabilities
        );
        doReturn(response).when(serviceCalendar).upsertPresetDetail(any(),any(), any());
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .post("/plan-it/calendar/users/{planit-user-id}/presets", 20)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("create_preset",
                        requestFields(
                                fieldWithPath("event_preset.name").description("The name of the preset to be created").type(String.class),
                                fieldWithPath("event_preset.break_into_smaller_events").description("The event should be broken into smaller events if no timeslot was found with the provided event duration.").type(Boolean.class),
                                fieldWithPath("event_preset.id_event_preset").ignored(),
                                fieldWithPath("event_preset.shared_presets").ignored(),
                                fieldWithPath("event_preset.min_length_of_single_event").description("The minimum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("event_preset.max_length_of_single_event").description("The maximum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("guests[].id_event_guest").ignored(),
                                fieldWithPath("guests[].entity_EventPreset").ignored(),
                                fieldWithPath("guests[]").description("List of guests that should always be invites when the parent preset is selected"),
                                fieldWithPath("guests[].email").description("The email of the guest").type(String.class),
                                fieldWithPath("guests[].obligatory").description("The guest's attendance in the event is obligatory").type(Boolean.class),
                                fieldWithPath("preset_availability[]").description("List of days availabilities that should be taken into account when scheduling an event"),
                                fieldWithPath("preset_availability[].id_preset_availability").ignored(),
                                fieldWithPath("preset_availability[].entity_EventPreset").ignored(),
                                fieldWithPath("preset_availability[].day").description("The day of availability ").type(String.class),
                                fieldWithPath("preset_availability[].start_available_time").optional().description("The start hour after when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("preset_availability[].end_available_time").optional().description("The end hour before when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("preset_availability[].day_off").description("No events can be created in this day").type(Boolean.class)
                        ),
                        pathParameters(
                                parameterWithName("planit-user-id").description("the id of the PlanIt User for whom the preset will be created")
                        ),
                        responseFields(
                                fieldWithPath("event_preset.name").description("The name of the preset to be created").type(String.class),
                                fieldWithPath("event_preset.break_into_smaller_events").description("The event should be broken into smaller events if no timeslot was found with the provided event duration.").type(Boolean.class),
                                fieldWithPath("event_preset.id_event_preset").description("The identifier of the Preset record, unique").type(Integer.class),
                                fieldWithPath("event_preset.shared_presets").ignored(),
                                fieldWithPath("event_preset.min_length_of_single_event").description("The minimum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("event_preset.max_length_of_single_event").description("The maximum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("guests[].id_event_guest").description("The identifier of the Preset Guest record, unique").type(Integer.class),
                                fieldWithPath("guests[].entity_EventPreset").ignored(),
                                fieldWithPath("guests[]").description("List of guests that should always be invites when the parent preset is selected"),
                                fieldWithPath("guests[].email").description("The email of the guest").type(String.class),
                                fieldWithPath("guests[].obligatory").description("The guest's attendance in the event is obligatory").type(Boolean.class),
                                fieldWithPath("preset_availability[]").description("List of days availabilities that should be taken into account when scheduling an event"),
                                fieldWithPath("preset_availability[].id_preset_availability").description("The identifier of the Preset Availability record, unique").type(Integer.class),
                                fieldWithPath("preset_availability[].entity_EventPreset").ignored(),
                                fieldWithPath("preset_availability[].day").description("The day of availability ").type(String.class),
                                fieldWithPath("preset_availability[].start_available_time").optional().description("The start hour after when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("preset_availability[].end_available_time").optional().description("The end hour before when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("preset_availability[].day_off").description("No events can be created in this day").type(Boolean.class)
                        )));

    }

    @Test
    public void getPresetForAPlanItUser() throws Exception {
        Entity_EventPreset preset1 = TestUtils.createPreset("Test 1", false, null, null, 1L);
        Entity_EventPreset preset2 = TestUtils.createPreset("Test 2", true, 30, 60, 2L);
        List<Entity_Guest> guestsForPreset1 = new ArrayList<>() {
            {
                add(TestUtils.createGuest("test@gmail.com", true, 1L));
                add(TestUtils.createGuest("test2@gmail.com", false, 2L));
                add(TestUtils.createGuest("test3@gmail.com", false, 3L));
            }
        };
        List<Entity_Guest> guestsForPreset2 = new ArrayList<>() {
            {
                add(TestUtils.createGuest("test@gmail.com", true, 4L));
                add(TestUtils.createGuest("test5@gmail.com", false, 5L));
            }
        };
        List<Entity_PresetAvailability> availabilitiesForPreset1 = new ArrayList<>() {
            {
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.THURSDAY, Time.valueOf("09:00:00"), Time.valueOf("17:00:00"), false, 1L));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.MONDAY, Time.valueOf("09:00:00"), Time.valueOf("17:00:00"), false, 2L));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.SATURDAY, null, null, true, 3L));
            }
        };
        List<Entity_PresetAvailability> availabilitiesForPreset2 = new ArrayList<>() {
            {
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.TUESDAY, Time.valueOf("09:00:00"), Time.valueOf("17:00:00"), false, 4L));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.SATURDAY, null, null, true, 5L));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.SUNDAY, null, null, true, 6L));
            }
        };
        List<DTO_PresetDetail> response = new ArrayList<>() {
            {
                add(new DTO_PresetDetail(
                        preset2,
                        guestsForPreset2,
                        availabilitiesForPreset2
                ));
                add(new DTO_PresetDetail(
                        preset1,
                        guestsForPreset1,
                        availabilitiesForPreset1
                ));

            }
        };
        doReturn(response).when(serviceCalendar).getEventPresetsByPlanItUserId(1L);
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .get("/plan-it/calendar/users/{planit-user-id}/presets", 1)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print())
                .andDo(document("get_presets",
                        responseFields(
                                fieldWithPath("[].event_preset").description("The preset information").type(String.class),
                                fieldWithPath("[].event_preset.name").description("The name of the preset to be created").type(String.class),
                                fieldWithPath("[].event_preset.break_into_smaller_events").description("The event should be broken into smaller events if no timeslot was found with the provided event duration.").type(Boolean.class),
                                fieldWithPath("[].event_preset.min_length_of_single_event").optional().description("The minimum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("[].event_preset.id_event_preset").description("The identifier of the Preset record, unique").type(Integer.class),
                                fieldWithPath("[].event_preset.shared_presets[]").ignored(),
                                fieldWithPath("[].event_preset.max_length_of_single_event").optional().description("The maximum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("[].guests[].id_event_guest").description("The identifier of the Guest record, unique").type(Integer.class),
                                fieldWithPath("[].guests[].entity_EventPreset").ignored(),
                                fieldWithPath("[].guests[]").description("List of guests that should always be invites when the parent preset is selected"),
                                fieldWithPath("[].guests[].email").description("The email of the guest").type(String.class),
                                fieldWithPath("[].guests[].obligatory").description("The guest's attendance in the event is obligatory").type(Boolean.class),
                                fieldWithPath("[].preset_availability[]").description("List of days availabilities that should be taken into account when scheduling an event"),
                                fieldWithPath("[].preset_availability[].id_preset_availability").description("The identifier of the Availability record, unique").type(Integer.class),
                                fieldWithPath("[].preset_availability[].entity_EventPreset").ignored(),
                                fieldWithPath("[].preset_availability[].day").description("The day of availability ").type(String.class),
                                fieldWithPath("[].preset_availability[].start_available_time").optional().description("The start hour after when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("[].preset_availability[].end_available_time").optional().description("The end hour before when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("[].preset_availability[].day_off").description("No events can be created in this day").type(Boolean.class)
                        ),
                        pathParameters(
                                parameterWithName("planit-user-id").description("The id of the PlanIt User whose presets should be returned")
                        )));

    }

    @Test
    public void createEventTest() throws Exception {
        Entity_EventPreset preset = TestUtils.createPreset("Test 1", false, null, null);
        List<Entity_Guest> guests = new ArrayList<>() {
            {
                add(TestUtils.createGuest("test@gmail.com", true));
                add(TestUtils.createGuest("test2@gmail.com", true));
                add(TestUtils.createGuest("test3@gmail.com", true));
            }
        };
        List<Entity_PresetAvailability> availabilities = new ArrayList<>() {
            {
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.THURSDAY, null, null, false));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.MONDAY, null, null, true));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.SATURDAY, null, null, true));
            }
        };
        DTO_PresetDetail presetDetail = new DTO_PresetDetail(
                preset,
                guests,
                availabilities
        );
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DTO_NewEventDetail newEventDetail = new DTO_NewEventDetail(
                "TEST",
                "Summary TEST",
                "PWR",
                "this is a unit test",
                presetDetail,
                "owner@email.com",
                format.parse("2022-1-10 12:00:00"),
                format.parse("2022-2-10 12:00:00"),
                60L
        );
        DateTime start = new DateTime(format.parse("2022-1-20 15:00:00"));
        DateTime end = new DateTime(format.parse("2022-1-20 16:00:00"));
        CalendarResponse response = new CalendarResponse(start, end);
        doReturn(response).when(serviceCalendar).createEvent(any());
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .post("/plan-it/calendar/events")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(newEventDetail)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("create-event",
                        requestFields(
                                fieldWithPath("name").description("The name of the event"),
                                fieldWithPath("summary").description("Small description of the event"),
                                fieldWithPath("description").description("Complete description of the event"),
                                fieldWithPath("location").description("Location of the event"),
                                fieldWithPath("owner_email").description("The email of the event owner"),
                                fieldWithPath("duration").description("The duration of the event"),
                                fieldWithPath("start_date").description("The start date after when an event should be created. The date has the following format yyyy-MM-dd HH:mm:ss"),
                                fieldWithPath("end_date").description("The end date before when an event should be created. The date has the following format yyyy-MM-dd HH:mm:ss"),
                                fieldWithPath("event_preset_detail.event_preset.name").description("The name of the preset to be created").type(String.class),
                                fieldWithPath("event_preset_detail.event_preset.break_into_smaller_events").description("The event should be broken into smaller events if no timeslot was found with the provided event duration.").type(Boolean.class),
                                fieldWithPath("event_preset_detail.event_preset.id_event_preset").description("The identifier of the Preset record, unique").type(Integer.class),
                                fieldWithPath("event_preset_detail.event_preset.shared_presets").ignored(),
                                fieldWithPath("event_preset_detail.event_preset.min_length_of_single_event").description("The minimum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("event_preset_detail.event_preset.max_length_of_single_event").description("The maximum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("event_preset_detail.guests[].id_event_guest").description("The identifier of the Preset Guest record, unique").type(Integer.class),
                                fieldWithPath("event_preset_detail.guests[].entity_EventPreset").ignored(),
                                fieldWithPath("event_preset_detail.guests[]").description("List of guests that should always be invites when the parent preset is selected"),
                                fieldWithPath("event_preset_detail.guests[].email").description("The email of the guest").type(String.class),
                                fieldWithPath("event_preset_detail.guests[].obligatory").description("The guest's attendance in the event is obligatory").type(Boolean.class),
                                fieldWithPath("event_preset_detail.preset_availability[]").description("List of days availabilities that should be taken into account when scheduling an event"),
                                fieldWithPath("event_preset_detail.preset_availability[].id_preset_availability").description("The identifier of the Preset Availability record, unique").type(Integer.class),
                                fieldWithPath("event_preset_detail.preset_availability[].entity_EventPreset").ignored(),
                                fieldWithPath("event_preset_detail.preset_availability[].day").description("The day of availability ").type(String.class),
                                fieldWithPath("event_preset_detail.preset_availability[].start_available_time").optional().description("The start hour after when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("event_preset_detail.preset_availability[].end_available_time").optional().description("The end hour before when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("event_preset_detail.preset_availability[].day_off").description("No events can be created in this day").type(Boolean.class)
                        ),
                        responseFields(
                                fieldWithPath("start_date").description("The scheduled event start date").type(DateTime.class),
                                fieldWithPath("start_date.value").description("Date/time value expressed as the number of ms since the Unix epoch.").type(Long.class),
                                fieldWithPath("start_date.dateOnly").description("Specifies whether this is a date-only value.").type(Boolean.class),
                                fieldWithPath("start_date.timeZoneShift").description("Time zone shift from UTC in minutes or 0 for date-only value.").type(Integer.class),
                                fieldWithPath("end_date").description("The scheduled event end date").type(DateTime.class),
                                fieldWithPath("end_date.value").description("Date/time value expressed as the number of ms since the Unix epoch.").type(Long.class),
                                fieldWithPath("end_date.dateOnly").description("Specifies whether this is a date-only value.").type(Boolean.class),
                                fieldWithPath("end_date.timeZoneShift").description("Time zone shift from UTC in minutes or 0 for date-only value.").type(Integer.class)
                        )
                ));
    }

    @Test
    public void updatePresetDetailTest() throws Exception {
        Entity_EventPreset preset = TestUtils.createPreset("Test 1", false, null, null, 1L);
        List<Entity_Guest> guests = new ArrayList<>() {
            {
                add(TestUtils.createGuest("test@gmail.com", true, 1L));
                add(TestUtils.createGuest("test2@gmail.com", true, 2L));
                add(TestUtils.createGuest("test3@gmail.com", true, 3L));
            }
        };
        List<Entity_PresetAvailability> availabilities = new ArrayList<>() {
            {
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.THURSDAY, null, null, false, 1L));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.MONDAY, null, null, true, 2L));
                add(TestUtils.createPresetAvailability(Entity_PresetAvailability.WeekDays.SATURDAY, null, null, true, 3L));
            }
        };
        DTO_PresetDetail request = new DTO_PresetDetail(
                preset,
                guests,
                availabilities
        );
        preset.setId_event_preset(4L);
        for (int i = 0; i < guests.size(); i++) {
            guests.get(i).setId_event_guest(i+3L);
            availabilities.get(i).setId_preset_availability(i+3L);
        }
        DTO_PresetDetail response  = new DTO_PresetDetail(
                preset,
                guests,
                availabilities
        );
        doReturn(response).when(serviceCalendar).upsertPresetDetail(any(),any(), any());
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .patch("/plan-it/calendar/users/{planit-user-id}/presets", 20)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("update_preset",
                        requestFields(
                                fieldWithPath("event_preset.name").description("The new name of the preset").type(String.class),
                                fieldWithPath("event_preset.break_into_smaller_events").description("The event should be broken into smaller events if no timeslot was found with the provided event duration.").type(Boolean.class),
                                fieldWithPath("event_preset.id_event_preset").description("The original identifier of the Event Preset record"),
                                fieldWithPath("event_preset.shared_presets").ignored(),
                                fieldWithPath("event_preset.min_length_of_single_event").description("The minimum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("event_preset.max_length_of_single_event").description("The maximum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("guests[].id_event_guest").description("The original identifier of the Preset Guest record").type(Integer.class),
                                fieldWithPath("guests[].entity_EventPreset").ignored(),
                                fieldWithPath("guests[]").description("List of guests that should always be invites when the parent preset is selected"),
                                fieldWithPath("guests[].email").description("The email of the guest").type(String.class),
                                fieldWithPath("guests[].obligatory").description("The guest's attendance in the event is obligatory").type(Boolean.class),
                                fieldWithPath("preset_availability[]").description("List of days availabilities that should be taken into account when scheduling an event"),
                                fieldWithPath("preset_availability[].id_preset_availability").description("The original identifier of the Preset Availability record").type(Integer.class),
                                fieldWithPath("preset_availability[].entity_EventPreset").ignored(),
                                fieldWithPath("preset_availability[].day").description("The day of availability ").type(String.class),
                                fieldWithPath("preset_availability[].start_available_time").optional().description("The start hour after when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("preset_availability[].end_available_time").optional().description("The end hour before when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("preset_availability[].day_off").description("No events can be created in this day").type(Boolean.class)
                        ),
                        pathParameters(
                                parameterWithName("planit-user-id").description("the id of the PlanIt User who requested the update")
                        ),
                        responseFields(
                                fieldWithPath("event_preset.name").description("The name of the preset to be created").type(String.class),
                                fieldWithPath("event_preset.break_into_smaller_events").description("The event should be broken into smaller events if no timeslot was found with the provided event duration.").type(Boolean.class),
                                fieldWithPath("event_preset.id_event_preset").description("The identifier of the Preset record, unique").type(Integer.class),
                                fieldWithPath("event_preset.shared_presets").ignored(),
                                fieldWithPath("event_preset.min_length_of_single_event").description("The minimum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("event_preset.max_length_of_single_event").description("The maximum duration for a small event (in minutes) in case \"break_into_smaller_events\" is set to true.").type(Integer.class),
                                fieldWithPath("guests[].id_event_guest").description("The identifier of the Preset Guest record, unique").type(Integer.class),
                                fieldWithPath("guests[].entity_EventPreset").ignored(),
                                fieldWithPath("guests[]").description("List of guests that should always be invites when the parent preset is selected"),
                                fieldWithPath("guests[].email").description("The email of the guest").type(String.class),
                                fieldWithPath("guests[].obligatory").description("The guest's attendance in the event is obligatory").type(Boolean.class),
                                fieldWithPath("preset_availability[]").description("List of days availabilities that should be taken into account when scheduling an event"),
                                fieldWithPath("preset_availability[].id_preset_availability").description("The identifier of the Preset Availability record, unique").type(Integer.class),
                                fieldWithPath("preset_availability[].entity_EventPreset").ignored(),
                                fieldWithPath("preset_availability[].day").description("The day of availability ").type(String.class),
                                fieldWithPath("preset_availability[].start_available_time").optional().description("The start hour after when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("preset_availability[].end_available_time").optional().description("The end hour before when events can be created. The time is provided in the following format HH:mm").type(Time.class),
                                fieldWithPath("preset_availability[].day_off").description("No events can be created in this day").type(Boolean.class)
                        )));

    }

    @Test
    public void deletePresetTest() throws Exception {
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .delete("/plan-it/calendar/users/{planit-user-id}/presets/{preset-id}", 1,1)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andDo(document("delete_preset",
                        pathParameters(
                                parameterWithName("planit-user-id").description("the id of the PlanIt User who requested the delete"),
                                parameterWithName("preset-id").description("the id of the preset to be deleted")
                        )));
    }

    @Test
    public void sharePresetTest() throws Exception {
        DTO_SharePreset request = new DTO_SharePreset(
                "inviter@email.com",
                "invitee@email.com",
                1L
        );
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .post("/plan-it/calendar/presets/share")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("share_preset",
                        requestFields(
                                fieldWithPath("inviter_email").description("the email of the inviter"),
                                fieldWithPath("invitee_email").description("the email of the invitee"),
                                fieldWithPath("preset_id").description("the id of the preset to be shared")
                        )));
    }

}
