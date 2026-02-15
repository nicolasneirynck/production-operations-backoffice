package gui;

import domein.SiteController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SiteController sc = new SiteController();

        // FXML Loader -> leest FXML (layout), JavaFX nodes maken (Tableview, Buttons,..), @FXML velden/methodes koppelen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteOverviewView.fxml"));
        // controller injecteren
        loader.setControllerFactory(type -> {
            if (type == SiteOverviewController.class)
                return new SiteOverviewController(sc); // deze ipv no-args constructor
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Sites beheren");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
