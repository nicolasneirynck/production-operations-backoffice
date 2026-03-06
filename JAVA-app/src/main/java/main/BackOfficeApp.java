package main;

import exception.SiteException;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.dev.DevSeeder;
import security.SecurityContext;

public class BackOfficeApp extends Application {

    private void bindAuthNavigation(Navigator navigator) {
        SecurityContext.userProperty().addListener((obs, oldUser, newUser) -> {
            // TODO: in de plaats van goTo(MAIN_MENU): laat een specifiek scherm zien afhankelijk van de role?
            if (newUser != null) navigator.goTo(View.MAIN_MENU);
//            else navigator.goTo(View.LOGIN);
            else navigator.goTo(View.LOGIN);
        });
    }

    @Override
    public void start(Stage stage) throws Exception {

        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-SemiBold.ttf").toExternalForm(), 10);

        AppContext context = new AppContext();
        Navigator navigator = new Navigator(stage, context);

        try {
            new DevSeeder(context.getGebruikerRepo()).seed();
            MockdataSeeder.seed(context);
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }

        navigator.initLayout("/gui/LayoutView.fxml", "BackOffice", 1200, 800, "/css/app.css");

        bindAuthNavigation(navigator);
        navigator.goTo(View.LOGIN);
    }
}

