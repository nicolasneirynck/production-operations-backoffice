package gui;

import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import util.Rollen;
import util.View;

import java.util.List;
import java.util.Optional;

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
    @FXML private HBox usersRow;
    @FXML private HBox allTeamsRow;
    @FXML private HBox mijnTeamRow;
    @FXML private HBox sitesRow;

    @FXML private HBox loginRow;
    @FXML private Button logoutBtn;
    @FXML private HBox takenRow;

    @Getter
    @FXML private StackPane contentHost;

    @Setter private Navigator navigator;
    @Setter private AppContext context; // niet nodig hier

    @FXML
    private void initialize() {
        navRows = List.of(homeRow, usersRow, allTeamsRow, mijnTeamRow, sitesRow, takenRow);

        bindToAuthorization();
        handleAuthorizationChange(SecurityContext.userProperty().get());

        // logo
        logoImage.setViewport(null);
        logoImage.setPreserveRatio(true);
        logoImage.setSmooth(true);
        logoImage.setFitHeight(20);
    }

    public void showContent(Node node) {
        contentHost.getChildren().setAll(node);
    }


    @Override
    public void loadData() {
        clearActiveNavigation();
    }

    private void clearActiveNavigation() {
        navRows.forEach(row -> row.getStyleClass().remove("active"));
    }


    public void setActiveNavigation(View view) {
        clearActiveNavigation();

        HBox row = switch (view) {
            case HOME -> homeRow;
            case GEBRUIKER_OVERVIEW, GEBRUIKER_FORM -> usersRow;
            case ALL_TEAMS_OVERVIEW, MANAGER_TEAMS_FORM -> allTeamsRow;
            case MIJN_TEAM_OVERVIEW, VERANTWOORDELIJKE_TEAMS_FORM -> mijnTeamRow;
            case SITES_OVERVIEW, SITES_FORM -> sitesRow;
            case TAKEN_OVERVIEW, TAKEN_FORM -> takenRow;
            default -> null;
        };

        if (row != null) {
            row.getStyleClass().add("active");
        }
    }

    private void setRowVisibility(HBox row, boolean visible) {
        row.setVisible(visible);
        row.setManaged(visible);
    }

    private void changeVisibility(boolean visible) {
        setRowVisibility(usersRow, visible && Authorizer.has(Permission.GEBRUIKERS_BEHEREN));
        setRowVisibility(sitesRow, visible && Authorizer.has(Permission.SITES_BEHEREN));
        setRowVisibility(allTeamsRow, visible && Authorizer.has(Permission.ALLE_TEAMS_BEHEREN ));
        setRowVisibility(mijnTeamRow, visible && Authorizer.has(Permission.MIJN_TEAM_BEHEREN ));
        setRowVisibility(takenRow, visible && Authorizer.has(Permission.TAKEN_BEHEREN));
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
    private void onHome() {
        navigator.goTo(View.HOME);
    }

    @FXML
    private void onGebruikers() {
        navigator.goTo(View.GEBRUIKER_OVERVIEW);
    }

    @FXML
    private void onAllTeams() {
        navigator.goTo(View.ALL_TEAMS_OVERVIEW);
    }

    @FXML
    private void onMijnTeam(){
        navigator.goTo(View.MIJN_TEAM_OVERVIEW);
    }

    @FXML
    private void onSites() {
        navigator.goTo(View.SITES_OVERVIEW);
    }

    @FXML
    private void onTaken() {
        navigator.goTo(View.TAKEN_OVERVIEW);
    }

    @FXML
    private void onLogout() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType jaButton = new ButtonType("Ja");
        ButtonType neeButton = new ButtonType("Nee");

        confirmDialog.setTitle("Uitloggen");
        confirmDialog.setHeaderText("Wil je zeker uitloggen?");
        confirmDialog.setContentText("Je huidige sessie wordt afgesloten.");
        confirmDialog.getButtonTypes().setAll(jaButton, neeButton);

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == jaButton) {
            SecurityContext.logout();
        }
    }
}
