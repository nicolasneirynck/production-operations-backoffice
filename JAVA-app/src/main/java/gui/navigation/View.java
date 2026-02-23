package gui.navigation;

public enum View {
    MAIN_MENU("/gui/MainMenuView.fxml", "Hoofdmenu"),
    TAKEN_OVERVIEW("/gui/TaakOverviewView.fxml", "Taken beheren"),
    TAKEN_FORM("/gui/TaakFormView.fxml","Taken beheren"),
    SITES_OVERVIEW("/gui/SiteOverviewView.fxml","Sites beheren"),
    SITES_FORM("/gui/SiteFormView.fxml","Sites beheren");

    public final String fxml;
    public final String title;

    View(String fxml, String title) {
        this.fxml = fxml;
        this.title = title;
    }
}
