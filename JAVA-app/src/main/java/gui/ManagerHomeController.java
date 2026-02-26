package gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class ManagerHomeController {

    @FXML private Button homeBtn;
    @FXML private Button teamsBtn;
    @FXML private Button sitesBtn;
    @FXML private Button machinesBtn;
    @FXML private Button logoutBtn;

    @FXML private Button teamsTile;
    @FXML private Button sitesTile;
    @FXML private Button machinesTile;

    @FXML private StackPane logoHeader;
    @FXML private ImageView logoImage;

    @FXML
    private void initialize() {
        // Dit wordt automatisch aangeroepen na FXML loading
        System.out.println("ManagerHomeController initialized");

        // Belangrijk: als je vroeger viewport gebruikte, reset die!
        logoImage.setViewport(null);

        // Grootte bepalen
        logoImage.setPreserveRatio(true);
        logoImage.setSmooth(true);

        // Kies 1 van deze 2:
        logoImage.setFitHeight(44);   // meestal best voor header van 56px
        //logoImage.setFitWidth(220); // alternatief als je vooral breedte wil sturen

    }

    // ===== Sidebar Actions =====

    @FXML
    private void onHome() {
        System.out.println("Home clicked");
    }

    @FXML
    private void onTeams() {
        System.out.println("Teams beheren via sidebar");
    }

    @FXML
    private void onSites() {
        System.out.println("Sites beheren via sidebar");
    }

    @FXML
    private void onMachines() {
        System.out.println("Machines beheren via sidebar");
    }

    @FXML
    private void onLogout() {
        System.out.println("Logout clicked");
    }

    // ===== Tile Actions =====

    @FXML
    private void onTeamsTile() {
        System.out.println("Teams beheren via tile");
    }

    @FXML
    private void onSitesTile() {
        System.out.println("Sites beheren via tile");
    }

    @FXML
    private void onMachinesTile() {
        System.out.println("Machines beheren via tile");
    }
}