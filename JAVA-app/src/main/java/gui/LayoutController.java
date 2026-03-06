package gui;

import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import main.AppContext;
import security.Authorizer;
import security.Permission;
import security.SecurityContext;

import java.io.IOException;
import java.util.List;

public class LayoutController implements NavigableController {

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
    @Getter
    @FXML private StackPane contentHost;

    @Setter private Navigator navigator;
    @Setter private AppContext context;

    @FXML
    private void initialize() {
        navRows = List.of(homeRow, teamsRow, sitesRow); // TODO autorisatie? setSideNav()?

        // logo
        logoImage.setViewport(null);
        logoImage.setPreserveRatio(true);
        logoImage.setSmooth(true);
        logoImage.setFitHeight(20);
       // logoImage.setFitWidth(140);

        // default
        setActive(homeRow);
        setContent(View.HOME);
    }

    // TODO
    public void setUser(String name, String role) {
        userNameLbl.setText(name);
        userRoleLbl.setText(role);
    }

    public void setContent(View view) {
        if (view == View.SITES_OVERVIEW) {
            Authorizer.require(Permission.SITES_BEHEREN);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));

            loader.setControllerFactory(type -> {
                try {
                    Object controller = type.getDeclaredConstructor().newInstance();

                    if (controller instanceof NavigableController nc) {
                        nc.setNavigator(navigator);
                        nc.setContext(context);
                    }

                    return controller;

                } catch (Exception e) {
                    throw new RuntimeException("Kan controller niet maken: " + type.getName(), e);
                }
            });

            Parent content = loader.load();

            contentHost.getChildren().setAll(content);

        } catch (Exception e) {
            throw new RuntimeException("Kan content niet laden: " + view, e);
        }
    }

    public void setContent(Node node) {
        contentHost.getChildren().setAll(node);
    }

    private void setActive(HBox activeRow) {
        navRows.forEach(row -> row.getStyleClass().remove("active"));

        if (!activeRow.getStyleClass().contains("active")) {
            activeRow.getStyleClass().add("active");
        }
    }

    @FXML
    private void onHome() {
        setContent(View.HOME);
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
        setContent(View.SITES_OVERVIEW);
        setActive(sitesRow);
    }

    @FXML
    private void onLogout() {
        // TODO: confirmation pop-up
        SecurityContext.logout();
    }
}