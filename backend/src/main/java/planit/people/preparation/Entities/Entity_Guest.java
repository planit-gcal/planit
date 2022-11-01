package planit.people.preparation.Entities;

import javax.persistence.*;

@Entity
@Table(name = "preset_guest", uniqueConstraints = {
        @UniqueConstraint(name = "UniquePresetAndEmail", columnNames = {"id_event_preset", "email"})
})
public class Entity_Guest {
    /**
     * The primary key of the table. Auto Generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "preset_guests_gen")
    @SequenceGenerator(name = "preset_guests_gen", sequenceName = "preset_guests_seq", allocationSize = 1)
    @Column(name = "id_event_guest", nullable = false)
    private Long id_event_guest;

    /**
     * Foreign Key references the EventPreset for each Guest record.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_event_preset")
    private Entity_EventPreset entity_EventPreset;

    /**
     * defines the email of the guest. The Email and Entity_EventPreset is under a unique key constrain.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Defines whether the guest is obligated to attend an event or not.
     */
    @Column(name = "obligatory", nullable = false)
    private Boolean obligatory = false;

    /**
     * Constructor used in retrieving record from the DB.
     *
     * @param id_event_guest     Guest Id.
     * @param entity_EventPreset EventPreset foreign key
     * @param email              Guest Email.
     * @param obligatory         Guest's obligation to attend.
     */
    public Entity_Guest(Long id_event_guest, Entity_EventPreset entity_EventPreset, String email, Boolean obligatory) {
        this.id_event_guest = id_event_guest;
        this.entity_EventPreset = entity_EventPreset;
        this.email = email;
        this.obligatory = obligatory;
    }

    /**
     * Constructor used in the process of creating a new record.
     *
     * @param id_event_guest Guest Id.
     * @param email          Guest Email.
     * @param obligatory     Guest's obligation to attend.
     */
    public Entity_Guest(Long id_event_guest, String email, Boolean obligatory) {
        this.id_event_guest = id_event_guest;
        this.email = email;
        this.obligatory = obligatory;
    }

    /**
     * Constructor used in creating test methods
     *
     * @param email      Guest Email
     * @param obligatory Guest's obligation to attend.
     */
    public Entity_Guest(String email, Boolean obligatory) {
        this.email = email;
        this.obligatory = obligatory;
    }

    /**
     * Empty constructor needed by Spring.
     */
    public Entity_Guest() {

    }

    public Boolean getObligatory() {
        return obligatory;
    }

    public void setObligatory(Boolean obligatory) {
        this.obligatory = obligatory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Entity_EventPreset getEntity_EventPreset() {
        return entity_EventPreset;
    }

    public void setEntity_EventPreset(Entity_EventPreset entity_EventPreset) {
        this.entity_EventPreset = entity_EventPreset;
    }

    public Long getId_event_guest() {
        return id_event_guest;
    }

    public void setId_event_guest(Long id_event_guest) {
        this.id_event_guest = id_event_guest;
    }

    @Override
    public String toString() {
        return "Entity_Guest{" +
                "id_event_guest=" + id_event_guest +
                ", entity_EventPreset=" + entity_EventPreset +
                ", email='" + email + '\'' +
                ", obligatory=" + obligatory +
                '}';
    }
}
