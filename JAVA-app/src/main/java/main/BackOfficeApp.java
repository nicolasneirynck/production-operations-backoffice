package main;

import exception.SiteException;
import exception.TaakException;
import gui.navigation.Navigator;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BackOfficeApp extends Application {

    @Override
    public void start(Stage stage) {

        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-SemiBold.ttf").toExternalForm(), 10);

        AppContext context = new AppContext();
        Navigator navigator = new Navigator(stage, context);

        try {
            MockdataSeeder.seed(context);
        } catch (SiteException | TaakException e) {
            throw new RuntimeException(e);
        }

        navigator.initLayout("/gui/LayoutView.fxml", "BackOffice", 1200, 800, "/css/app.css");
    }
}

