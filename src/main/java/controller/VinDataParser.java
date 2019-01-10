package controller;

import database.Sqlite;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static common.Constants.Samples;

public class VinDataParser {
    private final String VIN;
    private List<String> samples;
    private List<char[]> vinParseList;
    private Map<String, String> tisMap;
    private String controller;

    public List<char[]> getVinParseList() {
        return vinParseList;
    }

    VinDataParser(String vin, Map<String, String> tisMap, String controller) {
        VIN = vin;
        this.tisMap = tisMap;
        vinParseList = parseVin(VIN);
        this.controller = controller;
    }

    public VinDataParser(String vin) {
        VIN = vin;
        vinParseList = parseVin(VIN);
    }

    private static List<char[]> parseVin(String vinStr) {
        char[] vin = vinStr.toCharArray();
        if (vin.length != 17) {
            throw new RuntimeException("Invalid VIN!");
        }

        List<char[]> vinParseList = new ArrayList<>(6);

        vinParseList.add(vin);
        vinParseList.add(new char[3]);
        vinParseList.add(new char[5]);
        vinParseList.add(new char[1]);
        vinParseList.add(new char[1]);
        vinParseList.add(new char[1]);
        vinParseList.add(new char[6]);

        System.arraycopy(vin, 0, vinParseList.get(1), 0, 3);
        System.arraycopy(vin, 3, vinParseList.get(2), 0, 5);
        System.arraycopy(vin, 8, vinParseList.get(3), 0, 1);
        System.arraycopy(vin, 9, vinParseList.get(4), 0, 1);
        System.arraycopy(vin, 10, vinParseList.get(5), 0, 1);
        System.arraycopy(vin, 11, vinParseList.get(6), 0, 6);

        return vinParseList;
    }

    boolean writeToFile(String vin) throws IOException {
        if (!makeSamples()) {
            return false;
        }
        try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(String.format("%s_prog_VIN_%s.sql", controller, vin)), "windows-1251"))) {
            samples.forEach(line -> {
                try {
                    br.write(line);
                } catch (IOException e) {
                    System.err.println("Error writing to file");
                    throw new RuntimeException();
                }
            });
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Can't find file", e);
        } catch (IOException e) {
            System.err.println("IO error");
            throw e;
        }
        System.out.println("The file has been saved successfully.");
        return true;
    }

    private boolean makeSamples() {
        samples = new LinkedList<>();

        List<Integer> t5 = Sqlite.getT5(controller);
        if (t5 == null) {
            return false;
        }
        List<Integer> t10 = Sqlite.getT10(t5.get(0));
        if (t10 == null) {
            return false;
        }
        List<String> t9t12 = Sqlite.getT9T12(tisMap.get(String.valueOf(t10.get(0))));
        if (t9t12 == null) {
            return false;
        }

        samples.add(String.format(Samples.SAMPLE1, String.valueOf(vinParseList.get(0)),
                String.valueOf(vinParseList.get(1)), String.valueOf(vinParseList.get(2)),
                String.valueOf(vinParseList.get(3)), String.valueOf(vinParseList.get(4)),
                String.valueOf(vinParseList.get(5)), String.valueOf(vinParseList.get(6)),
                String.valueOf(vinParseList.get(1)), String.valueOf(vinParseList.get(2)),
                String.valueOf(vinParseList.get(3)), String.valueOf(vinParseList.get(4)),
                String.valueOf(vinParseList.get(5)), String.valueOf(vinParseList.get(6))));

        samples.add(String.format(Samples.SAMPLE2, String.valueOf(vinParseList.get(6))));

        samples.add(String.format(Samples.SAMPLE3, String.valueOf(vinParseList.get(1)),
                String.valueOf(vinParseList.get(2)), String.valueOf(vinParseList.get(3)),
                String.valueOf(vinParseList.get(4)), String.valueOf(vinParseList.get(5)),
                String.valueOf(vinParseList.get(1)), String.valueOf(vinParseList.get(2)),
                String.valueOf(vinParseList.get(3)), String.valueOf(vinParseList.get(4)),
                String.valueOf(vinParseList.get(5))));

        samples.add(String.format(Samples.SAMPLE4, String.valueOf(vinParseList.get(1)),
                String.valueOf(vinParseList.get(2)), String.valueOf(vinParseList.get(3)),
                String.valueOf(vinParseList.get(4)), String.valueOf(vinParseList.get(5)),
                String.valueOf(vinParseList.get(6))));

        int identType = t10.get(0) + 1;
        StringBuilder body = new StringBuilder("");
        for (; identType <= t10.get(1); identType++) {
            body.append(String.format(Samples.SAMPLE50BODY, identType, tisMap.get(String.valueOf(identType))));
        }
        samples.add(String.format(Samples.SAMPLE50, t10.get(0), tisMap.get(String.valueOf(t10.get(0))), body));

        samples.add(String.format(Samples.SAMPLE5, String.valueOf(vinParseList.get(1)),
                String.valueOf(vinParseList.get(2)), String.valueOf(vinParseList.get(3)),
                String.valueOf(vinParseList.get(4)), String.valueOf(vinParseList.get(5)),
                String.valueOf(vinParseList.get(6)), t5.get(0), t5.get(1)));

        samples.add(Samples.SAMPLE6);

        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyyMMdd");
        samples.add(String.format(Samples.SAMPLE7, formatForDateNow.format(dateNow)));

        samples.add(Samples.SAMPLE8);

        samples.add(String.format(Samples.SAMPLE9, t9t12.get(0)));

        identType = t10.get(0);
        body = new StringBuilder("");
        for (; identType <= t10.get(1); identType++) {
            body.append(String.format(Samples.SAMPLE10BODY, identType, tisMap.get(String.valueOf(identType))));
        }
        samples.add(String.format(Samples.SAMPLE10, body));

        identType = t10.get(0);
        body = new StringBuilder("");
        for (; identType <= t10.get(1); identType++) {
            if(!tisMap.get(String.valueOf(identType)).equals("*")) {
                body.append(String.format(Samples.SAMPLE11BODY, tisMap.get(String.valueOf(identType))));
            }
        }
        samples.add(String.format(Samples.SAMPLE11, t9t12.get(1), body));

        identType = t10.get(0);
        body = new StringBuilder(String.format(Samples.SAMPLE12BODY, t9t12.get(1), 0, 0, t9t12.get(1)));
        for (int j = 1; identType <= t10.get(1); j++, identType++) {
            if(!tisMap.get(String.valueOf(identType)).equals("*")) {
                body.append(String.format(Samples.SAMPLE12BODY, tisMap.get(String.valueOf(identType)), j, 1, tisMap.get(String.valueOf(identType))));
            }
        }
        samples.add(String.format(Samples.SAMPLE12, body));

        samples.add(Samples.SAMPLE13);

        samples.add(Samples.SAMPLE14);

        samples.add(Samples.SAMPLE15);

        samples.add(Samples.SAMPLE16);
        return true;
    }

}