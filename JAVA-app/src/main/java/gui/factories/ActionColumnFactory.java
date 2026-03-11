package gui.factories;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class ActionColumnFactory {

    public static <T> void configureEditDeleteColumn(
            TableColumn<T, T> column,
            Consumer<T> onEdit,
            Consumer<T> onDelete
    ) {
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));

        column.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn =
                    IconButtonFactory.createIconButton("/images/pencil-write.png", "Bewerken");

            private final Button deleteBtn =
                    IconButtonFactory.createIconButton("/images/bin.png", "Verwijderen");

            private final HBox box = new HBox(8, editBtn, deleteBtn);

            {
                box.setAlignment(Pos.CENTER);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                editBtn.setOnAction(e -> {
                    T item = getItem();
                    if (item != null) {
                        onEdit.accept(item);
                    }
                });

                deleteBtn.setOnAction(e -> {
                    T item = getItem();
                    if (item != null) {
                        onDelete.accept(item);
                    }
                });
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });
    }

    public static <T> void configureEditOnlyColumn(
            TableColumn<T, T> column,
            Consumer<T> onEdit
    ) {
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));

        column.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn =
                    IconButtonFactory.createIconButton("/images/pencil-write.png", "Bewerken");

            private final HBox box = new HBox(editBtn);

            {
                box.setAlignment(Pos.CENTER);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                editBtn.setOnAction(e -> {
                    T item = getItem();
                    if (item != null) {
                        onEdit.accept(item);
                    }
                });
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });
    }
}
