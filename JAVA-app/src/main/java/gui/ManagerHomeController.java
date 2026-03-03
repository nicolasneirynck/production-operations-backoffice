package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Setter;

public class ManagerHomeController {

    @FXML private Button teamsTile;
    @FXML private Button sitesTile;
    @FXML private Button machinesTile;

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

    @FXML
    private void onMachinesTile() {
        System.out.println("Machines beheren via tile");
        //layout.setContent("/gui/MachinesOverviewContent.fxml");
    }
}