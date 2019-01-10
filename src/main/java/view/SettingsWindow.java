package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.EditingSettingCell;
import model.Settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Properties;

import static common.Constants.ConfigFileConstants.DEFAULT_PROPERTY_FILE;
import static common.Constants.TableConstants.NAME_AXIS_NAME;
import static common.Constants.UserResolutionConstants.DEFAULT_USER_RESOLUTION_HEIGHT;
import static common.Constants.UserResolutionConstants.DEFAULT_USER_RESOLUTION_WIDTH;

class SettingsWindow {
    private Stage settingsWindow;

    SettingsWindow(Stage stage) {
        settingsWindow = new Stage() {
            {
                setResizable(false);
                setTitle("Settings");
                initOwner(stage);
                setScene(new Scene(initSettings(), DEFAULT_USER_RESOLUTION_WIDTH * 0.3, DEFAULT_USER_RESOLUTION_HEIGHT * 0.3));
            }
        };
    }

    void showAndWait() {
        settingsWindow.initModality(Modality.APPLICATION_MODAL);
        settingsWindow.showAndWait();
    }

    private VBox initSettings() {
        VBox dataVBox = new VBox();

        ObservableList<Settings> dataList = FXCollections.observableArrayList();

        try (FileInputStream input = new FileInputStream(DEFAULT_PROPERTY_FILE)) {
            Properties props = new Properties();
            props.load(input);

            for (String prop : props.stringPropertyNames()) {
                dataList.add(new Settings(prop, props.getProperty(prop)));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error while working with file - " + DEFAULT_PROPERTY_FILE, e);
        }

        TableView<Settings> table = new TableView<>();
        table.setEditable(true);

        Callback<TableColumn<Settings, String>, TableCell<Settings, String>> cellFactory =
                param -> new EditingSettingCell();

        TableColumn<Settings, String> nameCol = new TableColumn<>(NAME_AXIS_NAME);
        nameCol.setEditable(false);

        nameCol.setCellValueFactory(new PropertyValueFactory<>(NAME_AXIS_NAME));
        nameCol.setCellFactory(cellFactory);


        TableColumn<Settings, String> valueCol = new TableColumn<>("Value");
        valueCol.setEditable(true);

        valueCol.setCellValueFactory(new PropertyValueFactory<>("Value"));
        valueCol.setCellFactory(cellFactory);
        valueCol.setOnEditCommit(
                event -> {
                    if (!Objects.equals(event.getNewValue(), event.getOldValue())) {
                        event.getTableView().getItems().get(event.getTablePosition().getRow()).setValue(event.getNewValue());
                    }
                }
        );

        table.setItems(dataList);
        table.getColumns().add(nameCol);
        table.getColumns().add(valueCol);

        nameCol.setSortable(false);
        valueCol.setSortable(false);

        final Button applyBtn = new Button("Apply");
        applyBtn.setOnMouseClicked(e -> {
            try (FileInputStream input = new FileInputStream(DEFAULT_PROPERTY_FILE)) {
                Properties props = new Properties();
                props.load(input);

                int i = 0;
                for (String prop : props.stringPropertyNames()) {
                    props.setProperty(prop, dataList.get(i++).getValue());
                }
                props.store(new FileOutputStream(DEFAULT_PROPERTY_FILE), null);
            } catch (IOException e1) {
                throw new UncheckedIOException("Error while working with file - " + DEFAULT_PROPERTY_FILE, e1);
            }
            settingsWindow.close();
        });

        dataVBox.setSpacing(5);
        dataVBox.setPadding(new Insets(10));
        dataVBox.getChildren().addAll(table, applyBtn);
        VBox.setVgrow(table, Priority.ALWAYS);

        table.setPrefWidth(nameCol.getPrefWidth() + valueCol.getPrefWidth());


        return dataVBox;
    }
}
