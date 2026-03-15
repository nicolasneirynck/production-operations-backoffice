package domein.entiteiten;

import jakarta.persistence.*;
import lombok.*;
import util.Rollen;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "businessKey")
public class TeamLid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private final String businessKey = UUID.randomUUID().toString(); // tijdelijk om hash en equals te kunnen doen

    @ManyToOne
    private Team team;

    @ManyToOne
    private Gebruiker werknemer;

    public TeamLid(Team team, Gebruiker werknemer) {
        if (team == null) {
            throw new IllegalArgumentException("Team is verplicht.");
        }
        if (werknemer == null) {
            throw new IllegalArgumentException("Gebruiker is verplicht.");
        }
        if (werknemer.getRol() != Rollen.WERKNEMER) {
            throw new IllegalArgumentException("Alleen een medewerker kan teamlid zijn.");
        }

        this.team = team;
        this.werknemer = werknemer;
    }
}
