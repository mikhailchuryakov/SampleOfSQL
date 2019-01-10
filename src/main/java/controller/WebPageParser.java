package controller;

import database.Sqlite;
import database.Transbase;
import view.EnterVinWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Data;
import model.EditingDataCell;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

import static common.Constants.ConfigFileConstants.URL;
import static common.Constants.TableConstants.*;
import static common.Constants.TitleConstants.DEFAULT_TITLE;
import static common.Constants.TitleConstants.LOADING_TITLE;

public class WebPageParser extends BorderPane {
    private String mode;
    private String vin;
    private String userAgent;
    private Map<String, String> loginCookies;
    private Connection.Response res;
    private Document doc;
    private Stage stage;
    private String controller;
    private boolean isChangeDBMode = false;

    public WebPageParser(Stage stage) throws IOException {
        this.stage = stage;

        EnterVinWindow vinWindow = new EnterVinWindow();
        vinWindow.showAndWait();
        this.vin = vinWindow.getVinStr();
        if (vin == null) {
            stage.close();
            System.exit(0);
        }
        getSoftwareKeys();

    }

    public WebPageParser(Stage stage, String vin, Boolean isChangeDBMode) throws IOException {
        this.stage = stage;
        this.vin = vin;
        this.isChangeDBMode = isChangeDBMode;
        getSoftwareKeys();
    }


    private void getSoftwareKeys() throws IOException {
        stage.show();
        stage.setTitle(LOADING_TITLE);
        userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0";

        res = Jsoup
                .connect(URL)
                .userAgent(userAgent)
                .method(Connection.Method.POST).execute();


        doc = res.parse();
        loginCookies = res.cookies();

        //parse main page for log
        Elements target = doc.getElementsByAttributeValue("method", "POST");
        String targetStr = target.get(0).attr("id");

        Elements bm = doc.getElementsByAttributeValue("type", "button");
        String[] split = bm.get(0).attr("onClick").split("'");
        String bmStr = split[1];

        Elements body = doc.getElementsByAttributeValue("type", "text");
        String fBody = body.get(0).attr("name");
        String sBody = body.get(1).attr("name");

        String url = String.format("%s?target=%s&target.method=onSubmit&%s=1&bm=%s#%s",
                URL, targetStr, bmStr, bmStr, bmStr);

        //log
        res = Jsoup
                .connect(url)
                .userAgent(userAgent)
                .cookies(loginCookies)
                .data(fBody, vin)
                .data(sBody, "")
                .execute();

        doc = res.parse();

        //checkAndAdd2to4 "Salesmake" page
        if (!doc.getElementsContainingOwnText("Salesmake:").isEmpty()) {
            res = Jsoup
                    .connect(getUrl(doc))
                    .userAgent(userAgent)
                    .cookies(loginCookies)
                    .data(getBody(doc), "1")
                    .execute();

            doc = res.parse();
        }

        choosingMode();
    }

    private void choosingMode() {
        if (!doc.getElementsByClass("summarytable").isEmpty()) {
            stage.setTitle(LOADING_TITLE);
            getData();
            return;
        }

        VBox box = new VBox();
        box.getChildren().clear();
        Elements elements = doc.getElementsByTag("option");

        ListView<String> list = new ListView<>();

        Button next = new Button("Next");
        ObservableList<String> moduleList = FXCollections.observableArrayList();
        for (Element element : elements) {
            moduleList.add(element.text());
        }

        box.getChildren().addAll(list, next);
        list.setItems(moduleList);

        VBox.setVgrow(list, Priority.ALWAYS);

        setCenter(box);
        stage.setTitle(DEFAULT_TITLE);

        next.setOnMouseClicked(e -> {
            mode = list.getSelectionModel().getSelectedItem();
            for (Element elem : doc.getElementsByTag("b")) {
                if (elem.toString().equals("<b>Controller:</b>")) {
                    controller = mode.split(" ")[0];
                }
            }
            //choosing mode
            stage.setTitle(LOADING_TITLE);
            try {
                res = Jsoup
                        .connect(getUrl(doc))
                        .userAgent(userAgent)
                        .cookies(loginCookies)
                        .data(getBody(doc), getValueForBodyByText(doc, mode))
                        .execute();
                doc = res.parse();
            } catch (IOException e1) {
                throw new UncheckedIOException(e1);
            }

            choosingMode();
        });

    }

