package main;

import domein.AuthenticationController;
import domein.GebruikerController;
import domein.controllers.AlleTeamsService;
import domein.controllers.MijnTeamService;
import domein.controllers.TeamService;
import domein.controllers.SiteController;
import domein.controllers.TaakController;
import gui.sites.ObservableSites;
import gui.taken.ObservableTaken;
import gui.teams.ObservableTeam;
import gui.teams.ObservableTeams;
import lombok.Getter;

@Getter
public class AppContext {

    private final AuthenticationController authenticationController = new AuthenticationController();
    private final TaakController taakController = new TaakController();
    private final SiteController siteController = new SiteController();
    private final GebruikerController gebruikerController = new GebruikerController();

    //private final TeamController teamController = new TeamController();
    private final TeamService teamService = new TeamService();
    private final AlleTeamsService alleTeamsService = teamService;
    private final MijnTeamService mijnTeamService = teamService;

    private final ObservableTaken observableTaken = new ObservableTaken(taakController);
    private final ObservableSites observableSites = new ObservableSites(siteController);
    private final ObservableTeams observableTeams = new ObservableTeams();
    private final ObservableTeam observableTeam = new ObservableTeam();


}
