package gui.navigation;

public enum View {
    MAIN_MENU("/gui/MainMenuView.fxml", "Hoofdmenu"),
    TAKEN_OVERVIEW("/gui/TaakOverviewView.fxml", "Taken beheren"),
    TAKEN_FORM("/gui/TaakFormView.fxml","Taken beheren"),
    //SITES_OVERVIEW("/gui/SiteOverviewView.fxml","Sites beheren"),
    //SITES_FORM("/gui/SiteFormView.fxml","Sites beheren"),
    GEBRUIKER_OVERVIEW("/gui/GebruikerOverviewView.fxml","Gebruikers beheren"),
    GEBRUIKER_FORM("/gui/GebruikerFormView.fxml","Gebruikers beheren"),
    HOME("/gui/ManagerHomeContent.fxml", "Home"),
    SITES_OVERVIEW("/gui/SitesOverviewContent.fxml", "Sites Beheren"),
    SITE_FORM("/gui/SiteFormContent.fxml", "Site aanmaken"),
    LOGIN("/gui/LoginView.fxml", "Login");

    public final String fxml;
    public final String title;

    View(String fxml, String title) {
        this.fxml = fxml;
        this.title = title;
    }
}
