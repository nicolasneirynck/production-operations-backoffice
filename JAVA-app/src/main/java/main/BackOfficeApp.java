package main;

import dto.SiteDTO;
import exception.SiteException;
import exception.TaakException;
import gui.MainMenuController;
import gui.ObservableSites;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.OperationeleStatus;
import util.ProductieStatus;

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

