package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import util.GebruikerStatus;
import util.Rollen;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "gebruikerId")
@Getter
public class Gebruiker {

    private static final EmailValidator VALIDATOR = EmailValidator.getInstance(false, false);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gebruikerId;
    @Column(unique=true)
    private String email;
    @Column(unique=true)
    private String gebruikersnaam;
    private String wachtwoord;
    @Enumerated(EnumType.STRING)
    private GebruikerStatus status;
    @Enumerated(EnumType.STRING)
    private Rollen rol;

 //   private Site site;

//    @OneToOne
//    private Team verantwoordelijkeTeam;
//
//    @ManyToMany
//    private Team medewerkerTeam;

    // enkel voor testing
    public Gebruiker(long id, Rollen rol) {
        this.gebruikerId = id;
        this.rol = rol;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static void validate(String email, String gebruikersnaam, String wachtwoord, GebruikerStatus status, Rollen rol) {
        if (email == null || email.isBlank() || !VALIDATOR.isValid(email.trim())) throw new IllegalArgumentException("Email is verplicht en moet een geldig formaat hebben.");
        if (gebruikersnaam == null || gebruikersnaam.isBlank()) throw new IllegalArgumentException("Gebruikersnaam is verplicht.");
        if (wachtwoord == null || wachtwoord.isBlank()) throw new IllegalArgumentException("Wachtwoord is verplicht.");
        if (status == null) throw new IllegalArgumentException("GebruikerStatus is verplicht.");
        if (rol == null) throw new IllegalArgumentException("Rol is verplicht.");
    }

    public void update(String email, String gebruikersnaam, String wachtwoord, GebruikerStatus status, Rollen rol){
        validate(email, gebruikersnaam, wachtwoord, status, rol);

        this.email = email;
        this.gebruikersnaam = gebruikersnaam;
        this.wachtwoord = wachtwoord;
        this.status = status;
        this.rol = rol;
    }

    private Gebruiker(Builder builder){
        this.email = builder.email;
        this.gebruikersnaam = builder.gebruikersnaam;
        this.wachtwoord = builder.wachtwoord;
        this.status = builder.status;
        this.rol = builder.rol;
    }

    public static class Builder {
        private String email;
        private String gebruikersnaam;
        private String wachtwoord;
        private GebruikerStatus status;
        private Rollen rol;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder gebruikersnaam(String gebruikersnaam) {
            this.gebruikersnaam = gebruikersnaam;
            return this;
        }

        public Builder wachtwoord(String wachtwoord) {
            this.wachtwoord = wachtwoord;
            return this;
        }

        public Builder status(GebruikerStatus status) {
            this.status = status;
            return this;
        }

        public Builder rol(Rollen rol) {
            this.rol = rol;
            return this;
        }

        public Gebruiker build() {
            validate(email, gebruikersnaam, wachtwoord, status, rol);
            if (status == GebruikerStatus.VERWIJDERD) throw new IllegalArgumentException("GebruikerStatus is verplicht en mag niet beginnen als verwijderd.");

            return new Gebruiker(this);
        }
    }
}
