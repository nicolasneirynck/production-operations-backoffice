import domein.TeamBeheerder;
import domein.entiteiten.Gebruiker;
import domein.entiteiten.Locatie;
import domein.entiteiten.Site;
import domein.entiteiten.Team;
import exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GebruikerDao;
import repository.GenericDao;
import repository.SiteDao;
import util.GebruikerStatus;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamBeheerderTest {

    @Mock
    private GenericDao<Team> teamRepo;

    @Mock
    private SiteDao siteRepo;

    @Mock
    private GebruikerDao gebruikerRepo;

    private TeamBeheerder teamBeheerder;

    @BeforeEach
    void setUp() {
        teamBeheerder = new TeamBeheerder(teamRepo, siteRepo, gebruikerRepo);
    }

    private Gebruiker maakGebruikerMetRol(Rollen rol, int personeelsnummer, String email) {
        return Gebruiker.builder()
                .personeelsnummer(personeelsnummer)
                .naam("Janssens")
                .voornaam("Jan" + personeelsnummer)
                .geboortedatum("2000-01-01")
                .adres("Teststraat 1, 9000 Gent")
                .email(email)
                .gsm("0470123456")
                .rol(rol)
                .status(GebruikerStatus.ACTIEF)
                .wachtwoord("Test123!")
                .build();
    }

    private Gebruiker werknemer(int personeelsnummer, String email) {
        return maakGebruikerMetRol(Rollen.WERKNEMER, personeelsnummer, email);
    }

    private Site geldigeSite() throws ValidationException {
        return Site.builder()
                .naam("SITE-A")
                .locatie(Locatie.builder("Straat", "1", "9000", "Gent", "België"))
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();
    }

    @Test
    public void getAllTeams_geeftAlleTeams() throws Exception {
        Site site = geldigeSite();
        Team team = new Team(site, List.of(
                werknemer(1, "w1@test.be"),
                werknemer(2, "w2@test.be"),
                werknemer(3, "w3@test.be")
        ));

        when(teamRepo.findAll()).thenReturn(List.of(team));

        List<Team> teams = teamBeheerder.getAllTeams();

        assertEquals(1, teams.size());
        assertEquals(site, teams.getFirst().getSite());
        assertEquals(3, teams.getFirst().getLeden().size());

        verify(teamRepo).findAll();
    }

    @Test
    public void addTeam_geldigeParameters_voegtTeamToe() throws Exception {
        long siteId = 1L;

        Site site = geldigeSite();
        Gebruiker w1 = werknemer(1, "w1@test.be");
        Gebruiker w2 = werknemer(2, "w2@test.be");
        Gebruiker w3 = werknemer(3, "w3@test.be");

        when(siteRepo.get(siteId)).thenReturn(site);
        when(gebruikerRepo.get(1L)).thenReturn(w1);
        when(gebruikerRepo.get(2L)).thenReturn(w2);
        when(gebruikerRepo.get(3L)).thenReturn(w3);

        teamBeheerder.addTeam(siteId, List.of(1L, 2L, 3L));

        verify(teamRepo).startTransaction();
        verify(siteRepo).get(siteId);
        verify(gebruikerRepo).get(1L);
        verify(gebruikerRepo).get(2L);
        verify(gebruikerRepo).get(3L);
        verify(teamRepo).insert(any(Team.class));
        verify(teamRepo).commitTransaction();
        verify(teamRepo, never()).rollbackTransaction();
    }

    @Test
    public void addTeam_onbestaandeSite_gooitException_enRollback() {
        long siteId = 99L;

        when(siteRepo.get(siteId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> teamBeheerder.addTeam(siteId, List.of(1L, 2L, 3L)));

        verify(teamRepo).startTransaction();
        verify(siteRepo).get(siteId);
        verify(teamRepo).rollbackTransaction();
        verify(teamRepo, never()).commitTransaction();
        verify(teamRepo, never()).insert(any());
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    public void addTeam_onbestaandeGebruiker_gooitException_enRollback() throws Exception {
        long siteId = 1L;
        Site site = geldigeSite();

        when(siteRepo.get(siteId)).thenReturn(site);
        when(gebruikerRepo.get(1L)).thenReturn(werknemer(1, "w1@test.be"));
        when(gebruikerRepo.get(2L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> teamBeheerder.addTeam(siteId, List.of(1L, 2L, 3L)));

        verify(teamRepo).startTransaction();
        verify(siteRepo).get(siteId);
        verify(gebruikerRepo).get(1L);
        verify(gebruikerRepo).get(2L);
        verify(teamRepo).rollbackTransaction();
        verify(teamRepo, never()).commitTransaction();
        verify(teamRepo, never()).insert(any());
    }

    @Test
    public void updateTeam_geldigeParameters_pastTeamAan() throws Exception {
        long teamCode = 1L;

        Site site = geldigeSite();
        Team team = new Team(site, List.of(
                werknemer(1, "w1@test.be"),
                werknemer(2, "w2@test.be"),
                werknemer(3, "w3@test.be")
        ));

        Gebruiker w1 = werknemer(1, "w1@test.be");
        Gebruiker w2 = werknemer(2, "w2@test.be");
        Gebruiker w4 = werknemer(4, "w4@test.be");

        when(teamRepo.get(teamCode)).thenReturn(team);
        when(gebruikerRepo.get(1L)).thenReturn(w1);
        when(gebruikerRepo.get(2L)).thenReturn(w2);
        when(gebruikerRepo.get(4L)).thenReturn(w4);

        teamBeheerder.updateTeam(teamCode, List.of(1L, 2L, 4L));

        verify(teamRepo).startTransaction();
        verify(teamRepo).get(teamCode);
        verify(gebruikerRepo).get(1L);
        verify(gebruikerRepo).get(2L);
        verify(gebruikerRepo).get(4L);
        verify(teamRepo).commitTransaction();
        verify(teamRepo, never()).rollbackTransaction();

        List<String> emails = team.getLeden().stream()
                .map(lid -> lid.getWerknemer().getEmail())
                .toList();

        assertEquals(3, emails.size());
        assertTrue(emails.containsAll(List.of("w1@test.be", "w2@test.be", "w4@test.be")));
        assertFalse(emails.contains("w3@test.be"));
    }

    @Test
    public void updateTeam_onbestaandTeam_gooitException_enRollback() {
        long teamCode = 99L;

        when(teamRepo.get(teamCode)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> teamBeheerder.updateTeam(teamCode, List.of(1L, 2L, 3L)));

        verify(teamRepo).startTransaction();
        verify(teamRepo).get(teamCode);
        verify(teamRepo).rollbackTransaction();
        verify(teamRepo, never()).commitTransaction();
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    public void updateTeam_onbestaandeGebruiker_gooitException_enRollback() throws Exception {
        long teamCode = 1L;

        Site site = geldigeSite();
        Team team = new Team(site, List.of(
                werknemer(1, "w1@test.be"),
                werknemer(2, "w2@test.be"),
                werknemer(3, "w3@test.be")
        ));

        when(teamRepo.get(teamCode)).thenReturn(team);
        when(gebruikerRepo.get(1L)).thenReturn(werknemer(1, "w1@test.be"));
        when(gebruikerRepo.get(2L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> teamBeheerder.updateTeam(teamCode, List.of(1L, 2L, 4L)));

        verify(teamRepo).startTransaction();
        verify(teamRepo).get(teamCode);
        verify(gebruikerRepo).get(1L);
        verify(gebruikerRepo).get(2L);
        verify(teamRepo).rollbackTransaction();
        verify(teamRepo, never()).commitTransaction();
    }

    @Test
    public void deleteTeam_bestaandTeam_verwijdertEnCommit() throws Exception {
        long teamCode = 1L;

        Site site = geldigeSite();
        Team team = new Team(site, List.of(
                werknemer(1, "w1@test.be"),
                werknemer(2, "w2@test.be"),
                werknemer(3, "w3@test.be")
        ));

        when(teamRepo.get(teamCode)).thenReturn(team);

        teamBeheerder.deleteTeam(teamCode);

        verify(teamRepo).startTransaction();
        verify(teamRepo).get(teamCode);
        verify(teamRepo).delete(team);
        verify(teamRepo).commitTransaction();
        verify(teamRepo, never()).rollbackTransaction();
    }

    @Test
    public void deleteTeam_onbestaandTeam_gooitException_enRollback() {
        long teamCode = 99L;

        when(teamRepo.get(teamCode)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> teamBeheerder.deleteTeam(teamCode));

        verify(teamRepo).startTransaction();
        verify(teamRepo).get(teamCode);
        verify(teamRepo).rollbackTransaction();
        verify(teamRepo, never()).commitTransaction();
        verify(teamRepo, never()).delete(any());
    }
}