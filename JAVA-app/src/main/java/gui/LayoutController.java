package gui;

import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import util.View;
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
    @FXML public HBox takenRow;

    @Getter
    @FXML private StackPane contentHost;

    @Setter private Navigator navigator;
    @Setter private AppContext context;

    @FXML
    private void initialize() {
        navRows = List.of(homeRow, teamsRow, sitesRow,takenRow); // TODO autorisatie? setSideNav()?

        // logo
        logoImage.setViewport(null);
        logoImage.setPreserveRatio(true);
        logoImage.setSmooth(true);
        logoImage.setFitHeight(20);
    }

    @Override
    public void loadData() {
        setActive(homeRow);
        navigator.goTo(View.HOME);
    }

    // TODO
    public void setUser(String name, String role) {
        userNameLbl.setText(name);
        userRoleLbl.setText(role);
    }

//    public void setContent(View view) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));
//
//            loader.setControllerFactory(type -> {
//                try {
//                    Object controller = type.getDeclaredConstructor().newInstance();
//
//                    if (controller instanceof NavigableController nc) {
//                        nc.setNavigator(navigator);
//                        nc.setContext(context);
//                    }
//
//                    return controller;
//
//                } catch (Exception e) {
//                    throw new RuntimeException("Kan controller niet maken: " + type.getName(), e);
//                }
//            });
//
//            Parent content = loader.load();
//
//            contentHost.getChildren().setAll(content);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Kan content niet laden: " + view, e);
//        }
//    }
//
    public void showContent(Node node) {
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
        navigator.goTo(View.HOME);
        setActive(homeRow);
    }

    @FXML
    private void onTeams() {
      //  setContent("/gui/TeamsOverviewContent.fxml"); // TODO
        navigator.goTo(View.TEAMS_OVERVIEW);
        setActive(teamsRow);
    }

    @FXML
    private void onSites() {
        //setContent("/gui/SitesOverviewContent.fxml"); // TODO
        navigator.goTo(View.SITES_OVERVIEW);
        setActive(sitesRow);
    }

    @FXML
    private void onTaken() {
        //setContent("/gui/SitesOverviewContent.fxml"); // TODO
        navigator.goTo(View.TAKEN_OVERVIEW);
        setActive(takenRow);
    }

    @FXML
    private void onLogout() {
        System.out.println("Logout clicked");
        // later: navigator.logout() of context.reset()
    }
}