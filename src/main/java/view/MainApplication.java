package view;

import controller.WebPageParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static common.Constants.TitleConstants.DEFAULT_TITLE;
import static common.Constants.UserResolutionConstants.DEFAULT_USER_RESOLUTION_HEIGHT;


public final class MainApplication extends Application {
    private final double DEFAULT_HEIGHT = 0.7 * DEFAULT_USER_RESOLUTION_HEIGHT;
    private final double DEFAULT_WIDTH = 400;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene mainScene;

        ChooseModeWindow chooseModeWindow = new ChooseModeWindow(primaryStage);
        chooseModeWindow.showAndWait();

        switch (chooseModeWindow.getMode()) {
            case 1:
                WebPageParser tis = new WebPageParser(primaryStage);
                mainScene = new Scene(tis, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                break;
            case 2:
                ChangeDataBaseWindow changeDataBaseWindow = new ChangeDataBaseWindow(primaryStage);
                mainScene = new Scene(changeDataBaseWindow, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                break;
            default:
                return;
        }


        primaryStage.setScene(mainScene);
        primaryStage.setTitle(DEFAULT_TITLE);
        primaryStage.show();
    }
}
