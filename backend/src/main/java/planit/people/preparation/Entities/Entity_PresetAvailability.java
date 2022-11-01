package planit.people.preparation.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import planit.people.preparation.Utils.SqlTimeDeserializer;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "preset_availability", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueDayAndPreset", columnNames = {"day", "id_event_preset"})
})
public class Entity_PresetAvailability {
    /**
     * The primary key of the table. Auto Generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "preset_availability_gen")
    @SequenceGenerator(name = "preset_availability_gen", sequenceName = "preset_availability_seq", allocationSize = 1)
    @Column(name = "id_preset_availability", nullable = false)
    private Long id_preset_availability;

    /**
     * Foreign Key references the EventPreset for each PresetAvailability record.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_event_preset")
    private Entity_EventPreset entity_EventPreset;

    /**
     * defines the day of a record. values are predefined an enum class. The Day and Entity_EventPreset is under a unique key constrain.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false)
    private WeekDays day;

    /**
     * defines the start time of a day when an event CAN be created. the time has the following format HH:mm
     */
    @Column(name = "start_available_time")
    @JsonFormat(pattern = "HH:mm")
    @JsonDeserialize(using = SqlTimeDeserializer.class)
    private Time start_available_time;
    /**
     * defines the end time of a day when an event CAN be created. the time has the following format HH:mm
     */
    @Column(name = "end_available_time")
    @JsonFormat(pattern = "HH:mm")
    @JsonDeserialize(using = SqlTimeDeserializer.class)
    private Time end_available_time;
    /**
     * defines whether the entire day should be excluded and no event should be created ih that day
     */
    @Column(name = "day_off", nullable = false)
    private Boolean day_off = false;

    /**
     * Empty constructor needed by Spring.
     */
    public Entity_PresetAvailability() {

    }

    /**
     * Constructor used in retrieving record from the DB.
     *
     * @param id_preset_availability id of the preset availability
     * @param entity_EventPreset     EventPreset foreign key
     * @param day                    day of a record
     * @param day_off                day off
     * @param start_available_time   availability start time of a day (since result returned by a query is in Util.Date format, the constructor will convert the value into sql.Time)
     * @param end_available_time     availability end time of a day (since result returned by a query is in Util.Date format, the constructor will convert the value into sql.Time)
     */
    public Entity_PresetAvailability(Long id_preset_availability, Entity_EventPreset entity_EventPreset, WeekDays day, Boolean day_off, java.util.Date start_available_time, java.util.Date end_available_time) {
        this.id_preset_availability = id_preset_availability;
        this.entity_EventPreset = entity_EventPreset;
        this.day = day;
        this.start_available_time = start_available_time == null ? null : new Time(start_available_time.getTime());
        this.end_available_time = end_available_time == null ? null : new Time(end_available_time.getTime());
        this.day_off = day_off;
    }

    /**
     * Constructor used in the process of creating a new record.
     *
     * @param id_preset_availability id of the preset availability
     * @param day                    day of a record
     * @param day_off                day off
     * @param start_available_time   availability start time of a day
     * @param end_available_time     availability end time of a day
     */
    public Entity_PresetAvailability(Long id_preset_availability, WeekDays day, Boolean day_off, Time start_available_time, Time end_available_time) {
        this.id_preset_availability = id_preset_availability;
        this.day = day;
        this.start_available_time = start_available_time;
        this.end_available_time = end_available_time;
        this.day_off = day_off;
    }

    /**
     * Constructor used in creating test methods
     *
     * @param day                  day of a record
     * @param day_off              day off
     * @param start_available_time availability start time of a day
     * @param end_available_time   availability end time of a day
     */
    public Entity_PresetAvailability(WeekDays day, Time start_available_time, Time end_available_time, Boolean day_off) {
        this.day = day;
        this.start_available_time = start_available_time;
        this.end_available_time = end_available_time;
        this.day_off = day_off;
    }

    public Long getId_preset_availability() {
        return id_preset_availability;
    }

    public void setId_preset_availability(Long id_preset_availability) {
        this.id_preset_availability = id_preset_availability;
    }

    public Boolean getDay_off() {

        return day_off;
    }

    public void setDay_off(Boolean day_off) {
        this.day_off = day_off;
    }

    public Time getEnd_available_time() {
        return end_available_time;
    }

    public void setEnd_available_time(Time end_available_time) {
        this.end_available_time = end_available_time;
    }

    public Time getStart_available_time() {
        return start_available_time;
    }

    public void setStart_available_time(Time start_available_time) {
        this.start_available_time = start_available_time;
    }

    public WeekDays getDay() {
        return day;
    }

    public void setDay(WeekDays day) {
        this.day = day;
    }

    public Entity_EventPreset getEntity_EventPreset() {
        return entity_EventPreset;
    }

    public void setEntity_EventPreset(Entity_EventPreset entity_EventPreset) {
        this.entity_EventPreset = entity_EventPreset;
    }

    @Override
    public String toString() {
        return "Entity_PresetAvailability{" +
                "id_preset_availability=" + id_preset_availability +
                ", entity_EventPreset=" + entity_EventPreset +
                ", day=" + day +
                ", start_available_time=" + start_available_time +
                ", end_available_time=" + end_available_time +
                ", day_off=" + day_off +
                '}';
    }

    /**
     * Enum class containing the Weekdays which will be stored in the DB as an Enum String value.
     */
    public enum WeekDays {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }
}
