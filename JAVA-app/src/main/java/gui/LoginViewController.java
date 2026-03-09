package gui;

import domein.AuthenticationController;
import dto.GebruikerDTO;
import exception.LoginException;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML private Label algemeenErr;
    @FXML private Label emailErr;
    @FXML private Label wachtwoordErr;

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
        clearErrors();

        try {
            GebruikerDTO gebruiker = ac.login(emailTxt.getText(), wachtwoordTxt.getText());

            SecurityContext.login(new UserPrincipal(gebruiker.naam(), gebruiker.voornaam(), gebruiker.rol(), RolePermissions.getPermissions(gebruiker.rol())));
        } catch (LoginException exception) {
            showLoginErrors(exception);
        }
    }

    private void showLoginErrors(LoginException exception) {
        clearErrors();

        exception.getExceptionMap().forEach((field, iae) -> {
            String msg = iae.getMessage();

            switch (field) {
                case "email" -> {
                    emailErr.setText(msg);
//                    naamTf.getStyleClass().add("field-error");
                }
                case "wachtwoord" -> {
                    wachtwoordErr.setText(msg);
//                    capaciteitTf.getStyleClass().add("field-error");
                }
                case "onbestaand" -> {
                    algemeenErr.setText(msg);
//                    capaciteitTf.getStyleClass().add("field-error");
                }
                default -> {
                    new Alert(Alert.AlertType.ERROR, msg).showAndWait();
                }
            }
        });
    }

    private void clearErrors() {
        emailErr.setText("");
        wachtwoordErr.setText("");
        algemeenErr.setText("");
    }
}
