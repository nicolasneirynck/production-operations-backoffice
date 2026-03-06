package main;

import domein.AuthenticationController;
import domein.GebruikerController;
import domein.SiteController;
import domein.TaakController;
import repository.GebruikerDao;
import repository.GebruikerDaoJpa;
import gui.ObservableSites;
import gui.ObservableTaken;

public class AppContext {
    private final TaakController taakController = new TaakController();
    private final SiteController siteController = new SiteController();
    private final AuthenticationController authenticationController = new AuthenticationController();

    private final GebruikerDao gebruikerRepo = new GebruikerDaoJpa();
    private final GebruikerController gebruikerController = new GebruikerController(gebruikerRepo);

    private final ObservableSites observableSites = new ObservableSites(siteController);
    private final ObservableTaken observableTaken = new ObservableTaken(taakController);


    public TaakController getTaakController() { return taakController; }
    public SiteController getSiteController() { return siteController; }
    public AuthenticationController getAuthenticationController() { return authenticationController; }

    public GebruikerDao getGebruikerRepo() { return gebruikerRepo; }
    public GebruikerController getGebruikerController() { return gebruikerController; }

    public ObservableSites getObservableSites() { return observableSites; }
    public ObservableTaken getObservableTaken() { return observableTaken; }
}
