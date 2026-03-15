package main;

import domein.services.AlleTeamsService;
import domein.services.GebruikerService;
import domein.services.LoginService;
import domein.services.MijnTeamService;
import domein.services.SiteService;
import domein.services.TaakService;
import domein.services.TeamService;
import gui.gebruikers.ObservableGebruikers;
import gui.sites.ObservableSites;
import gui.taken.ObservableTaken;
import gui.teams.ObservableTeam;
import gui.teams.ObservableTeams;
import lombok.Getter;

@Getter
public class AppContext {

    private final LoginService loginService = new LoginService();
    private final GebruikerService gebruikerService = new GebruikerService();
    private final TaakService taakService = new TaakService();
    private final SiteService siteService = new SiteService();
    private final TeamService teamService = new TeamService();
    private final AlleTeamsService alleTeamsService = teamService;
    private final MijnTeamService mijnTeamService = teamService;

    private final ObservableGebruikers observableGebruikers = new ObservableGebruikers();
    private final ObservableTaken observableTaken = new ObservableTaken();
    private final ObservableSites observableSites = new ObservableSites();
    private final ObservableTeams observableTeams = new ObservableTeams();
    private final ObservableTeam observableTeam = new ObservableTeam();


}
