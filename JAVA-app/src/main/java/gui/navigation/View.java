package gui.navigation;

public enum View {
    MAIN_MENU("/gui/MainMenuView.fxml", "Hoofdmenu"),
    TAKEN_OVERVIEW("/gui/TaakOverviewContent.fxml", "Taken beheren"),
    TAKEN_FORM("/gui/oldTaakFormView.fxml","Taken beheren"),
    GEBRUIKER_OVERVIEW("/gui/GebruikerOverviewView.fxml","Gebruikers beheren"),
    GEBRUIKER_FORM("/gui/GebruikerFormView.fxml","Gebruikers beheren"),
    HOME("/gui/ManagerHomeContent.fxml", "Home"),
    SITES_OVERVIEW("/gui/SitesOverviewContent.fxml", "Sites Beheren"),
    SITES_FORM("/gui/SiteFormContent.fxml","Sites beheren");

    public final String fxml;
    public final String title;

    View(String fxml, String title) {
        this.fxml = fxml;
        this.title = title;
    }
}
