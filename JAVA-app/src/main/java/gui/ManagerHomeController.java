package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Setter;

public class ManagerHomeController {

    @FXML private Button teamsTile;
    @FXML private Button sitesTile;

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
    private void onSitesTile() {
        System.out.println("Sites beheren via tile");
        //layout.setContent("/gui/SitesOverviewContent.fxml");
    }
}