package main;

import domein.SiteController;
import domein.TaakController;

public class AppContext {
    private final TaakController taakController = new TaakController();
    private final SiteController siteController = new SiteController();

    public TaakController getTaakController() { return taakController; }
    public SiteController getSiteController() { return siteController; }
}
