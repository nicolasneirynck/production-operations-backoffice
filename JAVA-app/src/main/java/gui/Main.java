package gui;

import domein.GebruikerController;
import domein.SiteController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SiteController sc = new SiteController();
        GebruikerController gc = new GebruikerController();

        // FXML Loader -> leest FXML (layout), JavaFX nodes maken (Tableview, Buttons,..), @FXML velden/methodes koppelen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GebruikerOverviewView.fxml"));
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteOverviewView.fxml"));
        // controller injecteren

        loader.setControllerFactory(type -> {
            if (type == SiteOverviewController.class)
                return new SiteOverviewController(sc); // deze ipv no-args constructor
            else if (type == GebruikerOverviewController.class)
                return new GebruikerOverviewController(gc);

            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();
        Object controller = loader.getController();

        String title;
        if (controller instanceof SiteOverviewController) {
            title = "Sites beheren";
        } else if (controller instanceof  GebruikerOverviewController) {
            title = "Gebruikers beheren";
        } else {
            title = "Default";
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
