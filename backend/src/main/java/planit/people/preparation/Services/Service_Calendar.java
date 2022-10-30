package planit.people.preparation.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import planit.people.preparation.DAOs.*;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.DTOs.DTO_PresetDetail;
import planit.people.preparation.Entities.*;
import planit.people.preparation.GoogleConnector.GoogleHelper;
import planit.people.preparation.Responses.CalendarResponse;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
public class Service_Calendar {
    private final IDAO_GoogleAccount idaoGoogleAccount;
    private final IDAO_EventPreset idaoEventPreset;
    private final IDAO_Guest idao_guest;
    private final IDAO_PresetAvailability idaoPresetAvailability;
    private final IDAO_User idaoUser;

    @Autowired
    public Service_Calendar(IDAO_GoogleAccount idao_google_account, IDAO_EventPreset idaoEventPreset, IDAO_Guest idao_guest, IDAO_PresetAvailability idaoPresetAvailability, IDAO_User idaoUser) {
        this.idaoGoogleAccount = idao_google_account;
        this.idaoEventPreset = idaoEventPreset;
        this.idao_guest = idao_guest;
        this.idaoPresetAvailability = idaoPresetAvailability;
        this.idaoUser = idaoUser;
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

    /**
     * Create a new Preset detail or update an already existing record for a PlanIt user.
     * The creation will happen in the following steps:
     * 1- Save the EventPreset into the DB, the Entity_EventPreset id is generated.
     * 2- Add the created EventPreset under the PlanIt User.
     * 3- Set the newly generated id of the Entity_EventPreset to the list of Availabilities and Guests.
     * 4- save the Availabilities and Guests into the DB.
     * The update  process takes the following steps:
     * 1- get EventPreset, PresetAvailabilities and guests from the Preset detail and update the records in the DB.
     * The function is annotated with Transactional in order to roll back all transactions that were made to the DB before a translation had failed
     *
     * @param planItUserId the PlanIt user Id for whom the system is creating the new preset.
     * @param dtoNewPreset the body of the new preset detail.
     * @param isUpdate     indicates whether the function is used for an insert or an update operation.
     */
    @Transactional
    public void upsertPresetDetail(Long planItUserId, DTO_PresetDetail dtoNewPreset, Boolean isUpdate) {
        Entity_EventPreset existing = dtoNewPreset.event_preset();
        List<Entity_PresetAvailability> presetAvailabilities = dtoNewPreset.preset_availability();
        List<Entity_Guest> presetGuests = dtoNewPreset.guests();
        if (isUpdate) {
            idaoEventPreset.save(existing);
            idaoPresetAvailability.saveAll(presetAvailabilities);
            idao_guest.saveAll(presetGuests);
            return;
        }
        Entity_User entityUser = new Entity_User(planItUserId);
        System.out.println("Entity User: " + entityUser);
        Entity_EventPreset eventPreset = new Entity_EventPreset(
                existing.getName(),
                existing.getBreak_into_smaller_events(),
                existing.getMin_length_of_single_event(),
                existing.getMax_length_of_single_event()
        );
        Entity_EventPreset newEventPreset = idaoEventPreset.save(eventPreset);
        System.out.println("newEventPreset: " + newEventPreset);
        idaoUser.findById(planItUserId).map(planItUser -> {
            planItUser.addPreset(newEventPreset);
            return idaoUser.save(planItUser);
        });

        for (Entity_PresetAvailability presetAvailability : presetAvailabilities) {
            presetAvailability.setEntity_EventPreset(newEventPreset);
        }
        for (Entity_Guest guest : presetGuests) {
            guest.setEntity_EventPreset(newEventPreset);
        }
        idaoPresetAvailability.saveAll(presetAvailabilities);
        idao_guest.saveAll(presetGuests);
    }

    /**
     * Get Preset details for a PlanIt User. The retrieval of details will occur in the following steps:
     * 1- get all EventPreset for the given PlanIt User. In case no result found. an error will be thrown
     * 2- Query guests and preset availabilities from the retrieved list of EventPresets.
     * 3- Convert all returned results from the DB into a list of PresetDetail records.
     *
     * @param planItUserId the PlanIt user id for whom the system is retrieving the preset details.
     * @return a list of preset details.
     */
    public List<DTO_PresetDetail> getEventPresetsByPlanItUserId(Long planItUserId) {
        List<DTO_PresetDetail> newPresets = new ArrayList<>();
        List<Entity_EventPreset> eventPresets = idaoUser.getAllEventPresetByPlanItId(planItUserId);
        if (eventPresets.isEmpty()) {
            throw new EmptyResultDataAccessException(0);
        } else {
            Map<Entity_EventPreset, List<Entity_PresetAvailability>> availabilities = getAvailabilities(idaoPresetAvailability.getAllByIdEventPreset(eventPresets));
            Map<Entity_EventPreset, List<Entity_Guest>> guests = getGuests(idao_guest.getAllByEntityEventPreset(eventPresets));
            for (Entity_EventPreset eventPreset : eventPresets) {
                newPresets.add(
                        new DTO_PresetDetail(
                                eventPreset,
                                guests.get(eventPreset),
                                availabilities.get(eventPreset))
                );
            }
        }
        return newPresets;
    }

    /**
     * Map each guest to its own EventPreset.
     *
     * @param guests all guests stored in the DB for the given PlanIt user Id.
     * @return a map of list of guests mapped to their parent preset.
     */
    private Map<Entity_EventPreset, List<Entity_Guest>> getGuests(List<Entity_Guest> guests) {
        Map<Entity_EventPreset, List<Entity_Guest>> result = new HashMap<>();
        for (Entity_Guest guest : guests) {
            if (result.containsKey(guest.getEntity_EventPreset())) {
                result.get(guest.getEntity_EventPreset()).add(
                        new Entity_Guest(
                                guest.getId_event_guest(),
                                guest.getEmail(),
                                guest.getObligatory()
                        )
                );
            } else {
                result.put(guest.getEntity_EventPreset(), new ArrayList<>() {
                    {
                        add(new Entity_Guest(
                                guest.getId_event_guest(),
                                guest.getEmail(),
                                guest.getObligatory()
                        ));
                    }
                });
            }
        }
        return result;
    }

    /**
     * Map each Preset Availability to its own EventPreset.
     *
     * @param availabilities all availabilities stored in the DB for the given PlanIt user Id.
     * @return a map of list of availabilities mapped to their parent preset.
     */
    private Map<Entity_EventPreset, List<Entity_PresetAvailability>> getAvailabilities(List<Entity_PresetAvailability> availabilities) {
        Map<Entity_EventPreset, List<Entity_PresetAvailability>> result = new HashMap<>();
        for (Entity_PresetAvailability availability : availabilities) {
            if (result.containsKey(availability.getEntity_EventPreset())) {
                result.get(availability.getEntity_EventPreset()).add(
                        new Entity_PresetAvailability(
                                availability.getId_preset_availability(),
                                availability.getDay(),
                                availability.getDay_off(),
                                availability.getStart_available_time(),
                                availability.getEnd_available_time()
                        )
                );
            } else {
                result.put(availability.getEntity_EventPreset(), new ArrayList<>() {
                    {
                        add(new Entity_PresetAvailability(
                                availability.getId_preset_availability(),
                                availability.getDay(),
                                availability.getDay_off(),
                                availability.getStart_available_time(),
                                availability.getEnd_available_time()
                        ));
                    }
                });
            }
        }
        return result;
    }
}
