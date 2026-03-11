package gui;

import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Setter;
import main.AppContext;
import security.Authorizer;
import security.Permission;
import security.SecurityContext;
import security.UserPrincipal;
import util.Rollen;
import util.View;

public class ManagerHomeController implements NavigableController {

    @FXML private Button usersTile;
    @FXML private Button teamsTile;
    @FXML private Button sitesTile;
    @FXML private Label teamsTileLabel;


    @Setter private Navigator navigator;
    @Setter private AppContext context;

    @Setter
    private LayoutController layout;

    private void changeVisibility(boolean visible) {
        setTileVisibility(usersTile, visible & Authorizer.has(Permission.GEBRUIKERS_BEHEREN));
        setTileVisibility(sitesTile, visible & Authorizer.has(Permission.SITES_BEHEREN));
        setTileVisibility(teamsTile, visible & Authorizer.has(Permission.TEAMS_BEHEREN));
    }

    private void setTileVisibility(Button tile, boolean visible) {
        tile.setVisible(visible);
        tile.setManaged(visible);
    }

    private void handleAuthorizationChange(UserPrincipal newUser) {
        changeVisibility(newUser != null);
        if (newUser != null) {
            teamsTileLabel.setText(newUser.rol() == Rollen.VERANTWOORDELIJKE ? "Team\nBeheren" : "Teams\nBeheren");
        } else {
            teamsTileLabel.setText("Teams\nBeheren");
        }
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
        navigator.goTo(View.TEAMS_OVERVIEW);
    }

    @FXML
    private void onGebruikersTile() {
        navigator.goTo(View.GEBRUIKER_OVERVIEW);
    }

    @FXML
    public void onSitesTile() {
        navigator.goTo(View.SITES_OVERVIEW);
    }
}
