package gui.navigation;

import gui.LayoutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import main.AppContext;
import util.View;

import java.io.IOException;

// TODO nu ingewikkeld systeem met controllerfactory, is dit wel nodig? (afwachten tot authorisatie geïmplementeerd is)
public class Navigator {

    @Getter
    private final Stage stage;
    private final AppContext context;

    private LayoutController layoutController;

    public Navigator(Stage stage, AppContext context) {
        this.stage = stage;
        this.context = context;
    }

    public void initLayout(String layoutFxml, String windowTitle, double width, double height, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(layoutFxml));
            Parent root = loader.load();

            this.layoutController = loader.getController();
            initializeController(layoutController);

            Scene scene = new Scene(root, width, height);
            if (cssPath != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle(windowTitle);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException("Kan LayoutView niet laden: " + layoutFxml, e);
        }
    }

    public void goTo(View view) {
        System.out.println("Navigator goTo: " + view);

        if (layoutController == null) {
            throw new IllegalStateException("Layout is not initialized. Call initLayout(...) first.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));
//            loader.setControllerFactory(type -> {
//                try {
//                    return type.getDeclaredConstructor().newInstance();
//                } catch (Exception e) {
//                    throw new RuntimeException("Kan controller niet maken: " + type.getName(), e);
//                }
//            });

            Parent content = loader.load(); // FXML injecteren + initialize() oproepen
            initializeController(loader.getController());

            layoutController.showContent(content);
            stage.setTitle(view.title);

        } catch (Exception e) {
            throw new RuntimeException("Kan content view niet laden: " + view, e);
        }
    }

    public Parent load(View view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));
            Parent root = loader.load();

            initializeController(loader.getController());

            return root;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeController(Object controller) {

        if (controller instanceof NavigableController nc) {
            nc.setNavigator(this);
            nc.setContext(context);
            nc.loadData();
        }
    }

}