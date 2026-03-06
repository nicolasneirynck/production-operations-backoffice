package gui;

import domein.AuthenticationController;
import domein.GebruikerController;
import dto.GebruikerDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.AppContext;

import java.util.Optional;

public class LoginViewController {
    @FXML private TextField emailTxt;
    @FXML private TextField wachtwoordTxt;
    @FXML private Button loginBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorLbl;

    private final AppContext ctx;
    private final Stage stage;
    private final AuthenticationController ac;

    public LoginViewController(AppContext ctx, Stage stage){
        this.ctx = ctx;
        this.stage = stage;
        this.ac = ctx.getAuthenticationController();
    }

    private void openMainMenu() {
        try {
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
            stage.setScene(new Scene(root));
            stage.setTitle("Hoofdmenu");
        }
        catch(Exception e){
            errorLbl.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogin() {
        Optional<GebruikerDTO> gebruikerOptional = ac.login(emailTxt.getText(), wachtwoordTxt.getText());
        if (gebruikerOptional.isPresent()) {
            openMainMenu();
        } else {
            errorLbl.setText("Ongeldig email of wachtwoord");
        }
    }

    @FXML
    private void onCancel() {
        // TODO: sluit applicatie hier
    }
}
