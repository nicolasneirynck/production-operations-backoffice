package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class LayoutController {

    // topbar
    @FXML private StackPane logoHeader;
    @FXML private ImageView logoImage;
    @FXML private Label userNameLbl;
    @FXML private Label userRoleLbl;

    // sidebar
    private List<HBox> navRows;
    @FXML private HBox homeRow;
    @FXML private HBox teamsRow;
    @FXML private HBox sitesRow;
   // @FXML private HBox machinesRow;

    // content
    @FXML private StackPane contentHost;

    @FXML
    private void initialize() {
        navRows = List.of(homeRow, teamsRow, sitesRow); // TODO autorisatie? setSideNav()?

        // logo
        logoImage.setViewport(null);
        logoImage.setPreserveRatio(true);
        logoImage.setSmooth(true);
        logoImage.setFitHeight(20);
       // logoImage.setFitWidth(140);

        // tijdelijk
        setContent("/gui/ManagerHomeContent.fxml");
        // default
        setActive(homeRow);
    }

    // TODO
    public void setUser(String name, String role) {
        userNameLbl.setText(name);
        userRoleLbl.setText(role);
    }

    public void setContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            Object controller = loader.getController();

            if (controller instanceof ManagerHomeController homeController) {
                homeController.setLayout(this);
            }

            contentHost.getChildren().setAll(content);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setActive(HBox activeRow) {
        navRows.forEach(row -> row.getStyleClass().remove("active"));

        if (!activeRow.getStyleClass().contains("active")) {
            activeRow.getStyleClass().add("active");
        }
    }

    @FXML
    private void onHome() {
        setContent("/gui/ManagerHomeContent.fxml");
        setActive(homeRow);
    }

    @FXML
    private void onTeams() {
      //  setContent("/gui/TeamsOverviewContent.fxml"); // TODO
        System.out.println("team beheer geopend");
        setActive(teamsRow);
    }

    @FXML
    private void onSites() {
        //setContent("/gui/SitesOverviewContent.fxml"); // TODO
        System.out.println("sites beheer geopend");
        setActive(sitesRow);
    }

    @FXML
    private void onLogout() {
        System.out.println("Logout clicked");
        // later: navigator.logout() of context.reset()
    }
}