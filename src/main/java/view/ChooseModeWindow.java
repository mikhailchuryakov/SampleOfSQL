package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static common.Constants.UserResolutionConstants.DEFAULT_USER_RESOLUTION_HEIGHT;
import static common.Constants.UserResolutionConstants.DEFAULT_USER_RESOLUTION_WIDTH;

public class ChooseModeWindow {
    private Stage modeWindow;
    private int mode;

    ChooseModeWindow(Stage stage) {
        modeWindow = new Stage() {
            {
                setResizable(false);
                setTitle("Choose Mode");
                initOwner(stage);
                setScene(new Scene(initControl(), 500, 250));
            }
        };
    }

    int getMode() {
        return mode;
    }

    private void setMode(int mode) {
        this.mode = mode;
    }

    void showAndWait() {
        modeWindow.showAndWait();
    }

    private VBox initControl() {
        HBox hBox = new HBox();
        VBox vBox = new VBox();

        Button txtCreateBtn = new Button() {
            {
                setText("Create script file");
                setPrefSize(DEFAULT_USER_RESOLUTION_WIDTH, DEFAULT_USER_RESOLUTION_HEIGHT);
                setFont(new Font(18));
            }
        };
        Button bdChangeBtn = new Button() {
            {
                setText("Edit database");
                setPrefSize(DEFAULT_USER_RESOLUTION_WIDTH, DEFAULT_USER_RESOLUTION_HEIGHT);
                setFont(new Font(18));
            }
        };
        Button settingsBtn = new Button() {
            {
                setText("Settings");
                setPrefSize(DEFAULT_USER_RESOLUTION_WIDTH, DEFAULT_USER_RESOLUTION_HEIGHT / 2);
                setFont(new Font(18));
            }
        };

        HBox.setHgrow(settingsBtn, Priority.ALWAYS);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        hBox.getChildren().addAll(txtCreateBtn, bdChangeBtn);
        vBox.getChildren().addAll(hBox, settingsBtn);

        txtCreateBtn.setOnMouseClicked(e -> {
            setMode(1);
            modeWindow.close();
        });

        bdChangeBtn.setOnMouseClicked(e -> {
            setMode(2);
            modeWindow.close();
        });

        settingsBtn.setOnMouseClicked(e -> {
            SettingsWindow settingsWindow = new SettingsWindow(modeWindow);
            settingsWindow.showAndWait();
        });

        return vBox;
    }
}
