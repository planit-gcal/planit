package planit.people.preparation.Entities;

import javax.persistence.*;

@Entity
@Table(name = "google_account")
public class Entity_Google_account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_gen")
    @SequenceGenerator(name = "account_gen", sequenceName = "account_seq", allocationSize = 1)
    @Column(name = "google_account_id", nullable = false)
    private Long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planit_user_id", nullable = false)
    private Entity_User the_user;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refresh_token;

    public Entity_Google_account() {
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

    public Long getId() {
        return id;
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
