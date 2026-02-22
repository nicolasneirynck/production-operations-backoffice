package main;

import gui.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BackOfficeApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        AppContext ctx = new AppContext();

        // FXML Loader -> leest FXML (layout), JavaFX nodes maken (Tableview, Buttons,..), @FXML velden/methodes koppelen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenuView.fxml"));
        // controller injecteren
        loader.setControllerFactory(type -> {
            if (type == MainMenuController.class)
                return new MainMenuController(ctx,stage);
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Hoofdmenu");
        stage.show();
    }
}

