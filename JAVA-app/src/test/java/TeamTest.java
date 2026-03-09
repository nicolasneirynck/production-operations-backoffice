import domein.*;
import exception.TeamException;
import exception.SiteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TeamTest {

    private Site geldigeSite() throws SiteException {
        return Site.builder()
                .naam("SITE-A")
                .locatie(Locatie.builder("Straat", "1", "9000", "Gent", "België"))
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();
    }

    private Gebruiker werknemer(long id) {
        Gebruiker g = new Gebruiker(id,Rollen.WERKNEMER);
        return g;
    }

    static Stream<Arguments> geldigeTeams() {
        return Stream.of(
                Arguments.of(List.of(1L, 2L, 3L)),
                Arguments.of(List.of(10L, 20L, 30L, 40L))
        );
    }

    @ParameterizedTest
    @MethodSource("geldigeTeams")
    void constructor_GeldigeParameters_GeenException(List<Long> ids) throws Exception {

        Site site = geldigeSite();

        List<Gebruiker> leden = ids.stream()
                .map(this::werknemer)
                .toList();

        Team team = new Team(site, leden);

        assertEquals(site, team.getSite());
        assertEquals(ids.size(), team.getLeden().size());
    }

    @Test
    void constructor_NullSite_GooitException() {

        List<Gebruiker> leden = List.of(
                werknemer(1),
                werknemer(2),
                werknemer(3)
        );

        assertThrows(TeamException.class, () -> new Team(null, leden));
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2})
    void constructor_MinderDanDrieLeden_GooitException(int aantal) throws Exception {

        Site site = geldigeSite();

        List<Gebruiker> leden = java.util.stream.LongStream.rangeClosed(1, aantal)
                .mapToObj(this::werknemer)
                .toList();

        assertThrows(TeamException.class, () -> new Team(site, leden));
    }

    @Test
    void constructor_DubbeleWerknemers_GooitException() throws Exception {

        Site site = geldigeSite();

        Gebruiker w1 = werknemer(1);
        Gebruiker w2 = werknemer(2);
        Gebruiker w1Dubbel = werknemer(1);

        List<Gebruiker> leden = List.of(w1, w2, w1Dubbel);

        assertThrows(TeamException.class, () -> new Team(site, leden));
    }

    @Test
    void updateLeden_GeldigeNieuweLijst_PastTeamAan() throws Exception {

        Site site = geldigeSite();

        Gebruiker w1 = werknemer(1);
        Gebruiker w2 = werknemer(2);
        Gebruiker w3 = werknemer(3);

        Team team = new Team(site, List.of(w1, w2, w3));

        Gebruiker w4 = werknemer(4);

        team.updateLeden(List.of(w1, w3, w4));

        assertEquals(3, team.getLeden().size());

        List<Long> ids = team.getLeden().stream()
                .map(l -> l.getWerknemer().getGebruikerId())
                .toList();

        assertTrue(ids.containsAll(List.of(1L,3L,4L)));
        assertFalse(ids.contains(2L));
    }
}