package utilities;

import static core.Main.DB_CONNECTION;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class SQLUtilities {
    public static final int TYPE_FARMER = 1;
    public static final int TYPE_MARKET = 2;
    public static final int TYPE_PRODUCT = 3;
    public static final int TYPE_PRODUCES = 4;
    public static final int TYPE_REGISTERS = 5;
    public static final int TYPE_BUYS = 6;

    public static void openDBConn() {
        try {
            Class.forName("org.sqlite.JDBC");
            DB_CONNECTION = DriverManager.getConnection("jdbc:sqlite:test.db");
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println("Error opening database.");
        }
    }

    private static ArrayList<String> getAllTableNames() throws SQLException{
            Statement stmt = DB_CONNECTION.createStatement();
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name <> 'sqlite_sequence';";
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<String> res = new ArrayList<>();
            if (rs != null) {
                while (rs.next()) {
                    res.add(rs.getString(1));
                }
                rs.getStatement().close();
                rs.close();
            }
        return  res;
    }
    public static HashMap<String,ResultSet> getAllTableContents() throws SQLException{
        HashMap<String,ResultSet> tables = new HashMap<>();
        //        ArrayList<ResultSet> tables = new ArrayList<>();
//        String[] tableNames = {"Addresses","Altitudes","Buys","Deposits","Farmer","Farmer_Emails","Farmer_Phones","LM_CreditCards","LM_Phones","Local_Market"
//        ,"Plant_Dates","Produces","Product","Registers","Website","Zips"};
        for(String table : getAllTableNames()) {
            tables.put(table,DB_CONNECTION.createStatement().executeQuery("SELECT * FROM " + table));
//            DB_CONNECTION.createStatement().executeUpdate("DELETE FROM " + table + " WHERE 1=1"); //TODO DELETE LATER!
        }
        return tables;
    }
    public static ResultSet getTable(int table){
        try{
            String selectSQL = "SELECT * FROM ";
            Statement selectStatement = DB_CONNECTION.createStatement();
            switch (table){
                case TYPE_FARMER: selectSQL = selectSQL + "Farmer";break;
                case TYPE_MARKET: selectSQL = selectSQL + "Local_Market";break;
                case TYPE_PRODUCT: selectSQL = selectSQL + "Product";break;
                case TYPE_REGISTERS: selectSQL = selectSQL + "Website";break;
            }
            return selectStatement.executeQuery(selectSQL);
        }catch (SQLException e){
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println("Error selecting the table");
            return null;
        }
    }

    private static int addEntryBase(int type,String line,Object...args) throws  SQLException{
        switch (type) {
            case TYPE_FARMER:
                return addFarmer(line);
            case TYPE_MARKET:
                return addMarket(line);
            case TYPE_PRODUCT:
                return addProduct(line);
            case TYPE_PRODUCES:
                return registerProduces(line);
            case TYPE_REGISTERS:
                return registerProduct(line, args);
            case TYPE_BUYS:
                return addBuy(line);
        }
        return 0;
    }
    public static int addEntry(int type, ArrayList<String> lines, Object... args) {
        int status;
        try {
            DB_CONNECTION.setAutoCommit(false);
            for (String line : lines) {
                status = addEntryBase(type,line,args);
                if(status == 0) throw new SQLException();
            }
            DB_CONNECTION.commit();
            DB_CONNECTION.setAutoCommit(true);
            return 1;
        } catch (SQLException e) {
            try {
                DB_CONNECTION.rollback();
                DB_CONNECTION.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println("Error inserting");
        }
        return 0;
    }
    static int addEntryCSV(int type,ArrayList<String> lines,Object...args){
        int status;
        try {
            DB_CONNECTION.setAutoCommit(false);
            for (String line : lines) {
                status = addEntryBase(type,line,args);
                if(status == 0) throw new SQLException();
            }
            return 1;
        } catch (SQLException e) {
            try {
                DB_CONNECTION.rollback();
                DB_CONNECTION.setAutoCommit(true);
                return 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println("Error inserting");
        }
        return 0;
    }
    static void CSVCommit(){
        try {
            DB_CONNECTION.commit();
            DB_CONNECTION.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int addEntry(int type, String line, Object...args) {
        ArrayList<String> arr = new ArrayList<>();
        arr.add(line);
        return addEntry(type, arr,args);
    }

    private static int addFarmer(String line) throws SQLException {
        String name;
        String last_name;
        String addr;
        int zip;
        String city;
        String[] phones, emails;

        try {
            String[] lineTokens = line.replaceAll("( , |, | ,)",",").split(",");
            name = lineTokens[0];
            last_name = lineTokens[1];
            addr = lineTokens[2];
            zip = Integer.parseInt(lineTokens[3]);
            city = lineTokens[4];
            phones = lineTokens[5].split("\\|");
            emails = lineTokens[6].split("\\|");
        }catch (Exception e){
            System.err.println("Error while parsing!");
            return 0;
        }

        String insertToFarmer = "INSERT INTO Farmer(name,last_name,address)VALUES(?,?,?);";
        String insertToAddresses = "INSERT OR IGNORE INTO Addresses(address, zip)VALUES(?,?);";
        String insertToZips = "INSERT OR IGNORE INTO Zips(zip, city)VALUES(?,?);";
        String insertToPhones = "INSERT INTO Farmer_Phones(phone, fid)VALUES(?,?)";
        String insertToEmails = "INSERT INTO Farmer_Emails(email, fid)VALUES(?,?)";

        String[] returnId = {"fid"};

        PreparedStatement farmerInsert = DB_CONNECTION.prepareStatement(insertToFarmer, returnId);
        farmerInsert.setString(1, name);
        farmerInsert.setString(2, last_name);
        farmerInsert.setString(3, addr);

        PreparedStatement addressInsert = DB_CONNECTION.prepareStatement(insertToAddresses);
        addressInsert.setString(1, addr);
        addressInsert.setInt(2, zip);

        PreparedStatement zipInsert = DB_CONNECTION.prepareStatement(insertToZips);
        zipInsert.setInt(1, zip);
        zipInsert.setString(2, city);

        zipInsert.executeUpdate();
        addressInsert.executeUpdate();
        farmerInsert.executeUpdate();

        ResultSet resultSet = farmerInsert.getGeneratedKeys();
        resultSet.next();
        int fid = resultSet.getInt(1);
        PreparedStatement phoneInsert = DB_CONNECTION.prepareStatement(insertToPhones);
        for (String phone : phones) {
            phoneInsert.setString(1, phone);
            phoneInsert.setInt(2, fid);
            phoneInsert.addBatch();
        }
        PreparedStatement emailInsert = DB_CONNECTION.prepareStatement(insertToEmails);
        for (String email : emails) {
            emailInsert.setString(1, email);
            emailInsert.setInt(2, fid);
            emailInsert.addBatch();
        }

        phoneInsert.executeBatch();
        emailInsert.executeBatch();


        System.out.println("Inserting with fid: " + fid);

        farmerInsert.close();
        addressInsert.close();
        zipInsert.close();
        phoneInsert.close();
        emailInsert.close();
        resultSet.close();
        return 1;
    }

    private static int addProduct(String line) throws SQLException {
        String name;
        String pdate;
        String hdate;
        int alt;
        int min_temp;
        int hardness_level;

        try {
            String[] lineTokens = line.replaceAll("( , |, | ,)",",").split(",");
            name = lineTokens[0];
            pdate = lineTokens[1];
            hdate = lineTokens[2];
            alt = Integer.parseInt(lineTokens[3]);
            min_temp = Integer.parseInt(lineTokens[4]);
            hardness_level = Integer.parseInt(lineTokens[5]);
        } catch (Exception e) {
            System.err.println("Error while parsing");
            return 0;
        }

        String insertToProduct = "INSERT INTO Product(name, hardness_level, plant_id, alt_id)VALUES(?,?,?,?);";
        String insertToPlantD = "INSERT INTO Plant_Dates(plant_date, harvest_date)VALUES(?,?);";
        String insertToAlt = "INSERT INTO Altitudes(altitude, min_temp)VALUES(?,?);";


        PreparedStatement plantDInsert = DB_CONNECTION.prepareStatement(insertToPlantD, Statement.RETURN_GENERATED_KEYS);
        plantDInsert.setInt(1, CommonUtilities.getMonthFromName(pdate)); //TODO Finish formatting the date.
        plantDInsert.setInt(2, CommonUtilities.getMonthFromName(hdate));

        PreparedStatement altInsert = DB_CONNECTION.prepareStatement(insertToAlt, Statement.RETURN_GENERATED_KEYS);
        altInsert.setInt(1, alt);
        altInsert.setInt(2, min_temp);

        altInsert.executeUpdate();
        plantDInsert.executeUpdate();

        ResultSet plantDateSet = plantDInsert.getGeneratedKeys();
        plantDateSet.next();
        int plant_id = plantDateSet.getInt(1);

        ResultSet altSet = altInsert.getGeneratedKeys();
        altSet.next();
        int alt_id = altSet.getInt(1);

        PreparedStatement productInsert = DB_CONNECTION.prepareStatement(insertToProduct, Statement.RETURN_GENERATED_KEYS);
        productInsert.setString(1, name);
        productInsert.setInt(2, hardness_level);
        productInsert.setInt(3, plant_id);
        productInsert.setInt(4, alt_id);

        productInsert.executeUpdate();

        ResultSet productSet = productInsert.getGeneratedKeys();
        productSet.next();
        int pid = productSet.getInt(1);


        System.out.println("Inserting with pid: " + pid);

        productInsert.close();
        plantDInsert.close();
        altInsert.close();
        plantDateSet.close();
        altSet.close();
        productSet.close();
        return 1;
    }

    private static int addMarket(String line) throws SQLException {
        String name;
        String addr;
        int zip;
        String city;
        String phone;
        double budget;

        try {
            String[] lineTokens = line.replaceAll("( , |, | ,)",",").split(",");
            name = lineTokens[0];
            addr = lineTokens[1];
            zip = Integer.parseInt(lineTokens[2]);
            city = lineTokens[3];
            phone = lineTokens[4];
            budget = Double.parseDouble(lineTokens[5]);
        } catch (Exception e) {
            System.err.println("Error while parsing");
            return 0;
        }

        String insertToMarket = "INSERT INTO Local_Market(budget, address, phone_number)VALUES(?,?,?);";
        String insertToAddresses = "INSERT OR IGNORE INTO Addresses(address, zip)VALUES(?,?);";
        String insertToZips = "INSERT OR IGNORE INTO Zips(zip, city)VALUES(?,?);";
        String insertToPhones = "INSERT INTO LM_Phones(phone_number, lm_name)VALUES(?,?)";


        PreparedStatement zipInsert = DB_CONNECTION.prepareStatement(insertToZips);
        zipInsert.setInt(1, zip);
        zipInsert.setString(2, city);
        zipInsert.executeUpdate();

        PreparedStatement addressInsert = DB_CONNECTION.prepareStatement(insertToAddresses);
        addressInsert.setString(1, addr);
        addressInsert.setInt(2, zip);
        addressInsert.executeUpdate();

        PreparedStatement phoneInsert = DB_CONNECTION.prepareStatement(insertToPhones);
        phoneInsert.setString(1, phone);
        phoneInsert.setString(2, name);
        phoneInsert.executeUpdate();

        PreparedStatement marketInsert = DB_CONNECTION.prepareStatement(insertToMarket, Statement.RETURN_GENERATED_KEYS);
        marketInsert.setDouble(1, budget);
        marketInsert.setString(2, addr);
        marketInsert.setString(3, phone);
        marketInsert.executeUpdate();

        ResultSet marketSet = marketInsert.getGeneratedKeys();
        marketSet.next();
        int lid = marketSet.getInt(1);


        System.out.println("Inserting with lid: " + lid);

        marketInsert.close();
        addressInsert.close();
        zipInsert.close();
        phoneInsert.close();
        marketSet.close();
        return 1;
    }

    private static int registerProduct(String line, Object... args) throws SQLException {
        String fname = null;
        String flast_name = null;
        String iban;
        String pname = null;
        double qty, price;
        int fid = 0;
        int pid = 0;
        try {
            String[] lineTokens = line.replaceAll("( , |, | ,)",",").split(",");
            if(args.length > 0){
                fid = (int) args[0];
                pid = (int) args[1];
                qty = Double.parseDouble(lineTokens[0]);
                price = Double.parseDouble(lineTokens[1]);
                iban = lineTokens[2];
            }else {
                fname = lineTokens[0];
                flast_name = lineTokens[1];
                pname = lineTokens[2];
                qty = Double.parseDouble(lineTokens[3]);
                price = Double.parseDouble(lineTokens[4]);
                iban = lineTokens[5];
            }
        } catch (Exception e) {
            System.err.println("Error while parsing");
            return 0;
        }
        String getFid = "SELECT f.fid FROM Farmer f WHERE f.name = \'" + fname + "\' AND f.last_name = \'" + flast_name + "\' ;";
        String getPid = "SELECT p.pid FROM Product p WHERE p.name = \'" + pname + "\' ;";
        String insertToRegisters = "INSERT INTO Registers (IBAN, fid, rid) VALUES (?,?,?) ;";
        String insertToWebsite = "INSERT INTO Website (qty, price, origin, pid, fid) VALUES (?,?,?,?,?);";
        String getFarmerCity = "SELECT Z.city from Zips Z,Farmer F,Addresses A WHERE F.address = A.address AND A.zip = Z.zip AND F.fid = ?";



        fid = (!(args.length >0)) ? getIDFromDB(getFid) : fid;
        pid = (!(args.length >0)) ? getIDFromDB(getPid) : pid;

        PreparedStatement getOriginStatement = DB_CONNECTION.prepareStatement(getFarmerCity);
        getOriginStatement.setInt(1, fid);
        ResultSet originSet = getOriginStatement.executeQuery();
        originSet.next();
        String origin = originSet.getString("city");

        PreparedStatement websiteInsert = DB_CONNECTION.prepareStatement(insertToWebsite, Statement.RETURN_GENERATED_KEYS);
        websiteInsert.setDouble(1, qty);
        websiteInsert.setDouble(2, price);
        websiteInsert.setString(3, origin);
        websiteInsert.setInt(4, pid);
        websiteInsert.setInt(5, fid);

        websiteInsert.executeUpdate();

        ResultSet ridSet = websiteInsert.getGeneratedKeys();
        ridSet.next();
        int rid = ridSet.getInt(1);

        PreparedStatement registerInsert = DB_CONNECTION.prepareStatement(insertToRegisters);
        registerInsert.setString(1, iban);
        registerInsert.setInt(2, fid);
        registerInsert.setInt(3, rid);

        registerInsert.executeUpdate();

        System.out.println("Inserting with rid:" + rid);

        getOriginStatement.close();
        originSet.close();
        websiteInsert.close();
        registerInsert.close();
        ridSet.close();
        return 1;
    }

    private static int getIDFromDB(String query) throws SQLException {
        Statement getID = DB_CONNECTION.createStatement();
        ResultSet idSet = getID.executeQuery(query);
        idSet.next();
        int id = idSet.getInt(1);
        getID.close();
        idSet.close();
        return id;
    }

    private static int registerProduces(String line) throws SQLException {
        String fname, flast_name, pname;
        double qty;
        int fid, pid, year;

        try {
            String[] lineTokens = line.replaceAll("( , |, | ,)",",").split(",");
            fname = lineTokens[0];
            flast_name = lineTokens[1];
            pname = lineTokens[2];
            qty = Double.parseDouble(lineTokens[3]);
            year = Integer.parseInt(lineTokens[4]);
        } catch (Exception e) {
            System.err.println("Error while parsing");
            return 0;
        }

        String getFid = "SELECT f.fid FROM Farmer f WHERE f.name = \'" + fname + "\' AND f.last_name = \'" + flast_name + "\' ;";
        String getPid = "SELECT p.pid FROM Product p WHERE p.name = \'" + pname + "\' ;";
        String insertProduces = "INSERT INTO Produces(qty, fid, pid, year) VALUES (?,?,?,?);";


        Statement fid_find = DB_CONNECTION.createStatement();
        ResultSet fidSet = fid_find.executeQuery(getFid);
        fidSet.next();
        fid = fidSet.getInt(1);

        Statement pid_find = DB_CONNECTION.createStatement();
        ResultSet pidSet = pid_find.executeQuery(getPid);
        pidSet.next();
        pid = pidSet.getInt(1);

        PreparedStatement producesInsert = DB_CONNECTION.prepareStatement(insertProduces, Statement.RETURN_GENERATED_KEYS);
        producesInsert.setDouble(1, qty);
        producesInsert.setInt(2, fid);
        producesInsert.setInt(3, pid);
        producesInsert.setInt(4, year);

        producesInsert.executeUpdate();

        ResultSet producesSet = producesInsert.getGeneratedKeys();
        producesSet.next();
        int pro_id = producesSet.getInt(1);

        System.out.println("Inserting with pro_id: " + pro_id);

        fid_find.close();
        fidSet.close();
        pid_find.close();
        pidSet.close();
        producesInsert.close();
        producesSet.close();
        return 1;
    }

    private static int addBuy(String line) throws SQLException {
        String fname, flast_name, pname, lm_name, lm_addr, cc;
        double qty;
        try {
            String[] lineTokens = line.replaceAll("( , |, | ,)",",").split(",");
            fname = lineTokens[0];
            flast_name = lineTokens[1];
            pname = lineTokens[2];
            lm_name = lineTokens[3];
            lm_addr = lineTokens[4];
            qty = Double.parseDouble(lineTokens[5]);
            cc = lineTokens[6];
        } catch (Exception e) {
            System.err.println("Error while parsing");
            return 0;
        }

        //language=SQLite
        String getRid = "SELECT W.rid,W.price FROM Website W,Farmer F,Product P WHERE f.name = \'" + fname + "\' AND f.last_name = \'" + flast_name + "\' AND P.name = '" + pname + "\' AND W.fid = F.fid AND w.pid = P.pid;";
        String getLid = "SELECT lm.lid FROM Local_Market lm, LM_Phones PH WHERE lm.phone_number = PH.phone_number AND PH.lm_name = \'" + lm_name + "\' AND lm.address = \'" + lm_addr + "\';";
        String insertToBuys = "INSERT INTO Buys(qty, rid, cc, price) VALUES (?,?,?,?);";
        String insertToCC = "INSERT OR IGNORE INTO LM_CreditCards(cc, lid) VALUES (?,?);";


        Statement ridStmt = DB_CONNECTION.createStatement();
        ResultSet ridSet = ridStmt.executeQuery(getRid);
        ridSet.next();
        int rid = ridSet.getInt(1);
        double price_ = ridSet.getDouble(2);

        Statement lidStmt = DB_CONNECTION.createStatement();
        ResultSet lidSet = lidStmt.executeQuery(getLid);
        lidSet.next();
        int lid = lidSet.getInt(1);

        PreparedStatement ccInsert = DB_CONNECTION.prepareStatement(insertToCC);
        ccInsert.setString(1, cc);
        ccInsert.setInt(2, lid);
        ccInsert.executeUpdate();

        PreparedStatement buysInsert = DB_CONNECTION.prepareStatement(insertToBuys, Statement.RETURN_GENERATED_KEYS);
        buysInsert.setDouble(1, qty);
        buysInsert.setInt(2, rid);
        buysInsert.setString(3, cc);
        buysInsert.setDouble(4, price_ * qty);
        buysInsert.executeUpdate();

        ResultSet buysSet = buysInsert.getGeneratedKeys();
        buysSet.next();
        int bid = buysSet.getInt(1);

        System.out.println("Inserting with bid: " + bid);

        ridStmt.close();
        ridSet.close();
        lidStmt.close();
        lidSet.close();
        ccInsert.close();
        buysInsert.close();
        buysSet.close();
        return 1;
    }



    public static void runQuery(int q) throws SQLException {
        String sql;
        Statement statement;
        ResultSet rs;
        switch (q) {
            case 1:
                sql = "SELECT P.name as product,F.name,F.last_name " +
                        "FROM Product P,Farmer F,(SELECT Pro.pid,MAX(Pro.qty) as max FROM Produces Pro GROUP BY Pro.pid) as maxs, Produces Pros " +
                        "WHERE F.fid = Pros.fid AND Pros.pid = maxs.pid AND Pros.qty = maxs.max AND p.pid = maxs.pid " +
                        "ORDER BY 1";
                statement = DB_CONNECTION.createStatement();
                rs = statement.executeQuery(sql);
                CommonUtilities.printRStoConsole(rs);
                break;
            case 2:
                sql = "SELECT P.name as product, F.name, F.last_name FROM Farmer F, Product P,(SELECT W.pid,MAX(W.qty) as max FROM Website W GROUP BY W.pid)as maxs,Website W2 " +
                        "WHERE P.pid = maxs.pid AND P.pid = W2.pid AND W2.fid = F.fid AND W2.qty = maxs.max " +
                        "ORDER BY 1";
                statement = DB_CONNECTION.createStatement();
                rs = statement.executeQuery(sql);
                CommonUtilities.printRStoConsole(rs);
                break;
            case 3:
                String sql1 = "DROP TABLE IF EXISTS temp_sums;";
                String sql2 = "CREATE TEMP TABLE temp_sums AS SELECT W2.fid,SUM(B2.price) as sum FROM Website W2,Buys B2 WHERE W2.rid = B2.rid GROUP BY W2.fid ORDER BY sum DESC;";
                //noinspection SqlResolve
                sql = "SELECT F.name,F.last_name FROM Farmer F," +
                        "(SELECT * FROM temp_sums LIMIT 1) as maxx,temp_sums SUMS " +
                        "WHERE F.fid = SUMS.fid AND sums.sum = maxx.sum";
                Statement s1 = DB_CONNECTION.createStatement();
                Statement s2 = DB_CONNECTION.createStatement();
                s1.executeUpdate(sql1);
                s2.executeUpdate(sql2);
                statement = DB_CONNECTION.createStatement();
                rs = statement.executeQuery(sql);
                CommonUtilities.printRStoConsole(rs);
                break;
            case 4:
                sql = "SELECT DISTINCT Z.city,LP.lm_name FROM LM_Phones LP,Local_Market LM,Addresses A,Zips Z," +
                        "(SELECT Z2.city,MAX(LM2.budget) as max FROM Zips Z2, Addresses A2,Local_Market LM2 WHERE LM2.address = A2.address AND A2.zip = Z2.zip                               GROUP BY Z2.city) as maxx " +
                        "WHERE LP.phone_number = LM.phone_number AND Z.city = maxx.city AND LM.budget = maxx.max";
                statement = DB_CONNECTION.createStatement();
                rs = statement.executeQuery(sql);
                CommonUtilities.printRStoConsole(rs);
                break;
            case 5:
                sql = "SELECT (COUNT(DISTINCT F.fid) + COUNT(DISTINCT LM.lid)) as totalUsers FROM Farmer F, Local_Market LM";
                statement = DB_CONNECTION.createStatement();
                rs = statement.executeQuery(sql);
                CommonUtilities.printRStoConsole(rs);
                break;
            default: break;
        }

    }
}

