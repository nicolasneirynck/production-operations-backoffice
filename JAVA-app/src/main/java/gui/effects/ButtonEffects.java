package gui.effects;

import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.util.Duration;

public class ButtonEffects {
    public void applyEffect(Scene scene) {
        scene.getRoot().lookupAll(".btn-primary").forEach(node -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(160), node);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(160), node);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            node.setOnMouseEntered(e -> scaleUp.playFromStart());
            node.setOnMouseExited(e -> scaleDown.playFromStart());
        });
    }
}
