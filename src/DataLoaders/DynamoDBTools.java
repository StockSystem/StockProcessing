/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package DataLoaders;

import DataContainers.PointCalculationResult;
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
    static DynamoDBMapper mapper;

    public DynamoDBTools() {
        super();
        try {
            createClient();
            mapper = new DynamoDBMapper(client);
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
        
        if (data.getCandles().size() <1  || data==null) 
            { return;}
        
        try {
            for (Quote current : data.getCandles()) {
                mapper.save(current);
            }
        } catch (AmazonServiceException ase) {
            System.err.println("Failed to create item in " + data.getName() + " :" + ase);
        }
    }
    
    public static void loadTechnicaltoDB(PointCalculationResult data) {
        
        try {
            
                mapper.save(data);
            
        } catch (AmazonServiceException ase) {
            System.err.println("Failed to create Technical entry: " + data.getTestname());
        }
    }
        
    public String getMostRecentStockEntryDate(String indexname) {
        
        try {
            Quote replyKey = new Quote();
            replyKey.setStockname(indexname);

            DynamoDBQueryExpression<Quote> queryExpression = new DynamoDBQueryExpression<Quote>()
                    .withHashKeyValues(replyKey)
                    .withScanIndexForward(false)
                    .withLimit(1);

            //List<Quote> fullList = mapper.query(Quote.class, queryExpression);
            List<Quote> fullList = mapper.queryPage(Quote.class, queryExpression).getResults();

            if (fullList.size() >0) {
                return fullList.get(0).getTextDate();
            } 
            return null;  
            
        } catch (AmazonServiceException ase) {
            Logger.getLogger(DynamoDBTools.class.getName()).log(Level.SEVERE, "Failed to fetch item in " + indexname);
            return null;
        }
    }
    
        public String getMostRecentTechnicalEntryDate(String indexname) {
        
        try {
            PointCalculationResult replyKey = new PointCalculationResult();
            replyKey.setTestname(indexname);

            DynamoDBQueryExpression<PointCalculationResult> queryExpression = new DynamoDBQueryExpression<PointCalculationResult>()
                    .withHashKeyValues(replyKey)
                    .withScanIndexForward(false)
                    .withLimit(1);

            //List<Quote> fullList = mapper.query(Quote.class, queryExpression);
            List<PointCalculationResult> fullList = mapper.queryPage(PointCalculationResult.class, queryExpression).getResults();

            if (fullList.size() >0) {
                return fullList.get(0).getTextDate();
            } 
            return null;  
            
        } catch (AmazonServiceException ase) {
            Logger.getLogger(DynamoDBTools.class.getName()).log(Level.SEVERE, "Failed to fetch item in " + indexname);
            return null;
        }
    }
     
    public List<Quote> readStockfromDB (String stockname) {
        return readStockfromDB(stockname, "1995-01-01");
    }
    
    public List<Quote> readStockfromDB(String stockname, String indate) {
        return readStockfromDB(stockname,java.sql.Date.valueOf(indate).getTime());
    }
    
    public List<Quote> readStockfromDB(String stockname, long startdate) {
        
        try {
            
         Condition rangeKeyCondition = new Condition()
            .withComparisonOperator(ComparisonOperator.GT.toString())
            .withAttributeValueList(new AttributeValue().withN(Long.toString(startdate)));

        Quote replyKey = new Quote();
        replyKey.setStockname(stockname);
        
        DynamoDBQueryExpression<Quote> queryExpression = new DynamoDBQueryExpression<Quote>()
            .withHashKeyValues(replyKey)
            .withRangeKeyCondition("date", rangeKeyCondition)
            .withScanIndexForward(false);

            
            List<Quote> fullList = mapper.query(Quote.class, queryExpression);
            
            return fullList;
            
        } catch (AmazonServiceException ase) {
            Logger.getLogger(DynamoDBTools.class.getName()).log(Level.SEVERE, "Failed to fetch item in " + stockname, ase);
        }
        return null;
    }
    
    
    public List<PointCalculationResult> readTechnicalfromDB (String stockname) {
        return readTechnicalfromDB(stockname, "1995-01-01");
    }
    
    public List<PointCalculationResult> readTechnicalfromDB (String stockname, String indate) {
        return readTechnicalfromDB(stockname, java.sql.Date.valueOf(indate).getTime());
    }
    
    public List<PointCalculationResult> readTechnicalfromDB(String testname, long startdate) {
        
        
        try {
            
         Condition rangeKeyCondition = new Condition()
            .withComparisonOperator(ComparisonOperator.GT.toString())
            .withAttributeValueList(new AttributeValue().withN(Long.toString(startdate)));

        PointCalculationResult replyKey = new PointCalculationResult();
        replyKey.setTestname(testname);
        
        DynamoDBQueryExpression<PointCalculationResult> queryExpression = new DynamoDBQueryExpression<PointCalculationResult>()
            .withHashKeyValues(replyKey)
            .withRangeKeyCondition("date", rangeKeyCondition)
            .withScanIndexForward(false);

            
            List<PointCalculationResult> fullList = mapper.query(PointCalculationResult.class, queryExpression);
            
            return fullList;
            
        } catch (AmazonServiceException ase) {
            Logger.getLogger(DynamoDBTools.class.getName()).log(Level.SEVERE, "Failed to fetch item in " + testname, ase);
        }
        return null;
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
