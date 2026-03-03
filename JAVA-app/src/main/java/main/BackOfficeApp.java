package main;

import gui.MainMenuController;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BackOfficeApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        AppContext context = new AppContext();
        Navigator navigator = new Navigator(stage,context);

       // navigator.goTo(View.MAIN_MENU);

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ManagerHomeView.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LayoutView.fxml"));
        Parent root = loader.load();

        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Bold.ttf").toExternalForm(), 10);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

        stage.setTitle("Backoffice APP");
        stage.setScene(scene);
        stage.show();
    }
}

