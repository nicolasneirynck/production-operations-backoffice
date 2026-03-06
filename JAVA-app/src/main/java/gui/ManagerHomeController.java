package gui;

import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Setter;
import main.AppContext;
import util.View;

public class ManagerHomeController implements NavigableController {

    @FXML private Button teamsTile;
    @FXML private Button sitesTile;


    @Setter private Navigator navigator;
    @Setter private AppContext context;

    @Setter
    private LayoutController layout;

    @FXML
    private void initialize() {
        // TODO
    }

    @FXML
    private void onTeamsTile() {
        System.out.println("Teams beheren via tile");
        //layout.setContent("/gui/TeamsOverviewContent.fxml");
    }

    @FXML
    public void onSitesTile() {
        navigator.goTo(View.SITES_OVERVIEW);
    }
}