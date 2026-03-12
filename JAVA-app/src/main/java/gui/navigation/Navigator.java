package gui.navigation;

import gui.LayoutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import main.AppContext;
import security.Authorizer;
import security.Permission;
import security.SecurityContext;
import security.UserPrincipal;
import util.Rollen;
import util.View;

public class Navigator {

    @Getter
    private final Stage stage;
    //private final AppContext context;
    private final ControllerInitializer controllerInitializer;

    private NavigableController currentController;
    private LayoutController layoutController;

    public Navigator(Stage stage, AppContext context) {
        this.stage = stage;
        this.controllerInitializer = new ControllerInitializer(context, this);
    }

    public void setLayoutController(LayoutController layoutController) {
        this.layoutController = layoutController;
        controllerInitializer.initialize(layoutController);
    }

    public void goTo(View view) {
        if (view == View.SITES_OVERVIEW || view == View.SITES_FORM) {
            Authorizer.require(Permission.SITES_BEHEREN);
        } else if (view == View.TAKEN_OVERVIEW || view == View.TAKEN_FORM) {
            Authorizer.require(Permission.TAKEN_BEHEREN);
        } else if (view == View.GEBRUIKER_OVERVIEW || view == View.GEBRUIKER_FORM) {
            Authorizer.require(Permission.GEBRUIKERS_BEHEREN);
        } else if (view == View.ALL_TEAMS_OVERVIEW || view == View.MIJN_TEAM_OVERVIEW || view == View.TEAMS_FORM) {
            Authorizer.require(Permission.TEAMS_BEHEREN);
        }

        if (layoutController == null) {
            throw new IllegalStateException("Layout is not initialized. Call initLayout(...) first.");
        }

        if (currentController instanceof NavigationGuard guard) {
            if (!guard.canNavigateAway()) {
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));
            Parent content = loader.load(); // FXML injecteren + initialize() oproepen

            Object controller = loader.getController();
            controllerInitializer.initialize(controller);

            if (controller instanceof NavigableController nc) {
                currentController = nc;
            } else {
                currentController = null;
            }

            layoutController.showContent(content);
            stage.setTitle(view.title);

        } catch (Exception e) {
            throw new RuntimeException("Kan content view niet laden: " + view, e);
        }
    }

    public void goToTeams() {
        UserPrincipal user = SecurityContext.getUser();

        if (user != null && user.rol() == Rollen.VERANTWOORDELIJKE) {
            goTo(View.MIJN_TEAM_OVERVIEW);
        } else {
            goTo(View.ALL_TEAMS_OVERVIEW);
        }
    }
}
