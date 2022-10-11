package planit.people.preparation.Entities;

import javax.persistence.*;

@Entity
@Table(name = "Account_meeting")
public class Entity_Account_Meeting {

    @EmbeddedId
    private Composite_key composite_key;

    public Entity_Account_Meeting(Composite_key composite_key) {
        this.composite_key = composite_key;
    }

    public Entity_Account_Meeting() {
    }

    public Composite_key getComposite_key() {
        return composite_key;
    }

    @Override
    public String toString() {
        return "Entity_Account_Meeting{" +
                "composite_key=" + composite_key +
                '}';
    }
}
