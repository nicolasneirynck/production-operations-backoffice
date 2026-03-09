package domein;

import exception.TeamException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.Rollen;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
//@Setter(AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long code;

    // team hoort tot 1 site
    @OneToOne
    private Site site;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TeamLid> leden = new ArrayList<>();

    public Team(Site site, List<Gebruiker> leden) throws TeamException {

        validate(site, leden);

        this.site = site;

        for (Gebruiker gebruiker : leden) {
            this.leden.add(new TeamLid(this, gebruiker));
        }
    }

    public void updateLeden(List<Gebruiker> nieuweGebruikers) throws TeamException {
        validate(this.site, nieuweGebruikers);

        Set<Long> nieuweIds = nieuweGebruikers.stream()
                .map(Gebruiker::getGebruikerId)
                .collect(java.util.stream.Collectors.toSet());

        leden.removeIf(lid -> !nieuweIds.contains(lid.getWerknemer().getGebruikerId()));

        Set<Long> bestaandeIds = leden.stream()
                .map(lid -> lid.getWerknemer().getGebruikerId())
                .collect(Collectors.toSet());

        // voeg alleen nieuwe leden toe
        for (Gebruiker gebruiker : nieuweGebruikers) {
            if (!bestaandeIds.contains(gebruiker.getGebruikerId())) {
                leden.add(new TeamLid(this, gebruiker));
            }
        }
    }

    private static void validate(Site site, List<Gebruiker> leden) throws TeamException {
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
                    .map(Gebruiker::getGebruikerId)
                    .distinct()
                    .count();

            if (aantalUniek != aantalLeden) {
                errors.put("ledenDubbel", new IllegalArgumentException(
                        "Een werknemer mag maar één keer in hetzelfde team voorkomen."
                ));
            }
        }

        if (!errors.isEmpty()) {
            throw new TeamException(errors);
        }
    }

}
