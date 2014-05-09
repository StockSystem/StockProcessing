/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package DataLoaders;

import DataContainers.Quote;
import DataContainers.StockData;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kpriddy
 */
public class DynamoDBTools {

    // JDBC driver name and database URL
  /*  String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://localhost/stockdata";

    //  Database credentials
    String USER = "kpriddy";
    String PASS = "1mpossibl#"; */
    
    static AmazonDynamoDBClient client;
    static String stockTable = "stockTable";

    public DynamoDBTools() {
        super();
        try {
            createClient();
        } catch (Exception ex) {
           // Logger.getLogger(DynamoDBTools.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Client connection error " + ex);
        }
    }
    
    private static void createClient() throws Exception {
        AWSCredentials credentials = new PropertiesCredentials(
                DynamoDBTools.class.getResourceAsStream("AWSCredentials.properties"));

        client = new AmazonDynamoDBClient(credentials);
        client.setRegion(Region.getRegion(Regions.US_WEST_2)); 
    }


    
 /*   public StockData readStockfromDB(String stockname) {
        StockData results = new StockData(stockname);

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
                local.setDate(rs.getString("date"));
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
    } */
    
    public void loadStocktoDB_lowlevel(StockData data) {
          
        int count = data.getDates().length;

        try {
            for (int i = 0; i < count; i++) {

                Map<String, AttributeValue> item = new HashMap<>();

                item.put("stockSymbol", new AttributeValue().withS(data.getName()));
                item.put("date", new AttributeValue().withS(data.getDates()[i]));
                item.put("open", new AttributeValue().withN(Double.toString(data.getOpens()[i])));
                item.put("high", new AttributeValue().withN(Double.toString(data.getHighs()[i])));
                item.put("low", new AttributeValue().withN(Double.toString(data.getLows()[i])));
                item.put("close", new AttributeValue().withN(Double.toString(data.getCloses()[i])));
                item.put("volume", new AttributeValue().withN(Integer.toString(data.getVolumes()[i])));
                item.put("adjclose", new AttributeValue().withN(Double.toString(data.getAdjCloses()[i])));

                PutItemRequest itemRequest = new PutItemRequest().withTableName("Stock").withItem(item);
                client.putItem(itemRequest);
                item.clear();
            }
        } catch (AmazonServiceException ase) {
            System.err.println("Failed to create item in " + data.getName() + " :" + ase);
        }

    }
    
    public void loadStocktoDB(StockData data) {
        
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        
        try {
            for (Quote current : data.getCandles()) {
                mapper.save(current);
            }
        } catch (AmazonServiceException ase) {
            System.err.println("Failed to create item in " + data.getName() + " :" + ase);
        }
    }
    
    public void readStockfromDB(String stockname) {

        DynamoDBMapper mapper = new DynamoDBMapper(client);
        
        try {
            
         Condition rangeKeyCondition = new Condition()
            .withComparisonOperator(ComparisonOperator.GT.toString())
            .withAttributeValueList(new AttributeValue().withS("2013-01-01"));

        Quote replyKey = new Quote();
        replyKey.setStockname(stockname);
        
        DynamoDBQueryExpression<Quote> queryExpression = new DynamoDBQueryExpression<Quote>()
            .withHashKeyValues(replyKey)
            .withRangeKeyCondition("date", rangeKeyCondition);

            
            List<Quote> fullList = mapper.query(Quote.class, queryExpression);
            
            for(Quote current : fullList) {
                System.out.println(current.toString());
            }
            
        } catch (AmazonServiceException ase) {
            Logger.getLogger(DynamoDBTools.class.getName()).log(Level.SEVERE, "Failed to fetch item in " + stockname, ase);
        }
    }
           
    public void loadStocktoDB(String stockname) {
        loadStocktoDB(YahooQuoteSource.fetchEOD(stockname));
    }
    
    public void dailyStockUpdatetoDB(String stockname) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
            cal.add(Calendar.DATE, -1);
            Date yesterday = cal.getTime();
            String startDate = format.format(yesterday);
            String endDate = format.format(today);

            StockData data = YahooQuoteSource.fetchEOD(stockname, startDate, endDate);
            if (data.candleCount() > 0) {
                loadStocktoDB(data);
                Logger.getLogger(DynamoDBTools.class.getName()).log(Level.INFO, "Updated records for {0}", stockname);
            } else {
                Logger.getLogger(DynamoDBTools.class.getName()).log(Level.WARNING, "No data update for: {0}", stockname);
            }
        } catch (Exception ex) {
            Logger.getLogger(DynamoDBTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        DynamoDBTools me = new DynamoDBTools();
        //me.loadStocktoDB("shld");
        me.dailyStockUpdatetoDB("shld");
        me.readStockfromDB("shld");
    }

}
