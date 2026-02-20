package gui;

import domein.SiteController;
import domein.TaakController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TakenBeherenApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TaakController tc = new TaakController();

        // FXML Loader -> leest FXML (layout), JavaFX nodes maken (Tableview, Buttons,..), @FXML velden/methodes koppelen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TaakOverviewView.fxml"));
        // controller injecteren
        loader.setControllerFactory(type -> {
            if (type == TaakOverviewController.class)
                return new TaakOverviewController(tc); // deze ipv no-args constructor
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Taken beheren");
        stage.show();
    }
}
