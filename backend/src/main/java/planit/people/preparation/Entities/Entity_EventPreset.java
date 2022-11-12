package planit.people.preparation.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "event_preset")
public class Entity_EventPreset {
    /**
     * The primary key of the table. Auto Generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_preset_gen")
    @SequenceGenerator(name = "event_preset_gen", sequenceName = "event_preset_seq", allocationSize = 1)
    @Column(name = "id_event_preset", nullable = false)
    private Long id_event_preset;

    /**
     * defines the name of the Event Preset
     */
    @Column(name = "name")
    private String name;

    /**
     * Defines whether a single event should be broken into smaller events in case no timeslot was found for a given event duration.
     */
    @Column(name = "break_into_smaller_events")
    private Boolean break_into_smaller_events;

    /**
     * defines the minimum duration of an event that is broken down when "break_into_smaller_events" set to true
     */
    @Column(name = "min_length_of_single_event")
    private Integer min_length_of_single_event;

    /**
     * defines the maximum duration of an event that is broken down when "break_into_smaller_events" set to true
     */
    @Column(name = "max_length_of_single_event")
    private Integer max_length_of_single_event;
    /**
     * defines the Many-Many relation between Entity_User and Entity_EventPreset
     */
    @ManyToMany(mappedBy = "entity_EventPresets",fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST}, targetEntity = Entity_User.class)
    @JsonIgnore
    private Set<Entity_User> entityUsers = new HashSet<>();
    /**
     * Create a bidirectional relation with Entity_PresetAvailability in order to enable CASCADE deletion
     */
    @OneToMany(mappedBy = "entity_EventPreset", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Entity_PresetAvailability> presetAvailabilities = new ArrayList<>();
    /**
     * Create a bidirectional relation with Entity_Guest in order to enable CASCADE deletion
     */
    @OneToMany(mappedBy = "entity_EventPreset", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Entity_Guest> guests = new ArrayList<>();
    /**
     * Create a bidirectional relation with Entity_SharePreset in order to enable CASCADE deletion
     */
    @OneToMany(mappedBy = "shared_preset", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private Set<Entity_SharePreset> shared_presets = new LinkedHashSet<>();

    /**
     * constructor used to retrieve records from the DB and create new ones.
     *
     * @param name                       name of preset
     * @param break_into_smaller_events  should an event be broken into smaller events when no proper timeslot is found
     * @param min_length_of_single_event minimum duration for an event that is resulted by setting "break_into_smaller_events" to true
     * @param max_length_of_single_event maximum duration for an event that is resulted by setting "break_into_smaller_events" to true
     */

    public Entity_EventPreset(String name, Boolean break_into_smaller_events, Integer min_length_of_single_event, Integer max_length_of_single_event) {
        this.name = name;
        this.break_into_smaller_events = break_into_smaller_events;
        this.min_length_of_single_event = min_length_of_single_event;
        this.max_length_of_single_event = max_length_of_single_event;
    }

    /**
     * constructor used to create an instance of EventPreset used to add the record as a FK in different tables
     * @param id_event_preset the id of the EventPreset
     */
    public Entity_EventPreset(Long id_event_preset) {
        this.id_event_preset = id_event_preset;
    }

    /**
     * Empty constructor needed by Spring.
     */
    public Entity_EventPreset() {
    }

    public Set<Entity_SharePreset> getShared_presets() {
        return shared_presets;
    }

    public void setShared_presets(Set<Entity_SharePreset> shared_presets) {
        this.shared_presets = shared_presets;
    }
    /**
     * Util method added to ease the process of adding a PlanIt user instance for the current EventPreset
     * @param user the PlanIt user to be added
     */
    public void addUser(Entity_User user) {
        this.entityUsers.add(user);
        user.getEntity_EventPresets().add(this);
    }

    /**
     * Util method added to ease the process of removing a PlanIt user instance for the current EventPreset
     * @param user the PlanIt user to be removed
     */
    public void removeUser(Entity_User user) {
        this.entityUsers.remove(user);
        user.getEntity_EventPresets().remove(this);
    }

    public Set<Entity_User> getEntityUsers() {
        return entityUsers;
    }

    public void setEntityUsers(Set<Entity_User> entityUsers) {
        this.entityUsers = entityUsers;
    }

    public Integer getMax_length_of_single_event() {
        return max_length_of_single_event;
    }

    public void setMax_length_of_single_event(Integer max_length_of_single_event) {
        this.max_length_of_single_event = max_length_of_single_event;
    }

    public Integer getMin_length_of_single_event() {
        return min_length_of_single_event;
    }

    public void setMin_length_of_single_event(Integer min_length_of_single_event) {
        this.min_length_of_single_event = min_length_of_single_event;
    }

    public Boolean getBreak_into_smaller_events() {
        return break_into_smaller_events;
    }

    public void setBreak_into_smaller_events(Boolean break_into_smaller_events) {
        this.break_into_smaller_events = break_into_smaller_events;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId_event_preset() {
        return id_event_preset;
    }

    public void setId_event_preset(Long id_event_preset) {
        this.id_event_preset = id_event_preset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Entity_EventPreset that = (Entity_EventPreset) o;
        return id_event_preset != null && Objects.equals(id_event_preset, that.id_event_preset);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Entity_EventPreset{" + "id_event_preset=" + id_event_preset + ", name='" + name + '\'' + ", break_into_smaller_events=" + break_into_smaller_events + ", min_length_of_single_event=" + min_length_of_single_event + ", max_length_of_single_event=" + max_length_of_single_event + '}';
    }
}
