package gui.navigation;

import gui.LayoutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import lombok.Getter;
import main.AppContext;
import security.Authorizer;
import security.SecurityContext;
import util.View;

public class Navigator {

    @Getter
    private final Stage stage;
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

    public void bindNavigationToAuthentication() {
        SecurityContext.userProperty().addListener((obs, oldUser, newUser) -> goToDefaultView());
    }

    public void goToDefaultView() {
        if (layoutController == null) {
            throw new IllegalStateException("Layout is not initialized. Call setLayoutController() first.");
        }

        goTo(SecurityContext.getUser() != null ? View.HOME : View.LOGIN);
    }

    public void goTo(View view) {
        Authorizer.require(view);

        if (layoutController == null) {
            throw new IllegalStateException("Layout is not initialized. Call initLayout() first.");
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
            layoutController.setActiveNavigation(view);
            stage.setTitle(view.title);

        } catch (Exception e) {
            throw new RuntimeException("Kan content view niet laden: " + view, e);
        }
    }
}
