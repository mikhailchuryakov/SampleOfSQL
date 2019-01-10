package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class EnterVinWindow extends BorderPane {
    private Stage stage;
    private String vinStr;

    public EnterVinWindow() throws IOException {
        stage = new Stage() {
            {
                setResizable(false);
                setTitle("Enter VIN");
                initOwner(stage);
                setScene(new Scene(createEnterVinArea()));
            }
        };
    }

    public void showAndWait() {
        stage.showAndWait();
    }

    public String getVinStr() {
        return vinStr;
    }

    private HBox createEnterVinArea() {
        final Label label = new Label("VIN: ");
        final TextArea textArea = new TextArea();
        textArea.setPrefWidth(140);
        textArea.setMinHeight(25);
        textArea.setMaxHeight(25);
        addTextLimiter(textArea, 17);
        final Button enterBtn = new Button("Enter");
        HBox hBox = new HBox() {
            {
                setPadding(new Insets(8));
                setSpacing(8);
            }
        };

        hBox.getChildren().addAll(label, textArea, enterBtn);

        enterBtn.setOnMouseClicked(e -> {
            String vinText = textArea.getText();
            if (vinText.length() == 17) {
                this.vinStr = vinText;
                stage.close();
            } else {
                textArea.setText("Invalid VIN!");
            }
        });

        return hBox;
    }

    private void addTextLimiter(final TextArea tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }
}
