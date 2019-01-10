package view;

import controller.WebPageParser;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ChangeDataBaseWindow extends BorderPane {
    private Stage dbWindow;

    ChangeDataBaseWindow(Stage stage) {
        this.dbWindow = stage;
//        dbWindow = new Stage() {
//            {
//                setResizable(false);
//                setTitle("Change db");
//                initOwner(stage);
//                setScene(new Scene(init(), 300, 300));
//            }
//        };
        setCenter(init());
    }

    private VBox init() {
        VBox vBox = new VBox();

        vBox.getChildren().add(createEnterVinArea());


        return vBox;
    }


    private HBox createEnterVinArea() {
        final Label label = new Label("VIN: ");
        final TextArea textArea = new TextArea();
        textArea.setPrefWidth(140);
        textArea.setMinHeight(25);
        textArea.setMaxHeight(25);
        addTextLimiter(textArea, 17);
        final Button checkBtn = new Button("Check");
        HBox hBox = new HBox() {
            {
                setSpacing(8);
                setPadding(new Insets(8));
            }
        };

        hBox.getChildren().addAll(label, textArea, checkBtn);

        checkBtn.setOnMouseClicked(e -> {
            String vinText = textArea.getText();
            if (vinText.length() == 17) {

                // 5
                try {
                    WebPageParser webPageParser = new WebPageParser(dbWindow, vinText, true);
                    setCenter(webPageParser);
                } catch (IOException e1) {
                    throw new UncheckedIOException(e1);
                }
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
