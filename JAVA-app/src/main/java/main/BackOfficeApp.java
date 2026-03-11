package main;

import exception.ValidationException;
import gui.effects.ButtonEffects;
import gui.navigation.Navigator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import security.SecurityContext;
import util.View;

public class BackOfficeApp extends Application {

    private void bindAuthNavigation(Navigator navigator) {
        SecurityContext.userProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null) navigator.goTo(View.HOME);
            else navigator.goTo(View.LOGIN);
        });
    }

    private void initGuiEffects(Scene scene) {
        ButtonEffects buttonEffects = new ButtonEffects();
        buttonEffects.applyEffect(scene);
    }

    @Override
    public void start(Stage stage) {

        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/NunitoSans-SemiBold.ttf").toExternalForm(), 10);

        AppContext context = new AppContext();
        Navigator navigator = new Navigator(stage, context);

        try {
            MockdataSeeder.seed(context);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        navigator.initLayout("/gui/LayoutView.fxml", "BackOffice", 1440, 1024, "/css/app.css");

        bindAuthNavigation(navigator);
        navigator.goTo(View.LOGIN);

        initGuiEffects(stage.getScene());
    }
}
