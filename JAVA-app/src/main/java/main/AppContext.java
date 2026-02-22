package main;

import domein.SiteController;
import domein.TaakController;
import gui.ObservableSites;
import gui.ObservableTaken;

public class AppContext {
    private final TaakController taakController = new TaakController();
    private final SiteController siteController = new SiteController();

    private final ObservableSites observableSites = new ObservableSites(siteController);
    private final ObservableTaken observableTaken = new ObservableTaken(taakController);


    public TaakController getTaakController() { return taakController; }
    public SiteController getSiteController() { return siteController; }

    public ObservableSites getObservableSites() { return observableSites; }
    public ObservableTaken getObservableTaken() { return observableTaken; }
}
