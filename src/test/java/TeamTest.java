import domein.entiteiten.Gebruiker;
import domein.entiteiten.Locatie;
import domein.entiteiten.Site;
import domein.entiteiten.Team;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import util.GebruikerStatus;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TeamTest {

    private Site geldigeSite() throws ValidationException {
        return Site.builder()
                .naam("SITE-A")
                .locatie(Locatie.builder("Straat", "1", "9000", "Gent", "België"))
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();
    }

    private Gebruiker werknemer(int personeelsnummer, String email) {
        try {
            return Gebruiker.builder()
                    .personeelsnummer(personeelsnummer)
                    .naam("Janssens")
                    .voornaam("Jan" + personeelsnummer)
                    .geboortedatum(LocalDate.parse("2000-01-01"))
                    .locatie(Locatie.builder("Teststraat", "1", "9000", "Gent", "België"))
                    .email(email)
                    .gsm("0470123456")
                    .rol(Rollen.WERKNEMER)
                    .status(GebruikerStatus.ACTIEF)
                    .wachtwoord("geheim123")
                    .build();
        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        }
    }

    static Stream<Arguments> geldigeTeams() {
        return Stream.of(
                Arguments.of(List.of(
                        new WerknemerGegevens(1, "w1@test.be"),
                        new WerknemerGegevens(2, "w2@test.be"),
                        new WerknemerGegevens(3, "w3@test.be")
                )),
                Arguments.of(List.of(
                        new WerknemerGegevens(10, "w10@test.be"),
                        new WerknemerGegevens(20, "w20@test.be"),
                        new WerknemerGegevens(30, "w30@test.be"),
                        new WerknemerGegevens(40, "w40@test.be")
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("geldigeTeams")
    void constructor_GeldigeParameters_GeenException(List<WerknemerGegevens> gegevens) throws Exception {
        Site site = geldigeSite();

        List<Gebruiker> leden = gegevens.stream()
                .map(g -> werknemer(g.personeelsnummer(), g.email()))
                .toList();

        Team team = new Team(site, leden);

        assertEquals(site, team.getSite());
        assertEquals(gegevens.size(), team.getWerknemers().size());
    }

    @Test
    void constructor_NullSite_GooitException() {
        List<Gebruiker> leden = List.of(
                werknemer(1, "w1@test.be"),
                werknemer(2, "w2@test.be"),
                werknemer(3, "w3@test.be")
        );

        assertThrows(ValidationException.class, () -> new Team(null, leden));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void constructor_MinderDanDrieLeden_GooitException(int aantal) throws Exception {
        Site site = geldigeSite();

        List<Gebruiker> leden = java.util.stream.IntStream.rangeClosed(1, aantal)
                .mapToObj(i -> werknemer(i, "w" + i + "@test.be"))
                .toList();

        assertThrows(ValidationException.class, () -> new Team(site, leden));
    }

    @Test
    void constructor_DubbeleWerknemers_GooitException() throws Exception {
        Site site = geldigeSite();

        Gebruiker w1 = werknemer(1, "w1@test.be");
        Gebruiker w2 = werknemer(2, "w2@test.be");

        // Dubbel op basis van hetzelfde personeelsnummer, want dat is nu de business-identiteit in Team
        Gebruiker w1Dubbel = werknemer(1, "ander-email@test.be");

        List<Gebruiker> leden = List.of(w1, w2, w1Dubbel);

        assertThrows(ValidationException.class, () -> new Team(site, leden));
    }

    @Test
    void updateLeden_GeldigeNieuweLijst_PastTeamAan() throws Exception {
        Site site = geldigeSite();

        Gebruiker w1 = werknemer(1, "w1@test.be");
        Gebruiker w2 = werknemer(2, "w2@test.be");
        Gebruiker w3 = werknemer(3, "w3@test.be");

        Team team = new Team(site, List.of(w1, w2, w3));

        Gebruiker w4 = werknemer(4, "w4@test.be");

        team.updateLeden(List.of(w1, w3, w4));

        assertEquals(3, team.getWerknemers().size());

        List<String> emails = team.getWerknemers().stream()
                .map(Gebruiker::getEmail)
                .toList();


        assertTrue(emails.containsAll(List.of("w1@test.be", "w3@test.be", "w4@test.be")));
        assertFalse(emails.contains("w2@test.be"));
    }

    private record WerknemerGegevens(int personeelsnummer, String email) {
    }
}
