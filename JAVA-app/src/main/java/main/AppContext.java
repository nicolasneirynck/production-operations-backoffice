package main;

import domein.AuthenticationController;
import domein.GebruikerController;
import domein.controllers.SiteController;
import domein.controllers.TaakController;
import gui.sites.ObservableSites;
import gui.taken.ObservableTaken;

public class AppContext {
    private final TaakController taakController = new TaakController();
    private final SiteController siteController = new SiteController();
    private final AuthenticationController authenticationController = new AuthenticationController();

    private final GebruikerController gebruikerController = new GebruikerController();

    private final ObservableSites observableSites = new ObservableSites(siteController);
    private final ObservableTaken observableTaken = new ObservableTaken(taakController);


    public TaakController getTaakController() { return taakController; }
    public SiteController getSiteController() { return siteController; }
    public AuthenticationController getAuthenticationController() { return authenticationController; }

    public GebruikerController getGebruikerController() { return gebruikerController; }

    public ObservableSites getObservableSites() { return observableSites; }
    public ObservableTaken getObservableTaken() { return observableTaken; }
}
