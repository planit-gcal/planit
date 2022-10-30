package planit.people.preparation.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "entity_EventPresets")
    @JsonIgnore
    private Set<Entity_User> entityUsers = new HashSet<>();

    /**
     * constructor used to retrieve records from the DB and create new ones.
     * @param name name of preset
     * @param break_into_smaller_events should an event be broken into smaller events when no proper timeslot is found
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
     * Empty constructor needed by Spring.
     */
    public Entity_EventPreset() {
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
        return "Entity_EventPreset{" +
                "id_event_preset=" + id_event_preset +
                ", name='" + name + '\'' +
                ", break_into_smaller_events=" + break_into_smaller_events +
                ", min_length_of_single_event=" + min_length_of_single_event +
                ", max_length_of_single_event=" + max_length_of_single_event +
                '}';
    }
}
