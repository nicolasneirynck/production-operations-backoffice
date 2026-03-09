package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import util.GebruikerStatus;
import util.Rollen;

import java.util.Optional;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "gebruikerId")
@Getter
@NamedQueries({
        @NamedQuery(name = "Gebruiker.findByEmail",
                query = """
                        SELECT g
                          FROM Gebruiker g
                          WHERE LOWER(g.email) = LOWER(:email)
						""")
})
public class Gebruiker {

    private static final EmailValidator VALIDATOR = EmailValidator.getInstance(false, false);

    // TODO: can gebruikerId be removed because we have personeelsnummer?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gebruikerId;
    @Column(unique=true)
    private int personeelsnummer;
    private String naam;
    private String voornaam;
    private String geboortedatum;
    private String adres;
    @Column(unique=true)
    private String email;
    @Column(nullable=true)
    private String gsm;
    @Enumerated(EnumType.STRING)
    private Rollen rol;
    @Enumerated(EnumType.STRING)
    private GebruikerStatus status;
    private String wachtwoord;

 //   private Site site;

//    @OneToOne
//    private Team verantwoordelijkeTeam;
//
//    @ManyToMany
//    private Team medewerkerTeam;

    public static Builder builder() {
        return new Builder();
    }

    private static void validate(int personeelsnummer, String naam, String voornaam, String geboortedatum, String adres, String email, String gsm, Rollen rol, GebruikerStatus status, String wachtwoord) {
        if (personeelsnummer < 0) throw new IllegalArgumentException("Personeelsnummer mag niet onder 0 zijn.");
        // TODO: check that there are no numbers
        if (naam == null || naam.isBlank()) throw new IllegalArgumentException("Naam is verplicht.");
        if (voornaam == null || voornaam.isBlank()) throw new IllegalArgumentException("Voornaam is verplicht.");
        // TODO: check format & make sure date is not in future and user is older than X years
        if (geboortedatum == null || geboortedatum.isBlank()) throw new IllegalArgumentException("Geboortedatum is verplicht.");
        // TODO: check format
        if (adres == null || adres.isBlank()) throw new IllegalArgumentException("Adres is verplicht.");
        if (email == null || email.isBlank() || !VALIDATOR.isValid(email.trim())) throw new IllegalArgumentException("Email is verplicht en moet een geldig formaat hebben.");

        // TODO: check format for gsm, null is allowed though

        if (rol == null) throw new IllegalArgumentException("Rol is verplicht.");
        if (status == null) throw new IllegalArgumentException("GebruikerStatus is verplicht.");
        if (wachtwoord == null || wachtwoord.isBlank()) throw new IllegalArgumentException("Wachtwoord is verplicht.");
    }

    public void update(int personeelsnummer, String naam, String voornaam, String geboortedatum, String adres, String email, String gsm, Rollen rol, GebruikerStatus status, String wachtwoord){
        validate(personeelsnummer, naam, voornaam, geboortedatum, adres, email, gsm, rol, status, wachtwoord);

        this.personeelsnummer = personeelsnummer;
        this.naam = naam;
        this.voornaam = voornaam;
        this.geboortedatum = geboortedatum;
        this.adres = adres;
        this.email = email;
        this.gsm = gsm;
        this.rol = rol;
        this.status = status;
        this.wachtwoord = wachtwoord;
    }

    private Gebruiker(Builder builder){
        this.personeelsnummer = builder.personeelsnummer;
        this.naam = builder.naam;
        this.voornaam = builder.voornaam;
        this.geboortedatum = builder.geboortedatum;
        this.adres = builder.adres;
        this.email = builder.email;
        this.gsm = builder.gsm;
        this.rol = builder.rol;
        this.status = builder.status;
        this.wachtwoord = builder.wachtwoord;
    }

    public static class Builder {
        private int personeelsnummer;
        private String naam;
        private String voornaam;
        private String geboortedatum;
        private String adres;
        private String email;
        private String gsm;
        private Rollen rol;
        private GebruikerStatus status;
        private String wachtwoord;

        public Builder personeelsnummer(int personeelsnummer) {
            this.personeelsnummer = personeelsnummer;
            return this;
        }

        public Builder naam(String naam) {
            this.naam = naam;
            return this;
        }

        public Builder voornaam(String voornaam) {
            this.voornaam = voornaam;
            return this;
        }

        public Builder geboortedatum(String geboortedatum) {
            this.geboortedatum = geboortedatum;
            return this;
        }

        public Builder adres(String adres) {
            this.adres = adres;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder gsm(String gsm) {
            this.gsm = gsm;
            return this;
        }

        public Builder rol(Rollen rol) {
            this.rol = rol;
            return this;
        }

        public Builder status(GebruikerStatus status) {
            this.status = status;
            return this;
        }

        public Builder wachtwoord(String wachtwoord) {
            this.wachtwoord = wachtwoord;
            return this;
        }

        public Gebruiker build() {
            validate(personeelsnummer, naam, voornaam, geboortedatum, adres, email, gsm, rol, status, wachtwoord);
            if (status == GebruikerStatus.INACTIEF) throw new IllegalArgumentException("GebruikerStatus is verplicht en mag niet beginnen als inactief.");

            return new Gebruiker(this);
        }
    }
}
