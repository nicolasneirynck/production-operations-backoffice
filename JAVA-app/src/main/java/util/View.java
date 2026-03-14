package util;

import security.Permission;

public enum View {
    HOME("/gui/HomeContent.fxml", "Home", null),
    LOGIN("/gui/LoginView.fxml", "Login", null),

    GEBRUIKER_OVERVIEW("/gui/gebruikers/GebruikerOverviewView.fxml", "Gebruikers beheren", Permission.GEBRUIKERS_BEHEREN),
    GEBRUIKER_FORM("/gui/gebruikers/GebruikerFormView.fxml", "Gebruikers beheren", Permission.GEBRUIKERS_BEHEREN),
    SITES_OVERVIEW("/gui/sites/SitesOverviewContent.fxml", "Sites beheren", Permission.SITES_BEHEREN),
    SITES_FORM("/gui/sites/SiteFormContent.fxml", "Sites beheren", Permission.SITES_BEHEREN),
    ALL_TEAMS_OVERVIEW("/gui/teams/AllTeamsOverviewContent.fxml", "Teams beheren", Permission.ALLE_TEAMS_BEHEREN),
    MIJN_TEAM_OVERVIEW("/gui/teams/MijnTeamOverviewContent.fxml", "Mijn team beheren", Permission.MIJN_TEAM_BEHEREN),
    MANAGER_TEAMS_FORM("/gui/teams/ManagerTeamFormContent.fxml", "Teams beheren", Permission.ALLE_TEAMS_BEHEREN),
    VERANTWOORDELIJKE_TEAMS_FORM("/gui/teams/MijnTeamFormContent.fxml", "Mijn team beheren", Permission.MIJN_TEAM_BEHEREN),
    TAKEN_OVERVIEW("/gui/taken/TaakOverviewContent.fxml", "Taken beheren", Permission.TAKEN_BEHEREN),
    TAKEN_FORM("/gui/taken/TaakFormContent.fxml", "Taken beheren", Permission.TAKEN_BEHEREN);

    public final String fxml;
    public final String title;
    public final Permission requiredPermission;

    View(String fxml, String title, Permission requiredPermission) {
        this.fxml = fxml;
        this.title = title;
        this.requiredPermission = requiredPermission;
    }
}
