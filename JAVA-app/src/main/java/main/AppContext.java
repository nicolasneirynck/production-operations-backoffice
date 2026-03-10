package main;

import domein.AuthenticationController;
import domein.GebruikerController;
import domein.controllers.SiteController;
import domein.controllers.TaakController;
import gui.sites.ObservableSites;
import gui.taken.ObservableTaken;
import gui.teams.ObservableTeams;
import lombok.Getter;

@Getter
public class AppContext {

    private final TaakController taakController = new TaakController();
    private final SiteController siteController = new SiteController();
    private final TeamController teamController = new TeamController();
    private final AuthenticationController authenticationController = new AuthenticationController();

    private final GebruikerController gebruikerController = new GebruikerController();

    private final ObservableSites observableSites = new ObservableSites(siteController);
    private final ObservableTaken observableTaken = new ObservableTaken(taakController);
    private final ObservableTeams observableTeams = new ObservableTeams(teamController);


    public TaakController getTaakController() { return taakController; }
    public SiteController getSiteController() { return siteController; }
    public AuthenticationController getAuthenticationController() { return authenticationController; }

    public GebruikerController getGebruikerController() { return gebruikerController; }

    public ObservableSites getObservableSites() { return observableSites; }
    public ObservableTaken getObservableTaken() { return observableTaken; }
}
