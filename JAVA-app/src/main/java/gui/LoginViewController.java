package gui;

import domein.AuthenticationController;
import domein.GebruikerController;
import dto.GebruikerDTO;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import main.AppContext;
import security.RolePermissions;
import security.SecurityContext;
import security.UserPrincipal;

import java.util.Optional;

public class LoginViewController implements NavigableController {
    @FXML private TextField emailTxt;
    @FXML private TextField wachtwoordTxt;
    @FXML private Button loginBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorLbl;

    @Setter
    private Navigator navigator;
    private AppContext ctx;
    private AuthenticationController ac;

    @Override
    public void setContext(AppContext ctx) {
         this.ctx = ctx;
    }

    @FXML
    private void initialize() {}

    @Override
    public void loadData() {
        this.ac = ctx.getAuthenticationController();
    }

    @FXML
    private void onLogin() {
        Optional<GebruikerDTO> gebruikerOptional = ac.login(emailTxt.getText(), wachtwoordTxt.getText());
        if (gebruikerOptional.isPresent()) {
            GebruikerDTO gebruiker = gebruikerOptional.get();
            SecurityContext.login(new UserPrincipal(gebruiker.rol(), RolePermissions.getPermissions(gebruiker.rol())));
        } else {
            errorLbl.setText("Ongeldig email of wachtwoord");
        }
    }

    @FXML
    private void onCancel() {
        // TODO: sluit applicatie hier
    }
}
