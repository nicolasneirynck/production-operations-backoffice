package main;

import gui.MainMenuController;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BackOfficeApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        AppContext context = new AppContext();
        Navigator navigator = new Navigator(stage,context);

       // navigator.goTo(View.MAIN_MENU);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ManagerHomeView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

        stage.setTitle("Manager - Home");
        stage.setScene(scene);
        stage.show();
    }
}

