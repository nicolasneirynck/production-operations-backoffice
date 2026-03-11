package domein;

import domein.entiteiten.Gebruiker;
import domein.entiteiten.Site;
import domein.entiteiten.Team;
import exception.ValidationException;
import repository.GenericDao;
import repository.GenericDaoJpa;

import java.util.List;

public class TeamBeheerder {

    private final GenericDao<Team> teamRepo;
    private final GenericDao<Site> siteRepo;
    private final GenericDao<Gebruiker> gebruikerRepo;

    // Mockito
    public TeamBeheerder(GenericDao<Team> teamRepo,
                         GenericDao<Site> siteRepo,
                         GenericDao<Gebruiker> gebruikerRepo) {
        this.teamRepo = teamRepo;
        this.siteRepo = siteRepo;
        this.gebruikerRepo = gebruikerRepo;
    }

    public TeamBeheerder() {
        this(
                new GenericDaoJpa<>(Team.class),
                new GenericDaoJpa<>(Site.class),
                new GenericDaoJpa<>(Gebruiker.class)
        );
    }

    public List<Team> getAllTeams() {
        return teamRepo.findAll();
    }

    public void addTeam(long siteId, List<Long> werknemerIds) throws ValidationException {
        teamRepo.startTransaction();
        try {
            Site site = siteRepo.get(siteId);
            if (site == null) {
                throw new IllegalArgumentException("Site niet gevonden.");
            }

            List<Gebruiker> werknemers = werknemerIds.stream()
                    .map(id -> {
                        Gebruiker gebruiker = gebruikerRepo.get(id);
                        if (gebruiker == null) {
                            throw new IllegalArgumentException("Gebruiker niet gevonden met id: " + id);
                        }
                        return gebruiker;
                    })
                    .toList();

            Team nieuwTeam = new Team(site, werknemers);
            teamRepo.insert(nieuwTeam);
            teamRepo.commitTransaction();

        } catch (RuntimeException | ValidationException ex) {
            teamRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void updateTeam(long teamCode, List<Long> werknemerIds) throws ValidationException {
        teamRepo.startTransaction();
        try {
            Team team = teamRepo.get(teamCode);
            if (team == null) {
                throw new IllegalArgumentException("Team niet gevonden.");
            }

            List<Gebruiker> werknemers = werknemerIds.stream()
                    .map(id -> {
                        Gebruiker gebruiker = gebruikerRepo.get(id);
                        if (gebruiker == null) {
                            throw new IllegalArgumentException("Gebruiker niet gevonden met id: " + id);
                        }
                        return gebruiker;
                    })
                    .toList();

            team.updateLeden(werknemers);
            teamRepo.commitTransaction();

        } catch (RuntimeException | ValidationException ex) {
            teamRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void deleteTeam(long teamCode) {
        teamRepo.startTransaction();
        try {
            Team team = teamRepo.get(teamCode);

            if (team == null) {
                throw new IllegalArgumentException("Team niet gevonden.");
            }

            teamRepo.delete(team);
            teamRepo.commitTransaction();

        } catch (RuntimeException ex) {
            teamRepo.rollbackTransaction();
            throw ex;
        }
    }
}