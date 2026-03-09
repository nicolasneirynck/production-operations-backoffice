package main;

import domein.GebruikerController;
import domein.SiteController;
import domein.TaakController;
import domein.TeamController;
import gui.sites.ObservableSites;
import gui.taken.ObservableTaken;
import gui.teams.ObservableTeams;
import lombok.Getter;

@Getter
public class AppContext {

    private final TaakController taakController = new TaakController();
    private final SiteController siteController = new SiteController();
    private final TeamController teamController = new TeamController();
    private final GebruikerController gebruikerController = new GebruikerController();

    private final ObservableSites observableSites = new ObservableSites(siteController);
    private final ObservableTaken observableTaken = new ObservableTaken(taakController);
    private final ObservableTeams observableTeams = new ObservableTeams(teamController);

}
