package planit.people.preparation.Entities;

import javax.persistence.*;

@Entity
@Table(name = "google_account")
public class Entity_GoogleAccount {
    /**
     * The primary key of the table. Auto Generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_gen")
    @SequenceGenerator(name = "account_gen", sequenceName = "account_seq", allocationSize = 1)
    @Column(name = "google_account_id", nullable = false)
    private Long id;
    /**
     * The email of the PlanIt User. The email is under a unique key constrain.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Foreign key references PlanIt User for each google account record
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planit_user_id", nullable = false)
    private Entity_User the_user;

    /**
     * the refresh token of a Google account. The refresh token is under a unique key constrain.
     * The refresh token is obtained from Google and used as credentials when communicating with Google APIs.
     */
    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refresh_token;

    /**
     * Empty constructor needed by Spring.
     */
    public Entity_GoogleAccount() {
    }

    /**
     * Constructor used in the process of creating a new record.
     *
     * @param email         email of a PlanIt User.
     * @param the_user      PlanIt User foreign key.
     * @param refresh_token Google's refresh token.
     */

    public Entity_GoogleAccount(String email, Entity_User the_user, String refresh_token) {
        this.email = email;
        this.the_user = the_user;
        this.refresh_token = refresh_token;
    }

    /**
     * Constructor used in retrieving record from the DB.
     *
     * @param id            google_account_id
     * @param email         email of a PlanIt User.
     * @param the_user      PlanIt User foreign key.
     * @param refresh_token Google's refresh token.
     */
    public Entity_GoogleAccount(Long id, String email, Entity_User the_user, String refresh_token) {
        this.id = id;
        this.email = email;
        this.the_user = the_user;
        this.refresh_token = refresh_token;
    }

    public Entity_GoogleAccount(Long id, Entity_User the_user) {
        this.id = id;
        this.the_user = the_user;
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

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Entity_GoogleAccount{" +
                "email=" + email +
                ", user='" + the_user + '\'' +
                ", refresh_token='" + refresh_token +
                '}';
    }

}
