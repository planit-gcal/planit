package planit.people.preparation.Entities;

import javax.persistence.*;

@Entity
@Table(name = "Google_account")
public class Entity_Google_account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_gen")
    @SequenceGenerator(name = "account_gen", sequenceName = "account_seq", allocationSize = 1)
    @Column(name = "Google_account_id", nullable = false)
    private Long id;
    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "The_User")
    private Entity_User the_user;

    @Column(name = "refresh_token")
    private String refresh_token;

    public Entity_Google_account() {

    }

    public Entity_Google_account(Long id, Entity_User the_user, String refresh_token) {
        this.id = id;
        this.the_user = the_user;
        this.refresh_token = refresh_token;
    }

    public Entity_Google_account(Entity_User the_user, String refresh_token) {
        this.the_user = the_user;
        this.refresh_token = refresh_token;
    }

    public Entity_Google_account(String email, Entity_User the_user, String refresh_token) {
        this.email = email;
        this.the_user = the_user;
        this.refresh_token = refresh_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public Entity_User getThe_user() {
        return the_user;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Entity_Google_account{" +
                "email=" + email +
                ", user='" + the_user + '\'' +
                ", refresh_token='" + refresh_token +
                '}';
    }

}
