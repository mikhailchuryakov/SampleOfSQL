package database;


import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static common.Constants.ConfigFileConstants.CONFIG_FILENAME_SQLITE;

public class Sqlite {
    private static Connection conn;
    private static Logger log = Logger.getLogger(Sqlite.class.getName());

    static {
        conn = connect();
    }

    private static Connection connect() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(CONFIG_FILENAME_SQLITE);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't connect to database");
            alert.showAndWait();
            throw new RuntimeException("Can't connect");
        }
        return conn;
    }

    public static List<Integer> getT5(String hidden) {
        String sql = "";
        List<Integer> t5 = new ArrayList<>();

        log.info(sql);
        try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
            ResultSet rs  = pstmt.executeQuery();
            log.info(String.format("Found: %s\t%s", rs.getInt(""), rs.getInt("")));
            while (rs.next()) {
                t5.add(rs.getInt(""));
                t5.add(rs.getInt(""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, String.format("There is no %s in column '' in table ''", hidden));
            alert.showAndWait();
            return null;
        }
        return t5;
    }

    public static List<String> getT9T12(String hidden) {
        String sql = "";
        List<String> t9t12 = new ArrayList<>();

        log.info(sql);
        try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
            ResultSet rs  = pstmt.executeQuery();
            log.info(String.format("Found: %s\t%s", rs.getString(""), rs.getString("")));
            while (rs.next()) {
                t9t12.add(rs.getString(""));
                t9t12.add(rs.getString(""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, String.format("There is no %s in column '' in table ''", hidden));
            alert.showAndWait();
            return null;
        }
        return t9t12;
    }

    public static List<Integer> getT10(int hidden) {
        String sql = "";
        List<Integer> t10 = new ArrayList<>();

        log.info(sql);
        try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
            ResultSet rs  = pstmt.executeQuery();
            log.info(String.format("Found: %s\t%s", rs.getInt(""), rs.getInt("")));
            while (rs.next()) {
                t10.add(rs.getInt(""));
                t10.add(rs.getInt(""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, String.format("There is no %s in column '' in table ''", hidden));
            alert.showAndWait();
            return null;
        }
        return t10;
    }
}
