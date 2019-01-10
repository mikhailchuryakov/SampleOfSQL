package model;

import javafx.beans.property.SimpleStringProperty;

public class Settings {
    private final SimpleStringProperty name;
    private final SimpleStringProperty value;

    public Settings(String name, String num) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleStringProperty(num);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
