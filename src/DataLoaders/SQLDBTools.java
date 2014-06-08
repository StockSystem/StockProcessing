/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package DataLoaders;

import DataContainers.PointCalculationResult;
import DataContainers.TechnicalResults;
import DataContainers.StockData;
import DataContainers.Quote;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kpriddy
 */
public class SQLDBTools {

    // JDBC driver name and database URL
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://localhost/stockdata";

    //  Database credentials
    String USER = "kpriddy";
    String PASS = "1mpossibl#";

    public SQLDBTools() {
        super();
    }

    public String getDB_URL() {
        return DB_URL;
    }

    public void setDB_URL(String DB_URL) {
        this.DB_URL = DB_URL;
    }

    public void setDB_NAME(String DB_NAME) {
        this.DB_URL="jdbc:mysql://localhost/" + DB_NAME;
    }
    
    public StockData readStockfromDB(String stockname) {
        StockData results = new StockData(stockname);
        Connection conn = null;
        Statement stmt = null;
         try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER).newInstance();

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            
            String fetchSql = "select * from " + stockname  + " order by date desc";
            ResultSet rs = stmt.executeQuery(fetchSql);
            while (rs.next()) {
                Quote local = new Quote();
                local.setTextDate(rs.getString("date"));
                local.setOpen(rs.getDouble("open"));
                local.setHigh(rs.getDouble("high"));
                local.setLow(rs.getDouble("low"));
                local.setClose(rs.getDouble("close"));
                local.setVolume(rs.getInt("volume"));
                local.setAdj_Close(rs.getDouble("adj_close"));    
                results.addCandle(local);
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        
        return results;
    }
    
    public void loadStocktoDB(StockData data) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER).newInstance();

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            
            //Make sure stock table exists, if not create it.
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet tableList = metadata.getTables(null, null, data.getName(), null);
            if (!tableList.next()) {
                System.out.println("Creating table: " + data.getName());
                String tableCreate = "CREATE TABLE " +data.getName() + " (date DATE, PRIMARY KEY(date), open FLOAT, high FLOAT, low FLOAT, close FLOAT, volume INT, adj_close FLOAT)";
                stmt.executeUpdate(tableCreate);
            }
            
            //set the starting date to pull data from the table.  
            // If it is an empty table, then set the date to 
            String mdate = "";
            String dbDate = "select max(date) from " + data.getName();
            try (ResultSet rs = stmt.executeQuery(dbDate)) {
                if (rs.next()) {
                    mdate = rs.getString("max(date)");
                } 
            }
            if (mdate==null) { mdate="1995-02-02";}
            /*
            Made a "well duh" error here.  Make sure any default date selected is an actual Market date!!
            For example, 2000-01-01 is NOT valid and will not match the string below!!!
            ----- I Should have written this to pull the oldest date (probably the last entry) from the Stockdata list! -------
            */
            
            String[] dates = data.getDates();
            int count=0;
            while (count <dates.length-1 && !mdate.contentEquals(dates[count]) ) {
                count++;
            }
            System.out.println("Match: " + dates[count]);
            // String sql = "SELECT * FROM hal";
            // "INSERT INTO $tableName (date, open, high, low, close, volume, adj_close)  VALUES ('$open', '$high', '$low', '$close', '$volume', '$adj_close')"
            //int dLength = data.getCloses().length;
            for (int i = 0; i < count; i++) {
                String sql = "INSERT INTO "
                        + data.getName()
                        + " (date, open, high, low, close, volume, adj_close)  VALUES ('"
                        + data.getDates()[i] + "','"
                        + data.getOpens()[i] + "','"
                        + data.getHighs()[i] + "','"
                        + data.getLows()[i] + "','"
                        + data.getCloses()[i] + "','"
                        + data.getVolumes()[i] + "','"
                        + data.getAdjCloses()[i] + "')";
                System.out.println(sql);

                stmt.executeUpdate(sql);
            }

        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException se) {
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }//end finally try
        }//end try

    }

        public void loadTechDatatoDB(String table, TechnicalResults data) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER).newInstance();

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            
            //Make sure stock table exists, if not create it.
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet tableList = metadata.getTables(null, null, table, null);
            if (!tableList.next()) {
                System.out.println("Creating table: " + data.getName());
                String tableCreate = "CREATE TABLE " +data.getName() + " (date DATE, PRIMARY KEY(date), open FLOAT, high FLOAT, low FLOAT, close FLOAT, volume INT, adj_close FLOAT)";
                stmt.executeUpdate(tableCreate);
            }
            
            //set the starting date to pull data from the table.  
            // If it is an empty table, then set the date to 
            String mdate = "";
            String dbDate = "select max(date) from " + data.getName();
            try (ResultSet rs = stmt.executeQuery(dbDate)) {
                if (rs.next()) {
                    mdate = rs.getString("max(date)");
                } 
            }
            if (mdate==null) { mdate="1995-02-02";}
            /*
            Made a "well duh" error here.  Make sure any default date selected is an actual Market date!!
            For example, 2000-01-01 is NOT valid and will not match the string below!!!
            ----- I Should have written this to pull the oldest date (probably the last entry) from the Stockdata list! -------
            */
            
            List<PointCalculationResult> realdata = data.getResults();
            int count=0;
            while (count <realdata.size()-1 && !mdate.contentEquals(realdata.get(count).getDate()) ) {
                count++;
            }
            System.out.println("Match: " + realdata.get(count).getDate());
            // String sql = "SELECT * FROM hal";
            // "INSERT INTO $tableName (date, open, high, low, close, volume, adj_close)  VALUES ('$open', '$high', '$low', '$close', '$volume', '$adj_close')"
            //int dLength = data.getCloses().length;
            for (int i = 0; i < count; i++) {
                String sql = "INSERT INTO "
                        + table 
                        + " (date, " + data.getName()+ ")  VALUES ('"
                        + realdata.get(i).getDate() + "','"
                        + realdata.get(i).getValue() + "')";
                System.out.println(sql);

                stmt.executeUpdate(sql);
            }

        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException se) {
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }//end finally try
        }//end try

    }
    public void loadStocktoDB(String stockname) {
        loadStocktoDB(YahooQuoteSource.fetchEOD(stockname));
    }

    public static void main(String[] args) {
        SQLDBTools me = new SQLDBTools();
        me.loadStocktoDB("acxm");
        me.readStockfromDB("acxm");
    }

}
