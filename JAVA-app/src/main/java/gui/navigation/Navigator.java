package gui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import main.AppContext;

import java.util.function.Consumer;

public class Navigator {

    @Getter
    private final Stage stage;
    private final AppContext context;

    public Navigator(Stage stage, AppContext context) {
        this.stage = stage;
        this.context = context;
    }

    public void goTo(View view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));
            loader.setControllerFactory(type -> {
                try {
                    Object controller = type.getDeclaredConstructor().newInstance();

                    if (controller instanceof NavigableController nc) {
                        nc.setNavigator(this);
                        nc.setContext(context);
                    }
                    return controller;

                } catch (Exception e) {
                    throw new RuntimeException("Kan controller niet maken: " + type.getName(), e);
                }
            });

            Parent root = loader.load();

            stage.setScene(new Scene(root));
            stage.setTitle(view.title);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException("Kan view niet laden: " + view, e);
        }
    }

    public <T extends NavigableController> void showDialog(View view, String title, Consumer<T> initController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));

            loader.setControllerFactory(type -> {
                try {
                    Object controller = type.getDeclaredConstructor().newInstance();

                    if (controller instanceof NavigableController nc) {
                        nc.setNavigator(this);
                        nc.setContext(context);
                    }
                    return controller;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();

            T controller = loader.getController();
            if (initController != null) initController.accept(controller);

            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.initOwner(stage);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

        } catch (Exception e) {
            throw new RuntimeException("Dialog openen mislukt: " + view, e);
        }
    }

}