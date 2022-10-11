package planit.people.preparation.Entities;

import javax.persistence.*;
import java.time.Duration;

@Entity
@Table(name = "Meeting")
public class Entity_Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meeting_gen")
    @SequenceGenerator(name = "meeting_gen", sequenceName = "meeting_seq", allocationSize = 1)
    @Column(name = "meeting_id", nullable = false)
    private Long meeting_id;

    @ManyToOne
    @JoinColumn(name = "The_User")
    private Entity_Google_account owner;

    @Column(name = "name")
    private String name;

    @Column(name="duration")
    private Duration duration;

    public Entity_Meeting() {

    }

    public Entity_Meeting(Entity_Google_account owner, String name, Duration duration) {
        this.owner = owner;
        this.name = name;
        this.duration = duration;
    }

    public Entity_Meeting(Long meeting_id, Entity_Google_account owner, String name, Duration duration) {
        this.meeting_id = meeting_id;
        this.owner = owner;
        this.name = name;
        this.duration = duration;
    }

    public Entity_Google_account getOwner(){return owner;}

    public String getName(){return name;}

    public Long getMeeting_id() {
        return meeting_id;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Entity_Meeting{" +
                "meeting_id=" + meeting_id +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", duration='" + duration +
                '}';
    }
}