package gui;

import domein.GebruikerController;
import domein.TaakController;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Setter;
import main.AppContext;

public class MainMenuController implements NavigableController {

    @FXML private Button gebruikersBhrnBtn;
    @FXML private Button takenBhrnBtn;
    @FXML private Button sitesBhrnBtn;
    @FXML private Label errorLbl;

    @Setter private Navigator navigator;
    @Setter private AppContext context;

    @FXML
    private void onGebruikersBhrn(){
        navigator.tempGoTo(View.GEBRUIKER_OVERVIEW,"Gebruikers beheren");
    }

    @FXML
    private void onTakenBhrn(){
        navigator.goTo(View.TAKEN_OVERVIEW);
    }

    @FXML
    private void onSitesBhrn() {
        navigator.goTo(View.SITES_OVERVIEW);
    }
}