    private void getData() {
        VBox dataVBox = new VBox();
        setCenter(dataVBox);
        // parse
        Elements data = doc.getElementsByAttributeValue("class", "att_table");
        // Elements names = data.select("th");
        Elements numbers = data.select("td");

        Map<String, String> tisMap = new HashMap<>();

        ObservableList<Data> dataList = FXCollections.observableArrayList();
        int length = numbers.size();
        try {
            int identType = Objects.requireNonNull(Sqlite.getT10(Objects.requireNonNull(Sqlite.getT5(controller)).get(0))).get(0);
            for (int i = 0, j = 0; i < length; i += 3, j++) {
                dataList.add(new Data(String.valueOf(identType + j), numbers.get(i).text(), numbers.get(i + 1).text()));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "There is no row in table '?'. Please add it and press OK");
            alert.showAndWait();
            choosingMode();
            return;
        }

        TableView<Data> table = createTable(dataList);

        final Button resultButton = new Button() {
            {
                if (isChangeDBMode) {
                    setText("Next");
                } else {
                    setText("Make file");
                }
            }
        };

        resultButton.setOnMouseClicked(e -> {
            if (isChangeDBMode) {

                Transbase transbase = new Transbase(vin, dataList, controller);
                List<CheckBox> lstCB = new LinkedList<>();
                CheckBox cb1 = new CheckBox("Check VIN and add (1-4)");
                CheckBox cb2 = new CheckBox("Check and add configuration (5-7)");
                CheckBox cb3 = new CheckBox("Check HWID (8-9)");
                CheckBox cb4 = new CheckBox("Insert TIS (10)");
                CheckBox cb5 = new CheckBox("Check and add module in base (11-14)");
                CheckBox cb6 = new CheckBox("Last insert");

                lstCB.add(cb1);
                lstCB.add(cb2);
                lstCB.add(cb3);
                lstCB.add(cb4);
                lstCB.add(cb5);
                lstCB.add(cb6);

                Button btn = new Button("Check");
                btn.setPadding(new Insets(5));

                Button enable = new Button("Enable");
                enable.setPadding(new Insets(5));
                enable.setDisable(true);

                btn.setOnMouseClicked(event -> {
                    if (cb1.isSelected()) {
                        transbase.checkVinInBase();
                        cb1.setDisable(true);
                    }
                    if (cb2.isSelected()) {
                        transbase.check5();
                        cb2.setDisable(true);
                    }
                    if (cb3.isSelected()) {
                        transbase.check8();
                        cb3.setDisable(true);
                    }
                    if (cb4.isSelected()) {
                        transbase.insert10();
                        cb4.setDisable(true);
                    }
                    if (cb5.isSelected()) {
                        transbase.check11();
                        cb5.setDisable(true);
                    }
                    if (cb6.isSelected()) {
                        transbase.lastInsert();
                        cb6.setDisable(true);
                    }
                    enable.setDisable(false);
                });

                enable.setOnMouseClicked(event -> {
                    lstCB.forEach(cb -> cb.setDisable(false));
                    enable.setDisable(true);
                });

                VBox vBox = new VBox();
                lstCB.forEach(cb -> {
                    cb.setPadding(new Insets(5));
                    vBox.getChildren().add(cb);
                });
                vBox.getChildren().add(btn);
                vBox.getChildren().add(enable);
                setCenter(vBox);

            } else {
                for (Data dataSample : dataList) {
                    tisMap.put(dataSample.getId(), dataSample.getNum());
                }
//                for (int i = 0; i < length; i++) {
//                    tisMap.put(dataList.get(i).getId(), dataList.get(i).getNum());
//                }
                VinDataParser vinDataParser = new VinDataParser(vin, tisMap, controller);
                try {
                    if (vinDataParser.writeToFile(vin)) {
                        dataVBox.getChildren().set(1, new Label("File has successfully saved."));
                    }
                } catch (IOException e1) {
                    throw new UncheckedIOException(e1);
                }
            }

        });

        dataVBox.setSpacing(5);
        dataVBox.setPadding(new Insets(10));
        dataVBox.getChildren().addAll(table, resultButton);
        VBox.setVgrow(table, Priority.ALWAYS);

        setCenter(dataVBox);
        stage.setTitle(DEFAULT_TITLE);
    }

