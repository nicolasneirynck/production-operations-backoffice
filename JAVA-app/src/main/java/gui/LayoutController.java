package gui;

import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import util.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
import security.UserPrincipal;

import java.util.List;

public class LayoutController implements NavigableController {

    // topbar
    @FXML private StackPane logoHeader;
    @FXML private ImageView logoImage;
    @FXML private Label userNameLbl;
    @FXML private Label userRoleLbl;
    @FXML private StackPane notif;

    // sidebar
    private List<HBox> navRows;
    @FXML private HBox homeRow;
    @FXML private HBox teamsRow;
    @FXML private HBox sitesRow;
    @FXML private HBox loginRow;
   // @FXML private HBox machinesRow;
    @FXML private Button logoutBtn;
    @FXML public HBox takenRow;

    @Getter
    @FXML private StackPane contentHost;

    @Setter private Navigator navigator;
    @Setter private AppContext context;

    private void setRowVisibility(HBox row, boolean visible) {
        row.setVisible(visible);
        row.setManaged(visible);
    }

    private void changeVisibility(boolean visible) {
        setRowVisibility(sitesRow, visible & Authorizer.has(Permission.SITES_BEHEREN));
        setRowVisibility(teamsRow, visible & Authorizer.has(Permission.TEAMS_BEHEREN));
        setRowVisibility(takenRow, visible & Authorizer.has(Permission.TAKEN_BEHEREN));
        setRowVisibility(homeRow, visible);

        userNameLbl.setVisible(visible);
        userRoleLbl.setVisible(visible);
        notif.setVisible(visible);
        logoutBtn.setVisible(visible);

        loginRow.setVisible(!visible);
        loginRow.setManaged(!visible);
    }

    private void handleAuthorizationChange(UserPrincipal newUser) {
        if (newUser != null) {
            changeVisibility(true);

            userNameLbl.setText(newUser.voornaam() + " " + newUser.naam());
            userRoleLbl.setText(newUser.rol().toString());
        } else {
            changeVisibility(false);
        }
    }

    private void bindToAuthorization() {
        SecurityContext.userProperty().addListener((obs, oldUser, newUser) -> {
            handleAuthorizationChange(newUser);
        });
    }

    @FXML
    private void initialize() {
        navRows = List.of(homeRow, teamsRow, sitesRow,takenRow);

        bindToAuthorization();
        handleAuthorizationChange(SecurityContext.userProperty().get());

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
//    if (view == View.SITES_OVERVIEW) {
//        Authorizer.require(Permission.SITES_BEHEREN);
//    }

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
        // TODO: confirmation pop-up
        SecurityContext.logout();
    }
}