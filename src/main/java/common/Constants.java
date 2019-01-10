package common;

import javafx.stage.Screen;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.logging.Logger;

public final class Constants {
    private final static Logger logger = Logger.getLogger(Constants.class.getName());


    public static final class ConfigFileConstants {
        public static final String CONFIG_FILENAME_SQLITE;
        public static final String URL;
        public static final String DEFAULT_PROPERTY_FILE = "config.properties";

        public static final String CONFIG_FILENAME_TRANSBASE;
        public static final String USER;
        public static final String PSW;


        static {
            Properties props = new Properties();
            try (FileInputStream input = new FileInputStream(DEFAULT_PROPERTY_FILE)) {
                props.load(input);

                CONFIG_FILENAME_SQLITE = props.getProperty("CONFIG_FILENAME_SQLITE");
                logger.info(String.format("Constant CONFIG_FILENAME_SQLITE was initialized as %s", CONFIG_FILENAME_SQLITE));

                URL = props.getProperty("URL");
                logger.info(String.format("Constant URL was initialized as %s", URL));

                CONFIG_FILENAME_TRANSBASE = props.getProperty("CONFIG_FILENAME_TRANSBASE");
                logger.info(String.format("Constant CONFIG_FILENAME_TRANSBASE was initialized as %s", CONFIG_FILENAME_TRANSBASE));

                USER = props.getProperty("USER");
                logger.info(String.format("Constant USER was initialized as %s", USER));

                PSW = props.getProperty("PSW");
                logger.info(String.format("Constant PSW was initialized as %s", PSW));
            } catch (IOException e) {
                throw new UncheckedIOException("Error while working with file - " + DEFAULT_PROPERTY_FILE, e);
            }
        }

        private ConfigFileConstants() {
            throw new AssertionError("Don't create instance of constant class");
        }
    }

    public static final class UserResolutionConstants {
        public static final double DEFAULT_USER_RESOLUTION_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
        public static final double DEFAULT_USER_RESOLUTION_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();

        private UserResolutionConstants() {
            throw new AssertionError("Don't create instance of constant class");
        }
    }

    public static final class TitleConstants {
        public static final String DEFAULT_TITLE = "TisDataBase";
        public static final String LOADING_TITLE = "Loading...";

        private TitleConstants() {
            throw new AssertionError("Don't create instance of constant class");
        }
    }


    public static final class TableConstants {
        public static final String ID_AXIS_NAME = "Id";
        public static final String NAME_AXIS_NAME = "Name";
        public static final String NUM_AXIS_NAME = "Num";

        private TableConstants() {
            throw new AssertionError("Don't create instance of constant class");
        }

    }

    public static final class Samples {
        public static final String SAMPLE1 = "";

        public static final String SAMPLE2 = "";

        public static final String SAMPLE3 = "";

        public static final String SAMPLE4 = "";

        public static final String SAMPLE50 = "";

        public static final String SAMPLE50BODY = "";

        public static final String SAMPLE5 = "";

        public static final String SAMPLE6 = "";

        public static final String SAMPLE7 = "";

        public static final String SAMPLE8 = "";

        public static final String SAMPLE9 = "";

        public static final String SAMPLE10 = "";

        public static final String SAMPLE10BODY = "";

        public static final String SAMPLE11 = "";

        public static final String SAMPLE11BODY = ", '%s.bin'";

        public static final String SAMPLE12 = "";

        public static final String SAMPLE12BODY = "";

        public static final String SAMPLE13 = "";

        public static final String SAMPLE14 = "";

        public static final String SAMPLE15 = "";

        public static final String SAMPLE16 = "";

        private Samples() {
            throw new AssertionError("Don't create instance of constant class");
        }
    }

    public static final class SqlSamples {
        public static final String SQLSAMPlE5 = "";

        public static final String SQLSAMPlE5TRUE = "";

        public static final String SQLSAMPLE5CHECK = "";

        public static final String SQLSAMPLE5CHECKBODY = "";

        public static final String SQLSAMPlE6 = "";

        public static final String SQLSAMPlE7 = "";

        public static final String SQLSAMPlE8 = "";

        public static final String SQLSAMPLE8FALSE = "";

        public static final String SQLSAMPLE9 = "";

        public static final String SQLSAMPLE9TRUE = "";

        public static final String SQLSAMPLE10BODY = "";

        public static final String SQLSAMPLE11 = "";

        public static final String SQLSAMPLE11BODY = ", '%s.bin'";

        public static final String SQLSAMPLE12 = "";

        public static final String SQLSAMPLE13 = "";

        public static final String SQLSAMPLE14 = "";

        public static final String SQLSAMPLE16P1 = "";

        public static final String SQLSAMPLE16P2 = "";

        private SqlSamples() {
            throw new AssertionError("Don't create instance of constant class");
        }
    }
}
