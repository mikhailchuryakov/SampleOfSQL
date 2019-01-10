package model;

import javafx.beans.property.SimpleStringProperty;

public class Data {
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty num;

    public Data(String id, String name, String num) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.num = new SimpleStringProperty(num);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getNum() {
        return num.get();
    }

    public void setNum(String num) {
        this.num.set(num);
    }
}
