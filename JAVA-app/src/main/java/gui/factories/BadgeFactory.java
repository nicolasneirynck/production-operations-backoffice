package gui.factories;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BadgeFactory {

    private BadgeFactory() {
    }

    public static Label createBadge(String text, String... styleClasses) {
        Label badge = new Label(text);

        if (styleClasses != null) {
            badge.getStyleClass().addAll(styleClasses);
        }

        return badge;
    }

    public static HBox createRemovableBadge(String text, Runnable onRemove, String... styleClasses) {
        HBox badge = new HBox(8);
        badge.setAlignment(Pos.CENTER_LEFT);

        if (styleClasses != null) {
            badge.getStyleClass().addAll(styleClasses);
        }

        Label textLbl = new Label(text);

        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("icon-button");
        removeBtn.getStyleClass().add("badge-remove-btn");
        removeBtn.setOnAction(e -> onRemove.run());

        badge.getChildren().addAll(textLbl, removeBtn);
        return badge;
    }
}
