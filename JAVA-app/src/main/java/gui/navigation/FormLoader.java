package gui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import main.AppContext;

import java.io.IOException;
import java.util.function.Consumer;

public class FormLoader {
        public static <T extends FormController> void showForm(VBox formHost, AppContext context,
                                                               String fxmlPath, Consumer<T> initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource(fxmlPath));
            Parent form = loader.load();

            T controller = loader.getController();
            controller.setContext(context);
            controller.setOnClose(() -> hideForm(formHost));
            controller.loadData();

            if (initializer != null) {
                initializer.accept(controller);
            }

            formHost.getChildren().setAll(form);
            formHost.setManaged(true);
            formHost.setVisible(true);

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
