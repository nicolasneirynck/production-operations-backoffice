package util;

import security.Permission;

public enum View {
    HOME("/gui/HomeContent.fxml", "Home"),
    LOGIN("/gui/LoginView.fxml", "Login"),

    TAKEN_OVERVIEW("/gui/taken/TaakOverviewContent.fxml", "Taken beheren"),
    TAKEN_FORM("/gui/taken/TaakFormContent.fxml","Taken beheren"),

    ALL_TEAMS_OVERVIEW("/gui/teams/AllTeamsOverviewContent.fxml", "Teams beheren"),
    MIJN_TEAM_OVERVIEW("/gui/teams/MijnTeamOverviewContent.fxml", "Mijn team"),
    TEAMS_FORM("/gui/teams/TeamFormContent.fxml", "Teams beheren"),

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
