package gui.factories;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButtonFactory {
    public static Button createIconButton(String imagePath, String tooltipText) {
        Button button = new Button();

        ImageView icon = new ImageView(new Image(imagePath));
        icon.setFitWidth(16);
        icon.setFitHeight(16);

        button.setGraphic(icon);
        button.setTooltip(new Tooltip(tooltipText));
        button.getStyleClass().add("icon-button");

        return button;
    }
}
