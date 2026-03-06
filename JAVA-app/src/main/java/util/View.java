package util;

public enum View {
    HOME("/gui/ManagerHomeContent.fxml", "Home"),

    TAKEN_OVERVIEW("/gui/taken/TaakOverviewContent.fxml", "Taken beheren"),
    TAKEN_FORM("/gui/taken/TaakFormContent.fxml","Taken beheren"),

    GEBRUIKER_OVERVIEW("/gui/gebruikers/GebruikerOverviewView.fxml","Gebruikers beheren"),
    GEBRUIKER_FORM("/gui/gebruikers/GebruikerFormView.fxml","Gebruikers beheren"),

    SITES_OVERVIEW("/gui/sites/SitesOverviewContent.fxml", "Sites Beheren"),
    SITES_FORM("/gui/sites/SiteFormContent.fxml","Sites beheren");

    public final String fxml;
    public final String title;

    View(String fxml, String title) {
        this.fxml = fxml;
        this.title = title;
    }
}
