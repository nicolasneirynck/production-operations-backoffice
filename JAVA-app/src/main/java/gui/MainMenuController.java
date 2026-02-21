package gui;

import domein.TaakController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Setter;

public class MainMenuController {

    @FXML private Button gebruikersBhrnBtn;
    @FXML private Button takenBhrnBtn;
    @FXML private Button sitesBhrnBtn;

    @FXML private Label errorLbl;

    @Setter
    private Stage stage;

    @FXML
    private void onGebruikersBhrn(){
        System.out.println("gebruikers beheren start");
    }

    @FXML
    private void onTakenBhrn(){
        try {
            TaakController tc = new TaakController();

            // FXML Loader -> leest FXML (layout), JavaFX nodes maken (Tableview, Buttons,..), @FXML velden/methodes koppelen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TaakOverviewView.fxml"));
            // controller injecteren
            loader.setControllerFactory(type -> {
                if (type == TaakOverviewController.class)
                    return new TaakOverviewController(tc);
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Taken Beheren");
            }
            catch(Exception e){
                errorLbl.setText(e.getMessage());
            }
    }

    @FXML
    private void onSitesBhrn(){
        System.out.println("Sites beheren start");

    }

}