    private String getUrl(Document doc) {
        Elements target = doc.getElementsByAttributeValue("method", "post");
        String targetStr = target.get(0).attr("id");

        Elements bm = doc.getElementsByAttributeValue("type", "button");
        String[] split = bm.get(1).attr("onClick").split("'");
        String bmStr = split[1];

        return String.format("%s%s%s", URL, targetStr, bmStr); // hidden url
    }

    private String getBody(Document doc) {
        Elements body = doc.getElementsByTag("select");
        return body.get(0).attr("id");
    }

    private String getValueForBodyByText(Document doc, String text) {
        Elements elements = doc.getElementsContainingOwnText(text);
        if (text.equals("Radio")) {
            for (Element element : elements) {
                if (element.text().split(" ")[1].equals(text)) {
                    return element.attr("value");
                }
            }
        }
        return elements.attr("value");
    }

    private TableView<Data> createTable(ObservableList<Data> dataList) {
        TableView<Data> table = new TableView<>();
        table.setEditable(true);

        Callback<TableColumn<Data, String>, TableCell<Data, String>> cellFactory =
                param -> new EditingDataCell();

        TableColumn<Data, String> idCol = new TableColumn<>(ID_AXIS_NAME);
        idCol.setEditable(false);

        idCol.setCellValueFactory(new PropertyValueFactory<>(ID_AXIS_NAME));
        idCol.setCellFactory(cellFactory);

        TableColumn<Data, String> nameCol = new TableColumn<>(NAME_AXIS_NAME);
        nameCol.setEditable(false);

        nameCol.setCellValueFactory(new PropertyValueFactory<>(NAME_AXIS_NAME));
        nameCol.setCellFactory(cellFactory);


        TableColumn<Data, String> numCol = new TableColumn<>(NUM_AXIS_NAME);
        numCol.setEditable(true);

        numCol.setCellValueFactory(new PropertyValueFactory<>(NUM_AXIS_NAME));
        numCol.setCellFactory(cellFactory);
        numCol.setOnEditCommit(
                event -> {
                    if (!Objects.equals(event.getNewValue(), event.getOldValue())) {
                        event.getTableView().getItems().get(event.getTablePosition().getRow()).setNum(event.getNewValue());
                    }
                }
        );

        table.setItems(dataList);
        table.getColumns().add(nameCol);
        table.getColumns().add(idCol);
        table.getColumns().add(numCol);

        idCol.setSortable(false);
        nameCol.setSortable(false);
        numCol.setSortable(false);

        final TextField addId = new TextField();
        addId.setPromptText(ID_AXIS_NAME);
        addId.setMaxWidth(idCol.getPrefWidth());
        final TextField addName = new TextField();
        addName.setPromptText(NAME_AXIS_NAME);
        addName.setMaxWidth(nameCol.getPrefWidth());
        final TextField addNum = new TextField();
        addNum.setMaxWidth(numCol.getPrefWidth());
        addNum.setPromptText(NUM_AXIS_NAME);

        table.setPrefWidth(nameCol.getPrefWidth() + idCol.getPrefWidth() + numCol.getPrefWidth());

        return table;
    }
}
