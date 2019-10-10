package utilities;

import core.Main;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import org.sqlite.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public abstract class CommonUtilities {

    static int getMonthFromName(String month) {
        switch (month) {
            case "January": return 1;
            case "February": return 2;
            case "March": return 3;
            case "April": return 4;
            case "May": return 5;
            case "June": return 6;
            case "July": return 7;
            case "August": return 8;
            case "September": return 9;
            case "October": return 10;
            case "November": return 11;
            case "December": return 12;
        }
        return 0;
    }

    public static void parseCSVs() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select the directory where the .csv files are stored at.");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            final CountDownLatch latch = new CountDownLatch(1);

            AtomicReference<File> dir = new AtomicReference<>();//Use Atomic variable in order to interact within the threads.
            Platform.runLater(() -> {
                        dir.set(directoryChooser.showDialog(Main.MAIN_STAGE));
                        latch.countDown();//trigger latch to continue execution.
                        Main.MAIN_STAGE.setIconified(true);//Minimize stage to focus on console again.
                    });
            latch.await();//wait until latch is triggered.
            System.out.println(dir.get().getAbsolutePath());
            String absPath = dir.get().getAbsolutePath();
            File farmers = new File(absPath+"/farmers.csv");
            File markets = new File(absPath+"/markets.csv");
            File products = new File(absPath+"/products.csv");
            File produces = new File(absPath+"/produces.csv");
            File registers = new File(absPath+"/registers.csv");
            File buys = new File(absPath+"/buys.csv");

            CsvReader csvReader = new CsvReader();
            csvReader.setContainsHeader(true);
            csvReader.setFieldSeparator(';');
            ArrayList<String> farmerList = new ArrayList<>();
            CsvContainer csvFarmers = csvReader.read(farmers, StandardCharsets.UTF_8);
            int status;
            for (CsvRow row : csvFarmers.getRows()) {
                String tmp = StringUtils.join(row.getFields(), ",");
                farmerList.add(tmp);
            }
            status = SQLUtilities.addEntryCSV(SQLUtilities.TYPE_FARMER, farmerList);
            if(status == 0)return;

            ArrayList<String> marketList = new ArrayList<>();
            CsvContainer csvMarkets = csvReader.read(markets, StandardCharsets.UTF_8);
            for (CsvRow row : csvMarkets.getRows()) {
                String tmp = StringUtils.join(row.getFields(), ",");
                marketList.add(tmp);
            }
            status = SQLUtilities.addEntryCSV(SQLUtilities.TYPE_MARKET, marketList);
            if(status == 0)return;

            ArrayList<String> productList = new ArrayList<>();
            CsvContainer csvProducts = csvReader.read(products, StandardCharsets.UTF_8);
            for (CsvRow row : csvProducts.getRows()) {
                String tmp = StringUtils.join(row.getFields(), ",");
                productList.add(tmp);
            }
            status = SQLUtilities.addEntryCSV(SQLUtilities.TYPE_PRODUCT, productList);
            if(status == 0)return;

            ArrayList<String> producesList = new ArrayList<>();
            CsvContainer csvProduces = csvReader.read(produces, StandardCharsets.UTF_8);
            for (CsvRow row : csvProduces.getRows()) {
                String tmp = StringUtils.join(row.getFields(), ",");
                producesList.add(tmp);
            }
            SQLUtilities.addEntryCSV(SQLUtilities.TYPE_PRODUCES, producesList);

            ArrayList<String> registerList = new ArrayList<>();
            CsvContainer csvRegisters = csvReader.read(registers, StandardCharsets.UTF_8);
            for (CsvRow row : csvRegisters.getRows()) {
                String tmp = StringUtils.join(row.getFields(), ",");
                registerList.add(tmp);
            }
            status = SQLUtilities.addEntryCSV(SQLUtilities.TYPE_REGISTERS, registerList);
            if(status == 0)return;

            ArrayList<String> buyList = new ArrayList<>();
            CsvContainer csvBuys = csvReader.read(buys, StandardCharsets.UTF_8);
            for (CsvRow row : csvBuys.getRows()) {
                String tmp = StringUtils.join(row.getFields(), ",");
                buyList.add(tmp);
            }
            status = SQLUtilities.addEntryCSV(SQLUtilities.TYPE_BUYS, buyList);
            if(status == 0)return;

            SQLUtilities.CSVCommit();
//        for(String h : csvFarmers.getHeader()){
//            System.out.print(h + "\t");
//        }
//        System.out.print("\n");
//        for(CsvRow row : csvFarmers.getRows()){
//            String tmp = "";
//            for(String h : csvFarmers.getHeader()){
//                System.out.print(row.getField(h) + "\t");
//
//            }
//            System.out.print("\n");
//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printTableToConsole(int type) {
        ResultSet resultSet = SQLUtilities.getTable(type);
        printHandler(resultSet);
    }
    public static void printRStoConsole(ResultSet rs){
        printHandler(rs);
    }
    private static void printHandler(ResultSet resultSet){
        try {
            int columnCount = resultSet.getMetaData().getColumnCount();
            ArrayList<String> headers = new ArrayList<>();
            List<List<String>> data = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                headers.add(resultSet.getMetaData().getColumnName(i+1));
            }
            while(resultSet.next()){
                ArrayList<String> tmp = new ArrayList<>();
                for (int i = 0; i < columnCount; i++) {
                    tmp.add(resultSet.getString(i+1));
                }
                data.add(tmp);
            }
            resultSet.close();

            Board board = new Board(75);
            Table table = new Table(board, 75, headers, data);
            table.setColAlignsList(Collections.nCopies(columnCount,Block.DATA_CENTER));
            Block tableBlock = table.tableToBlocks();
            board.setInitialBlock(tableBlock);
            board.build();
            String tableString = board.getPreview();
            System.out.println(tableString);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
