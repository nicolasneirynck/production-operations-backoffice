package domein.entiteiten;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import util.GebruikerStatus;
import util.Rollen;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "email")
@Getter
@NamedQueries({
        @NamedQuery(name = "Gebruiker.findByEmail",
                query = """
                        SELECT g
                          FROM Gebruiker g
                          WHERE LOWER(g.email) = LOWER(:email)
						""")
})
public class Gebruiker implements Comparable<Gebruiker> {

    private static final EmailValidator VALIDATOR = EmailValidator.getInstance(false, false);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gebruikerId;
    @Column(unique=true)
    private int personeelsnummer;
    private String naam;
    private String voornaam;
    private LocalDate geboortedatum;
    @Embedded
    private Locatie locatie;
    @Column(unique=true)
    private String email;
    @Column(nullable=true)
    private String gsm;
    @Enumerated(EnumType.STRING)
    private Rollen rol;
    @Enumerated(EnumType.STRING)
    private GebruikerStatus status;
    private String wachtwoord;

    public static Builder builder() {
        return new Builder();
    }

    private static void validate(int personeelsnummer, String naam, String voornaam, LocalDate geboortedatum,
                                 Locatie locatie, String email, String gsm, Rollen rol,
                                 GebruikerStatus status, String wachtwoord) {
        if (personeelsnummer <= 0) throw new IllegalArgumentException("Personeelsnummer moet groter zijn dan 0.");
        if (naam == null || naam.isBlank()) throw new IllegalArgumentException("Naam is verplicht.");
        if (voornaam == null || voornaam.isBlank()) throw new IllegalArgumentException("Voornaam is verplicht.");
        if (geboortedatum == null) throw new IllegalArgumentException("Geboortedatum is verplicht.");
        if (geboortedatum.isAfter(LocalDate.now())) throw new IllegalArgumentException("Geboortedatum mag niet in de toekomst liggen.");
        if (locatie == null) throw new IllegalArgumentException("Adres is verplicht.");
        if (email == null || email.isBlank() || !VALIDATOR.isValid(email.trim())) throw new IllegalArgumentException("Email is verplicht en moet een geldig formaat hebben.");
        if (rol == null) throw new IllegalArgumentException("Rol is verplicht.");
        if (status == null) throw new IllegalArgumentException("GebruikerStatus is verplicht.");
        if (wachtwoord == null || wachtwoord.isBlank()) throw new IllegalArgumentException("Wachtwoord is verplicht.");
    }

    public void update(String naam, String voornaam, LocalDate geboortedatum, Locatie locatie,
                       String email, String gsm, Rollen rol, GebruikerStatus status, String wachtwoord){
        validate(personeelsnummer, naam, voornaam, geboortedatum, locatie, email, gsm, rol, status, wachtwoord);

        this.naam = naam;
        this.voornaam = voornaam;
        this.geboortedatum = geboortedatum;
        this.locatie = locatie;
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
        this.locatie = builder.locatie;
        this.email = builder.email;
        this.gsm = builder.gsm;
        this.rol = builder.rol;
        this.status = builder.status;
        this.wachtwoord = builder.wachtwoord;
    }

    @Override
    public int compareTo(Gebruiker o) {

        int result = naam.compareToIgnoreCase(o.naam);
        return result != 0 ? result : voornaam.compareToIgnoreCase(o.voornaam);
    }

    public static class Builder {
        private int personeelsnummer;
        private String naam;
        private String voornaam;
        private LocalDate geboortedatum;
        private Locatie locatie;
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

        public Builder geboortedatum(LocalDate geboortedatum) {
            this.geboortedatum = geboortedatum;
            return this;
        }

        public Builder locatie(Locatie locatie) {
            this.locatie = locatie;
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
            validate(personeelsnummer, naam, voornaam, geboortedatum, locatie, email, gsm, rol, status, wachtwoord);
            if (status == GebruikerStatus.INACTIEF) throw new IllegalArgumentException("GebruikerStatus is verplicht en mag niet beginnen als inactief.");

            return new Gebruiker(this);
        }
    }
}
