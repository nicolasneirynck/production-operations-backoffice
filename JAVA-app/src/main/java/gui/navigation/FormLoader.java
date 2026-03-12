package gui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import main.AppContext;

import java.io.IOException;
import java.util.function.Consumer;

public class FormLoader {
        public static <T extends FormController> T showForm(VBox formHost, AppContext context,
                                                               String fxmlPath, Consumer<T> initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource(fxmlPath));
            Parent form = loader.load();

            T controller = loader.getController();
            controller.setContext(context);

            if (initializer != null) {
                initializer.accept(controller);
            }

            controller.loadData();

            formHost.getChildren().setAll(form);
            formHost.setManaged(true);
            formHost.setVisible(true);

            return controller;

        } catch (IOException e) {
            throw new RuntimeException("Kon formulier niet laden: " + fxmlPath, e);
        }
    }

        public static void hideForm(VBox formHost) {
        formHost.getChildren().clear();
        formHost.setManaged(false);
        formHost.setVisible(false);
    }
}
