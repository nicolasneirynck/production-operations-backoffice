package gui;

import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Setter;
import main.AppContext;
import util.View;
import security.Authorizer;
import security.Permission;
import security.SecurityContext;
import security.UserPrincipal;

public class ManagerHomeController implements NavigableController {

    @FXML private Button teamsTile;
    @FXML private Button sitesTile;


    @Setter private Navigator navigator;
    @Setter private AppContext context;

    @Setter
    private LayoutController layout;

    private void changeVisibility(boolean visible) {
        // TODO: add de andere
        sitesTile.setVisible(visible & Authorizer.has(Permission.SITES_BEHEREN));
    }

    private void handleAuthorizationChange(UserPrincipal newUser) {
        changeVisibility(newUser != null);
    }

    private void bindToAuthorization() {
        SecurityContext.userProperty().addListener((obs, oldUser, newUser) -> {
            handleAuthorizationChange(newUser);
        });
    }

    @FXML
    private void initialize() {
        bindToAuthorization();
        handleAuthorizationChange(SecurityContext.userProperty().get());
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