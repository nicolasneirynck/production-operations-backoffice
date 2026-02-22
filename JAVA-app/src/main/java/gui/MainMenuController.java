package gui;

import domein.GebruikerController;
import domein.TaakController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Setter;
import main.AppContext;

public class MainMenuController {

    @FXML private Button gebruikersBhrnBtn;
    @FXML private Button takenBhrnBtn;
    @FXML private Button sitesBhrnBtn;
    @FXML private Label errorLbl;

    private final AppContext ctx;
    private final Stage stage;

    public MainMenuController(AppContext ctx, Stage stage){
        this.ctx = ctx;
        this.stage = stage;
    }

    private void openCorrespondingGui(String resource, String title) {
        try {
            // FXML Loader -> leest FXML (layout), JavaFX nodes maken (Tableview, Buttons,..), @FXML velden/methodes koppelen
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            // controller injecteren
            loader.setControllerFactory(type -> {
                if (type == TaakOverviewController.class)
                    return new TaakOverviewController(ctx, stage);
                else if (type == SiteOverviewController.class) {
                    return new SiteOverviewController(ctx, stage);
                } else if (type == GebruikerOverviewController.class) {
                    // TODO: gebruik ctx & stage
                    return new GebruikerOverviewController(new GebruikerController());
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        }
        catch(Exception e){
            errorLbl.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onGebruikersBhrn(){
        openCorrespondingGui("/gui/GebruikerOverviewView.fxml", "Gebruikers Beheren");
    }

    @FXML
    private void onTakenBhrn(){
        openCorrespondingGui("/gui/TaakOverviewView.fxml", "Taken Beheren");
    }

    @FXML
    private void onSitesBhrn() {
        openCorrespondingGui("/gui/SiteOverviewView.fxml", "Sites Beheren");
    }
}
