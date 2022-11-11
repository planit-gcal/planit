package planit.people.preparation.Services;

import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import planit.people.preparation.DAOs.*;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.DTOs.DTO_PresetDetail;
import planit.people.preparation.DTOs.DTO_SharePreset;
import planit.people.preparation.Entities.*;
import planit.people.preparation.GoogleConnector.GoogleHelper;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Responses.PresetDetailResponse;
import planit.people.preparation.Utils.EmailHelper;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
public class Service_Calendar {
    private final IDAO_GoogleAccount idaoGoogleAccount;
    private final IDAO_EventPreset idaoEventPreset;
    private final IDAO_Guest idaoGuest;
    private final IDAO_PresetAvailability idaoPresetAvailability;
    private final IDAO_SharedPreset idaoSharedPreset;
    private final IDAO_User idaoUser;
    private final Configuration fmConfiguration;

    @Autowired
    public Service_Calendar(IDAO_GoogleAccount idao_google_account, IDAO_EventPreset idaoEventPreset, IDAO_Guest idaoGuest, IDAO_PresetAvailability idaoPresetAvailability, IDAO_SharedPreset idaoSharedPreset, IDAO_User idaoUser, Configuration fmConfiguration) {
        this.idaoGoogleAccount = idao_google_account;
        this.idaoEventPreset = idaoEventPreset;
        this.idaoGuest = idaoGuest;
        this.idaoPresetAvailability = idaoPresetAvailability;
        this.idaoSharedPreset = idaoSharedPreset;
        this.idaoUser = idaoUser;
        this.fmConfiguration = fmConfiguration;
    }

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail) throws IOException {
        List<Entity_GoogleAccount> googleAccounts = idaoGoogleAccount.getEntityGoogleAccountByEmail(new ArrayList<>() {
            {
                add(newEventDetail.owner_email());
                for (Entity_Guest guest : newEventDetail.event_preset_detail().guests()) {
                    add(guest.getEmail());
                }
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
     * The creation will happen in the following steps:
     * 1.1 - Save the EventPreset into the DB, the Entity_EventPreset id is generated.
     * 1.2 - Add the created EventPreset under the PlanIt User.
     * 1.3 - Set the newly generated id of the Entity_EventPreset to the list of Availabilities and Guests.
     * 1.4 - save the Availabilities and Guests into the DB.
     * The update  process takes the following steps:
     * 2.1 - get EventPreset, PresetAvailabilities and guests from the Preset detail
     * 2.2 - get all users who have access the provided preset
     * 2.3 -delete preset for all users
     * 2.4 - create a new preset detail, same as the mentioned above (1.*) process except step 1.2 is skipped.
     * 2.5 - Add the newly created preset under all retrieved users in step 2.2 of the update process.
     * The function is annotated with Transactional in order to roll back all transactions that were made to the DB before a translation had failed
     *
     * @param planItUserId the PlanIt user Id for whom the system is creating the new preset.
     * @param dtoNewPreset the body of the new preset detail.
     * @param isUpdate     indicates whether the function is used for an insert or an update operation.
     * @return The event preset id for either the newly created preset or the modified.
     * @see #deletePreset(Long, Long)
     */
    @Transactional
    public PresetDetailResponse upsertPresetDetailIntoDB(Long planItUserId, DTO_PresetDetail dtoNewPreset, Boolean isUpdate) throws Exception {
        Entity_EventPreset existing = dtoNewPreset.event_preset();
        Long eventPresetId = existing.getId_event_preset();
        List<Entity_PresetAvailability> presetAvailabilities = dtoNewPreset.preset_availability();
        List<Entity_Guest> presetGuests = dtoNewPreset.guests();
        if (isUpdate) {
            List<Entity_User> users = idaoEventPreset.getEntityUsersByEventPresetId(eventPresetId);
            deletePreset(null, eventPresetId);
            Long newPresetId = upsertPresetDetailIntoDB(null, dtoNewPreset, false).eventPresetId();
            Entity_EventPreset modifiedPreset = idaoEventPreset.findById(newPresetId).orElse(null);
            if (modifiedPreset != null) {
                for (Entity_User user : users) {
                    modifiedPreset.addUser(user);
                    user.addPreset(modifiedPreset);
                }
                idaoEventPreset.save(modifiedPreset);
                idaoUser.saveAll(users);
                return new PresetDetailResponse(planItUserId, modifiedPreset.getId_event_preset());
            }else {
                //TODO: THROW PRESET NOT FOUND
                throw new Exception();
            }

        }
        Entity_EventPreset eventPreset = new Entity_EventPreset(existing.getName(), existing.getBreak_into_smaller_events(), existing.getMin_length_of_single_event(), existing.getMax_length_of_single_event());
        Entity_EventPreset newEventPreset = idaoEventPreset.save(eventPreset);
        System.out.println("newEventPreset: " + newEventPreset);
        if (planItUserId != null) {
            idaoUser.findById(planItUserId).map(planItUser -> {
                planItUser.addPreset(newEventPreset);
                newEventPreset.addUser(planItUser);
                idaoEventPreset.save(newEventPreset);
                return idaoUser.save(planItUser);
            });
        }
        for (Entity_PresetAvailability presetAvailability : presetAvailabilities) {
            presetAvailability.setEntity_EventPreset(newEventPreset);
        }
        for (Entity_Guest guest : presetGuests) {
            guest.setEntity_EventPreset(newEventPreset);
        }
        idaoPresetAvailability.saveAll(presetAvailabilities);
        idaoGuest.saveAll(presetGuests);
        return new PresetDetailResponse(planItUserId, newEventPreset.getId_event_preset());
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
            return new ArrayList<>();
        } else {
            Map<Entity_EventPreset, List<Entity_PresetAvailability>> availabilities = getAvailabilities(idaoPresetAvailability.getAllByIdEventPreset(eventPresets));
            Map<Entity_EventPreset, List<Entity_Guest>> guests = getGuests(idaoGuest.getAllByEntityEventPreset(eventPresets));
            for (Entity_EventPreset eventPreset : eventPresets) {
                newPresets.add(new DTO_PresetDetail(eventPreset, guests.get(eventPreset), availabilities.get(eventPreset)));
            }
        }
        return newPresets;
    }

    /**
     * Create a new Preset detail or update an already existing record for a PlanIt user.
     * @param planItUserId the PlanIt user Id for whom the system is creating the new preset.
     * @param dtoNewPreset the body of the new preset detail.
     * @param isUpdate     indicates whether the function is used for an insert or an update operation.
     * @return the PresetDetail with all the newly generated ids
     * @see Service_Calendar#upsertPresetDetailIntoDB(Long, DTO_PresetDetail, Boolean)
     */
    public DTO_PresetDetail upsertPresetDetail(Long planItUserId, DTO_PresetDetail dtoNewPreset, Boolean isUpdate) throws Exception {
        PresetDetailResponse upsertedPresetIds = upsertPresetDetailIntoDB(planItUserId, dtoNewPreset, isUpdate);
        List<DTO_PresetDetail> allPresetsOfUser = getEventPresetsByPlanItUserId(planItUserId);
        for (DTO_PresetDetail detail : allPresetsOfUser) {
            if (detail.event_preset().getId_event_preset().equals(upsertedPresetIds.eventPresetId())){
                return detail;
            }
        }
        //TODO: THROW PRESET NOT FOUND
        return null;
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
            Entity_EventPreset presetKey = guest.getEntity_EventPreset();
            List<Entity_Guest> guestList = result.getOrDefault(presetKey, new ArrayList<>());
            guestList.add(new Entity_Guest(guest.getId_event_guest(), guest.getEmail(), guest.getObligatory()));
            result.put(presetKey, guestList);
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
            Entity_EventPreset presetKey = availability.getEntity_EventPreset();
            List<Entity_PresetAvailability> availabilityList = result.getOrDefault(presetKey, new ArrayList<>());
            availabilityList.add(new Entity_PresetAvailability(availability.getId_preset_availability(), availability.getDay(), availability.getDay_off(), availability.getStart_available_time(), availability.getEnd_available_time()));
            result.put(presetKey, availabilityList);
        }
        return result;
    }

    /**
     * Delete Preset Detail. the deletion process can happen in 2 ways
     * 1 - Delete all instances of a preset. this process is used for either
     *      a- the preset detail update process,
     *      b- there is only one owner of the preset,
     *      c- there is no owner of the preset.
     * 1.1 - In case of a and b,we first delete all PlanIt User instances in order to not violate the constraints in the PlanItUser-EventPreset helper table.
     * 1.2 - Delete the PlanItEvent record which would trigger the deletion of all related Guest and Availability records
     * 2 - Delete only the instance for the given PlanItUserId
     *
     * @param planItUserId the id of the PlanIt user whose instance should be removed.
     * @param presetId the id of the EventPreset which should be removed.
     */
    public void deletePreset(Long planItUserId, Long presetId) {
        Entity_EventPreset presetToBeDeleted = idaoEventPreset.findById(presetId).orElse(null);
        System.out.println("presetToBeDeleted: " + presetToBeDeleted);
        if (presetToBeDeleted != null) {
            System.out.println("Number of users owning the preset: " + presetToBeDeleted.getEntityUsers());
            if (presetToBeDeleted.getEntityUsers().isEmpty() ||
                    planItUserId == null ||
                    (
                            presetToBeDeleted.getEntityUsers().size() == 1 &&
                            Objects.equals(presetToBeDeleted.getEntityUsers().stream().toList().get(0).getUser_id(), planItUserId)
                    )
            ) {
                if (!presetToBeDeleted.getEntityUsers().isEmpty()) {
                    Set<Entity_User> updatedUsers = new HashSet<>();
                    for (Entity_User presetOwner : presetToBeDeleted.getEntityUsers()) {
                        presetOwner.removePreset(presetToBeDeleted);
                        updatedUsers.add(presetOwner);
                    }
                        idaoUser.saveAll(updatedUsers);
                }
                System.out.println("deleting all instances of the event preset");
                idaoEventPreset.delete(presetToBeDeleted);
            } else {
                Entity_User presetOwner = idaoUser.findById(planItUserId).orElse(null);
                if (presetOwner != null) {
                    presetOwner.removePreset(presetToBeDeleted);
                    idaoUser.save(presetOwner);
                } else {
                    //TODO:  THROW USER NOT FOUND
                }
            }
        } else {
            //TODO: THROW PRESET NOT FOUND
        }
    }

    /**
     * Share a Preset Detail with another PlanIt User.
     * 1- Get google account for both the Invitee and Inviter
     * 2- Get the preset to be shared
     * 3- Create a new record for Entity_SharedPreset in order to generate the new sharing code.
     * 4- Send an email using EmailHelper
     * @param sharePreset contains the sharing information
     * @throws MessagingException in case of any errors with Java Mail
     * @see EmailHelper#sendEmail(Entity_GoogleAccount, Entity_GoogleAccount, UUID, Configuration)
     */
    @Transactional
    public void sharePreset(DTO_SharePreset sharePreset) throws MessagingException {
        Entity_GoogleAccount inviteeAccount = idaoGoogleAccount.getIdOfUserFromEmail(sharePreset.invitee_email());
        Entity_GoogleAccount inviterAccount = idaoGoogleAccount.getIdOfUserFromEmail(sharePreset.inviter_email());
        if (inviterAccount != null) {
            if (inviteeAccount != null) {
                Entity_User inviterUser = inviterAccount.getThe_user();
                Entity_User inviteeUser = inviteeAccount.getThe_user();
                Entity_EventPreset presetToShare = idaoEventPreset.findById(sharePreset.preset_id()).orElse(null);
                if (presetToShare != null) {
                    Entity_SharePreset createdSharedPreset = idaoSharedPreset.save(new Entity_SharePreset(inviteeUser, inviterUser, presetToShare));
                    System.out.println("createdSharedPreset: " + createdSharedPreset);
                    EmailHelper.sendEmail(inviteeAccount, inviteeAccount, createdSharedPreset.getInvite_hash_code(), fmConfiguration);
                } else {
                    //TODO: THROW PRESET NOT FOUND
                }
            } else {
                //TODO: THROW USER NOT FOUND USER
            }
        } else {
            //TODO: THROW USER NOT FOUND USER
        }

    }
}
