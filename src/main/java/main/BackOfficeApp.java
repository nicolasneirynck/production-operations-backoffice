package main;

import exception.ValidationException;
import gui.LayoutController;
import gui.navigation.Navigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BackOfficeApp extends Application {

    @Override
    public void start(Stage stage) {
        loadFonts();

        AppContext context = new AppContext();
        Navigator navigator = new Navigator(stage, context);

        try {
            MockdataSeeder.seed(context);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        loadLayout(navigator);

        navigator.bindNavigationToAuthentication();
        navigator.goToDefaultView();
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-SemiBold.ttf").toExternalForm(), 10);
    }

    private void loadLayout(Navigator navigator) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LayoutView.fxml"));
            Parent root = loader.load();

            LayoutController layoutController = loader.getController();
            navigator.setLayoutController(layoutController);

            Scene scene = new Scene(root, 1440, 1024);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

            navigator.getStage().setScene(scene);
            navigator.getStage().setTitle("BackOffice");
            navigator.getStage().show();

        } catch (Exception e) {
            throw new RuntimeException("Kan LayoutView niet laden", e);
        }
    }
}
