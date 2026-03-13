package domein.entiteiten;

import exception.ValidationException;
import jakarta.persistence.*;
import lombok.*;
import util.Rollen;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "code")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private final String code = UUID.randomUUID().toString();
 
    // team hoort tot 1 site
    @OneToOne
    private Site site;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TeamLid> leden = new ArrayList<>();

    public Team(Site site, List<Gebruiker> leden) throws ValidationException {

        validate(site, leden);
        this.site = site;
        voegLedenToe(leden);
    }

    public List<Gebruiker> getWerknemers() {
        return leden.stream()
                .map(TeamLid::getWerknemer)
                .toList();
    }

    public void updateLeden(List<Gebruiker> nieuweGebruikers) throws ValidationException {
        validate(this.site, nieuweGebruikers);

        Set<Integer> nieuweIds = nieuweGebruikers.stream()
                .map(Gebruiker::getPersoneelsnummer)
                .collect(Collectors.toSet());

        leden.removeIf(lid -> !nieuweIds.contains(lid.getWerknemer().getPersoneelsnummer()));

        Set<Integer> bestaandeIds = leden.stream()
                .map(lid -> lid.getWerknemer().getPersoneelsnummer())
                .collect(Collectors.toSet());

        for (Gebruiker gebruiker : nieuweGebruikers) {
            if (!bestaandeIds.contains(gebruiker.getPersoneelsnummer())) {
                leden.add(new TeamLid(this, gebruiker));
            }
        }
    }

    private void voegLedenToe(List<Gebruiker> gebruikers) {
        for (Gebruiker gebruiker : gebruikers) {
            leden.add(new TeamLid(this, gebruiker));
        }
    }

    private static void validate(Site site, List<Gebruiker> leden) throws ValidationException {
        Map<String, IllegalArgumentException> errors = new HashMap<>();

        if (site == null) {
            errors.put("site", new IllegalArgumentException("Site is vereist."));
        }

        if (leden == null || leden.isEmpty()) {
            errors.put("leden", new IllegalArgumentException("Een team moet werknemers bevatten."));
        } else {
            if (leden.size() < 3) {
                errors.put("leden", new IllegalArgumentException("Een team moet minstens 3 werknemers hebben."));
            }

            if (leden.stream().anyMatch(g -> g.getRol() != Rollen.WERKNEMER)) {
                errors.put("ledenRol", new IllegalArgumentException("Alle teamleden moeten werknemers zijn."));
            }

            long aantalLeden = leden.size();

            long aantalUniek = leden.stream()
                    .filter(Objects::nonNull)
                    .map(Gebruiker::getPersoneelsnummer)
                    .distinct()
                    .count();

            if (aantalUniek != aantalLeden) {
                errors.put("ledenDubbel", new IllegalArgumentException(
                        "Een werknemer mag maar één keer in hetzelfde team voorkomen."
                ));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}
