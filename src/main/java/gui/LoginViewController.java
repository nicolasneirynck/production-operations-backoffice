package gui;

import domein.services.LoginService;
import dto.AuthenticatedUserDTO;
import exception.ValidationException;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import main.AppContext;
import security.RolePermissions;
import security.SecurityContext;
import security.UserPrincipal;

public class LoginViewController implements NavigableController {
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_WACHTWOORD = "admin";
    private static final String MANAGER_EMAIL = "manager@test.com";
    private static final String MANAGER_WACHTWOORD = "manager";
    private static final String VERANTWOORDELIJKE_EMAIL = "verantwoordelijke@test.com";
    private static final String VERANTWOORDELIJKE_WACHTWOORD = "verantwoordelijke";

    @FXML private TextField emailTxt;
    @FXML private PasswordField wachtwoordTxt;
    @FXML private Button loginBtn;
    @FXML private Button cancelBtn;
    @FXML private Label algemeenErr;

    @Setter
    private Navigator navigator;
    private AppContext ctx;
    private LoginService loginService;

    @Override
    public void setContext(AppContext ctx) {
         this.ctx = ctx;
    }

    @FXML
    private void initialize() {}

    @Override
    public void loadData() {
        this.loginService = ctx.getLoginService();
    }

    @FXML
    private void onLogin() {
        clearErrors();

        try {
            AuthenticatedUserDTO gebruiker = loginService.login(emailTxt.getText(), wachtwoordTxt.getText());

            SecurityContext.login(new UserPrincipal(gebruiker.gebruikerId(),gebruiker.naam(), gebruiker.voornaam(), gebruiker.rol(), RolePermissions.getPermissions(gebruiker.rol())));
        } catch (ValidationException exception) {
            showLoginErrors(exception);
        }
    }

    @FXML
    private void onQuickLoginAdmin() {
        quickLogin(ADMIN_EMAIL, ADMIN_WACHTWOORD);
    }

    @FXML
    private void onQuickLoginManager() {
        quickLogin(MANAGER_EMAIL, MANAGER_WACHTWOORD);
    }

    @FXML
    private void onQuickLoginVerantwoordelijke() {
        quickLogin(VERANTWOORDELIJKE_EMAIL, VERANTWOORDELIJKE_WACHTWOORD);
    }

    private void quickLogin(String email, String wachtwoord) {
        emailTxt.setText(email);
        wachtwoordTxt.setText(wachtwoord);
        onLogin();
    }

    private void showLoginErrors(ValidationException exception) {
        clearErrors();

        String msg = exception.getExceptionMap().values().stream()
                .findFirst()
                .map(IllegalArgumentException::getMessage)
                .orElse("Ongeldige login.");

        algemeenErr.setText(msg);
    }

    private void clearErrors() {
        algemeenErr.setText("");
    }
}
