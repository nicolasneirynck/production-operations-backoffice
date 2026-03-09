import domein.*;
import dto.TeamDTO;
import exception.SiteException;
import exception.TeamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GenericDao;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamControllerTest {

    @Mock
    private GenericDao<Team> teamRepo;

    @Mock
    private GenericDao<Site> siteRepo;

    @Mock
    private GenericDao<Gebruiker> gebruikerRepo;

    private TeamController teamController;

    @BeforeEach
    void setUp() {
        teamController = new TeamController(teamRepo, siteRepo, gebruikerRepo);
    }

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
        return new Gebruiker(id, Rollen.WERKNEMER);
    }

    @Test
    public void getAllTeams_geeftAlleTeams() throws Exception {
        Site site = geldigeSite();
        Team team = new Team(1L,site, List.of(
                werknemer(1L),
                werknemer(2L),
                werknemer(3L)
        ));

        when(teamRepo.findAll()).thenReturn(List.of(team));

        List<TeamDTO> teams = teamController.getAllTeams();

        assertEquals(1, teams.size());
        assertEquals(1L, teams.getFirst().teamCode());
        assertEquals(3, teams.getFirst().teamleden().size());

        verify(teamRepo).findAll();
    }

    @Test
    public void addTeam_geldigeParameters_voegtTeamToe() throws Exception {
        long siteId = 1L;

        Site site = geldigeSite();
        Gebruiker w1 = werknemer(1L);
        Gebruiker w2 = werknemer(2L);
        Gebruiker w3 = werknemer(3L);

        when(siteRepo.get(siteId)).thenReturn(site);
        when(gebruikerRepo.get(1L)).thenReturn(w1);
        when(gebruikerRepo.get(2L)).thenReturn(w2);
        when(gebruikerRepo.get(3L)).thenReturn(w3);

        teamController.addTeam(siteId, List.of(1L, 2L, 3L));

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
                () -> teamController.addTeam(siteId, List.of(1L, 2L, 3L)));

        verify(teamRepo).startTransaction();
        verify(siteRepo).get(siteId);
        verify(teamRepo).rollbackTransaction();
        verify(teamRepo, never()).commitTransaction();
        verify(teamRepo, never()).insert(any());
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    public void updateTeam_geldigeParameters_pastTeamAan() throws Exception {
        long teamCode = 1L;

        Site site = geldigeSite();
        Team team = new Team(1L,site, List.of(
                werknemer(1L),
                werknemer(2L),
                werknemer(3L)
        ));

        Gebruiker w1 = werknemer(1L);
        Gebruiker w2 = werknemer(2L);
        Gebruiker w4 = werknemer(4L);

        when(teamRepo.get(teamCode)).thenReturn(team);
        when(gebruikerRepo.get(1L)).thenReturn(w1);
        when(gebruikerRepo.get(2L)).thenReturn(w2);
        when(gebruikerRepo.get(4L)).thenReturn(w4);

        teamController.updateTeam(teamCode, List.of(1L, 2L, 4L));

        verify(teamRepo).startTransaction();
        verify(teamRepo).get(teamCode);
        verify(gebruikerRepo).get(1L);
        verify(gebruikerRepo).get(2L);
        verify(gebruikerRepo).get(4L);
        verify(teamRepo).commitTransaction();
        verify(teamRepo, never()).rollbackTransaction();

        List<Long> ids = team.getLeden().stream()
                .map(lid -> lid.getWerknemer().getGebruikerId())
                .toList();

        assertEquals(3, ids.size());
        assertTrue(ids.containsAll(List.of(1L, 2L, 4L)));
        assertFalse(ids.contains(3L));
    }

    @Test
    public void updateTeam_onbestaandTeam_gooitException_enRollback() {
        long teamCode = 99L;

        when(teamRepo.get(teamCode)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> teamController.updateTeam(teamCode, List.of(1L, 2L, 3L)));

        verify(teamRepo).startTransaction();
        verify(teamRepo).get(teamCode);
        verify(teamRepo).rollbackTransaction();
        verify(teamRepo, never()).commitTransaction();
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    public void deleteTeam_bestaandTeam_verwijdertEnCommit() throws Exception {
        long teamCode = 1L;

        Site site = geldigeSite();
        Team team = new Team(1L,site, List.of(
                werknemer(1L),
                werknemer(2L),
                werknemer(3L)
        ));

        when(teamRepo.get(teamCode)).thenReturn(team);

        teamController.deleteTeam(teamCode);

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
                () -> teamController.deleteTeam(teamCode));

        verify(teamRepo).startTransaction();
        verify(teamRepo).get(teamCode);
        verify(teamRepo).rollbackTransaction();
        verify(teamRepo, never()).commitTransaction();
        verify(teamRepo, never()).delete(any());
    }
}