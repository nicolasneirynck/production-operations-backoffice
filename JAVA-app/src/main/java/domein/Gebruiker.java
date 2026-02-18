package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.GebruikerStatus;
import util.Rollen;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "gebruikerId")
@Getter
public class Gebruiker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gebruikerId;
    private String email;
    private String gebruikersnaam;
    private String wachtwoord;
    @Enumerated(EnumType.STRING)
    private GebruikerStatus status;
    @Enumerated(EnumType.STRING)
    private Rollen rol;

    public static Builder builder() {
        return new Builder();
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
            if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is verplicht.");
            if (gebruikersnaam == null || gebruikersnaam.isBlank()) throw new IllegalArgumentException("Gebruikersnaam is verplicht.");
            if (wachtwoord == null || wachtwoord.isBlank()) throw new IllegalArgumentException("Wachtwoord is verplicht.");
            if (status == null || status == GebruikerStatus.VERWIJDERD) throw new IllegalArgumentException("GebruikerStatus is verplicht en mag niet beginnen als verwijderd.");
            if (rol == null) throw new IllegalArgumentException("Rol is verplicht.");

            return new Gebruiker(this);
        }
    }
}
