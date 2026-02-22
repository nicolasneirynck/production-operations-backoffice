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

        navigator.goTo(View.MAIN_MENU);
    }
}

