package planit.people.preparation.Entities;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Composite_key implements Serializable {

    @ManyToOne
    @JoinColumn(name = "Meeting")
    private Entity_Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "Google_account")
    private Entity_Google_account account;

    public Composite_key(Entity_Meeting meeting, Entity_Google_account account) {
        this.account = account;
        this.meeting = meeting;
    }

    public Composite_key() {

    }

    public Entity_Google_account getAccount() {
        return account;
    }

    public Entity_Meeting getMeeting() {
        return meeting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composite_key that = (Composite_key) o;
        return meeting.equals(that.meeting) && account.equals(that.account);
    }
    @Override
    public int hashCode() {
        return Objects.hash(meeting, account);
    }
}
