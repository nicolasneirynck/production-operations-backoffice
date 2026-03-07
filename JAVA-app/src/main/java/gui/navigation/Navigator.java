package gui.navigation;

import domein.GebruikerController;
import gui.GebruikerOverviewController;
import gui.LayoutController;
import gui.TaakOverviewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import main.AppContext;
import security.Authorizer;
import security.Permission;

import java.util.function.Consumer;

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

            if (layoutController instanceof NavigableController nc) {
                nc.setNavigator(this);
                nc.setContext(context);
            }

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
        // TODO: add de andere
        if (view == View.SITES_OVERVIEW) {
            Authorizer.require(Permission.SITES_BEHEREN);
        }

        if (layoutController == null) {
            throw new IllegalStateException("Layout is not initialized. Call initLayout(...) first.");
        }

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

            Parent content = loader.load();
            layoutController.setContent(content);

            String title = view.title;
            stage.setTitle(title);

        } catch (Exception e) {
            throw new RuntimeException("Kan content view niet laden: " + view, e);
        }
    }



//    public void goTo(View view) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));
//            loader.setControllerFactory(type -> {
//                try {
//                    Object controller = type.getDeclaredConstructor().newInstance();
//
//                    if (controller instanceof NavigableController nc) {
//                        nc.setNavigator(this);
//                        nc.setContext(context);
//                    }
//                    return controller;
//
//                } catch (Exception e) {
//                    throw new RuntimeException("Kan controller niet maken: " + type.getName(), e);
//                }
//            });
//
//            Parent root = loader.load();
//
//            stage.setScene(new Scene(root));
//            stage.setTitle(view.title);
//            stage.show();
//
//        } catch (Exception e) {
//            throw new RuntimeException("Kan view niet laden: " + view, e);
//        }
//    }

//    public void tempGoTo(View view, String title) {
//        try {
//            // FXML Loader -> leest FXML (layout), JavaFX nodes maken (Tableview, Buttons,..), @FXML velden/methodes koppelen
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));
//            // controller injecteren
//            loader.setControllerFactory(type -> {
//                if (type == GebruikerOverviewController.class) {
//                    // TODO: gebruik ctx & stage
//                    return new GebruikerOverviewController(new GebruikerController());
//                }
//                try {
//                    return type.getDeclaredConstructor().newInstance();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            Parent root = loader.load();
//            stage.setScene(new Scene(root));
//            stage.setTitle(title);
//        }
//        catch(Exception e){
//            //errorLbl.setText(e.getMessage());
//            e.printStackTrace();
//        }
//    }

//    public <T extends NavigableController> void showDialog(View view, String title, Consumer<T> initController) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.fxml));
//
//            loader.setControllerFactory(type -> {
//                try {
//                    Object controller = type.getDeclaredConstructor().newInstance();
//
//                    if (controller instanceof NavigableController nc) {
//                        nc.setNavigator(this);
//                        nc.setContext(context);
//                    }
//                    return controller;
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            Parent root = loader.load();
//
//            T controller = loader.getController();
//            if (initController != null) initController.accept(controller);
//
//            Stage dialog = new Stage();
//            dialog.setTitle(title);
//            dialog.initOwner(stage);
//            dialog.initModality(Modality.APPLICATION_MODAL);
//            dialog.setScene(new Scene(root));
//            dialog.showAndWait();
//
//        } catch (Exception e) {
//            throw new RuntimeException("Dialog openen mislukt: " + view, e);
//        }
//    }

}