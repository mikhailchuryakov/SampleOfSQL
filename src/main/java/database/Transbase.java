package database;

import controller.VinDataParser;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Data;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static common.Constants.ConfigFileConstants.CONFIG_FILENAME_TRANSBASE;
import static common.Constants.ConfigFileConstants.PSW;
import static common.Constants.ConfigFileConstants.USER;
import static common.Constants.SqlSamples.*;

public class Transbase {
    private Connection conn;
    private String url = CONFIG_FILENAME_TRANSBASE;
    private String user = USER;
    private String psw = PSW;
    private List<char[]> vinParseList;
    private ObservableList<Data> dataList;
    private String controller;

    private static Logger log = Logger.getLogger(Transbase.class.getName());

    static {
        try {
            FileHandler handler = new FileHandler("./log", 500000, 4, true);
            handler.setFormatter(new java.util.logging.Formatter() {
                public String format(java.util.logging.LogRecord record) {
                    java.util.Formatter formatter = new java.util.Formatter();
                    formatter.format("%07d:%2$tH.%2$tM.%2$tS.%tL:%07d:%s.%s:%s",
                            record.getSequenceNumber(),
                            record.getMillis(),
                            record.getThreadID(),
                            record.getSourceClassName(),
                            record.getSourceMethodName(),
                            record.getMessage());
                    Throwable thrown = record.getThrown();
                    if (thrown != null) {
                        StringWriter sw = new StringWriter();
                        thrown.printStackTrace(new PrintWriter(sw));
                        formatter.format("%s", sw.toString());
                    } else {
                        formatter.format("%n");
                    }
                    return formatter.toString();
                }
            });
            log.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Transbase(String vin, ObservableList<Data> dataList, String controller) {
        conn = connect();
        VinDataParser parseVin = new VinDataParser(vin);
        vinParseList = parseVin.getVinParseList();
        this.dataList = dataList;
        this.controller = controller;
    }

    private Connection connect() {
        try {
            Class.forName("transbase.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, psw);
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't connect to database");
            alert.showAndWait();
            throw new RuntimeException(e);
        }
        return conn;
    }

    public void checkVinInBase() {
        String sql = "";
        log.info(sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            log.info(String.format("Found: %d rows", rs.getRow()));
            if (!rs.next()) {
                log.info(String.format("VIN %s is absent", Arrays.toString(vinParseList.get(0))));
                checkAndAdd2to4();
            } else {
                log.info("Go to 5 step");
                //check5();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, String.format("There is no %s", ""));
            alert.showAndWait();
        }
    }

    private void checkAndAdd2to4() {
        String sql = "";
        log.info(sql);
        try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt2.executeQuery();
            log.info(String.format("Found: %d rows", rs.getRow()));
            if (!rs.next()) {
                sql = "";
                log.info(sql);
                try (PreparedStatement pstmt21 = conn.prepareStatement(sql)) {
                    pstmt21.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "";
        log.info(sql);
        try (PreparedStatement pstmt3 = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt3.executeQuery();
            log.info(String.format("Found: %d rows", rs.getRow()));
            if (!rs.next()) {
                sql = "";
                log.info(sql);
                try (PreparedStatement pstmt31 = conn.prepareStatement(sql)) {
                    pstmt31.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "";
        log.info(sql);
        try (PreparedStatement pstmt4 = conn.prepareStatement(sql)) {
            pstmt4.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void check5() {
        List<Integer> t10 = Objects.requireNonNull(Sqlite.getT10(Objects.requireNonNull(Sqlite.getT5(controller)).get(0)));
        int identType = t10.get(0) + 1;
        StringBuilder body = new StringBuilder();
        for (int j = 1; identType <= t10.get(1); j++, identType++) {
            body.append(String.format(SQLSAMPLE5CHECKBODY, identType, dataList.get(j).getNum()));
        }
        String sql = "";
        log.info(sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            log.info(String.format("Found: %d rows", rs.getRow()));
            if (!rs.next()) {
                List<Integer> t5 = Sqlite.getT5(controller);
                sql = "";
                log.info(sql);
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.executeUpdate();
                    sql = SQLSAMPlE6;
                    log.info(sql);
                    try (PreparedStatement pstmt4 = conn.prepareStatement(sql)) {
                        pstmt4.executeUpdate();
                        sql = SQLSAMPlE7;
                        log.info(sql);
                        try (PreparedStatement pstmt3 = conn.prepareStatement(sql)) {
                            pstmt3.executeUpdate();
                            check8();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String ecuid = rs.getString("ECUID");
                List<Integer> t5 = Sqlite.getT5(controller);
                sql = "";
                log.info(sql);
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void check8() {
        String sql = SQLSAMPlE8;
        log.info(sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> t9t12 = Sqlite.getT9T12(dataList.get(0).getNum());

        sql = "";
        log.info(sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            log.info(String.format("Found: %d rows", rs.getRow()));
            if (!rs.next()) {
                sql = "";
                log.info(sql);
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.executeUpdate();
                    sql = "";
                    log.info(sql);
                    try (PreparedStatement pstmt3 = conn.prepareStatement(sql)) {
                        pstmt3.executeUpdate();
                    }
                }
            } else {
                String hwid = rs.getString("HWID");
                sql = "";
                log.info(sql);
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert10() {
        List<Integer> t10 = Objects.requireNonNull(Sqlite.getT10(Objects.requireNonNull(Sqlite.getT5(controller)).get(0)));
        int identType = t10.get(0);
        for (int j = 0; identType <= t10.get(1); j++, identType++) {
            String sql = "";
            log.info(sql);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void check11() {
        List<Integer> t10 = Objects.requireNonNull(Sqlite.getT10(Objects.requireNonNull(Sqlite.getT5(controller)).get(0)));
        List<String> t9t12 = Sqlite.getT9T12(dataList.get(0).getNum());

        int identType = t10.get(0);
        StringBuilder body = new StringBuilder("");
        for (int j = 0; identType <= t10.get(1); j++, identType++) {
            if (!dataList.get(j).getNum().equals("*")) {
                body.append(String.format(SQLSAMPLE11BODY, dataList.get(j).getNum()));
            }
        }
        String sql = "";
        log.info(sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            log.info(String.format("Found: %d rows", rs.getRow()));
            rs.next();
            String current = rs.getString("MODULENAME");
            if (String.format("%s.bin", t9t12.get(1)).equals(current)) {
                check11true(0, 0, t9t12.get(1));
            } else {
                check11false(0, 0, t9t12.get(1));
            }
            int i = 1;
            for (Data data : dataList) {
                if (String.format("%s.bin", data.getNum()).equals(current)) {
                    log.info(String.format("%s is present", data.getNum()));
                    check11true(i, 1, data.getNum());
                    rs.next();
                } else {
                    log.info(String.format("%s is absent", data.getNum()));
                    check11false(i, 1, data.getNum());
                }
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void check11true(int i, int j, String modulename) {
        String sql = "";
        log.info(sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void check11false(int i, int j, String modulename) {
        String sql = "";
        log.info(sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            //todo add blob info
            sql = "";
            log.info(sql);
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                pstmt2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void lastInsert() {
        String sql = SQLSAMPLE16P1;
        log.info(sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            sql = SQLSAMPLE16P2;
            log.info(sql);
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                pstmt2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}