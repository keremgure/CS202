package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.CommonUtilities;
import utilities.SQLUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main extends Application {
    public static Connection DB_CONNECTION = null;
    public static Stage MAIN_STAGE = null;

    public static void main(String[] args) {
        Platform.setImplicitExit(false);
        SQLUtilities.openDBConn();
        Thread t = cmdListen();
        t.start();
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../gui/fxml/Main.fxml"));
        primaryStage.setIconified(true);
        primaryStage.setTitle("CS202 DB GUI");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        MAIN_STAGE = primaryStage;

    }

    @SuppressWarnings("DuplicateExpressions")
    private static Thread cmdListen() {
        return new Thread(() -> {
            printUsage();
            Scanner scanner = new Scanner(System.in);
            for (; ; ) {
                String line = scanner.nextLine();

                if ("SHOW TABLES".equals(line)) {
                    HashMap<String,ResultSet> tables = null;
                    try {
                        tables = SQLUtilities.getAllTableContents();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (tables != null) {

                        for (Map.Entry<String, ResultSet> entry : tables.entrySet()) {
                            System.out.printf("\t\t\t##########  %s  ##########\n\n",entry.getKey());
                            CommonUtilities.printRStoConsole(entry.getValue());
                        }
                    }

                } else if (line.matches("QUERY \\d+")) {
                    Pattern p = Pattern.compile("\\d+");
                    Matcher m = p.matcher(line);
                    System.out.println(m.find());
                    int d = Integer.parseInt(m.group(0));
                    System.out.println(d);
                    try {
                        SQLUtilities.runQuery(d);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else if ("LOAD DATA".equals(line))
                    CommonUtilities.parseCSVs();
                else {

                    int start = line.indexOf("(");
                    int end = line.lastIndexOf(")");
//                String data = line.substring(start+1,end);
                    String data;
                    if (start == -1 || end == -1) {
                        System.err.println("Wrong Command!");
                        printUsage();
                        continue;
                    } else {
                        data = line.substring(start + 1, end);
                    }

                    if (line.matches("ADD FARMER\\(.*\\)"))
                        SQLUtilities.addEntry(SQLUtilities.TYPE_FARMER, data);
                    else if (line.matches("ADD PRODUCT\\(.*\\)"))
                        SQLUtilities.addEntry(SQLUtilities.TYPE_PRODUCT, data);
                    else if (line.matches("ADD MARKET\\(.*\\)"))
                        SQLUtilities.addEntry(SQLUtilities.TYPE_MARKET, data);
                    else if (line.matches("REGISTER PRODUCT\\(.*\\)"))
                        SQLUtilities.addEntry(SQLUtilities.TYPE_REGISTERS, data);
                    else if (line.matches("ADD FARMERs\\(.*\\)"))
                        SQLUtilities.addEntry(SQLUtilities.TYPE_FARMER, new ArrayList<>(Arrays.asList(data.replace("(", "").replace(")", "").split(":"))));
                    else if (line.matches("ADD PRODUCTs\\(.*\\)"))
                        SQLUtilities.addEntry(SQLUtilities.TYPE_PRODUCT, new ArrayList<>(Arrays.asList(data.replace("(", "").replace(")", "").split(":"))));
                    else if (line.matches("ADD MARKETs\\(.*\\)"))
                        SQLUtilities.addEntry(SQLUtilities.TYPE_MARKET, new ArrayList<>(Arrays.asList(data.replace("(", "").replace(")", "").split(":"))));
                    else if (line.matches("REGISTER PRODUCTs\\(.*\\)"))
                        SQLUtilities.addEntry(SQLUtilities.TYPE_REGISTERS, new ArrayList<>(Arrays.asList(data.replace("(", "").replace(")", "").split(":"))));
                    else {
                        System.err.println("Wrong Command!");
                        printUsage();
                    }
                }
            }
        });
    }

    private static void printUsage() {
        System.out.println("Supported Commands: SHOW TABLES | LOAD DATA | QUERY # | ADD FARMER(...) |"
                + " ADD PRODUCT(...) | ADD MARKET() | REGISTER PRODUCT(...) | ADD FARMERs(...) |"
                + " ADD PRODUCTs(...) | ADD MARKETs() | REGISTER PRODUCTs(...)");
    }


}
