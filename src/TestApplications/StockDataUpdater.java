/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TestApplications;

import DataLoaders.DynamoDBTools;
import DataLoaders.YahooQuoteSource;
import TechnicalCalculators.CalculationManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kpriddy
 */
public class StockDataUpdater {
    //DB values
    //SQLDBTools dbConnection;
    DynamoDBTools dbConnection;
            
            
    public StockDataUpdater() {
        super();
        dbConnection = new DynamoDBTools();
    }
    
    private static List<String> generateStockList() {
        
        List<String> stocklist = new ArrayList<>();
        stocklist.add("shld");
        stocklist.add("rgr");
        stocklist.add("acxm");
        
        return stocklist;
    }
    
  private String dateAdjuster(String currentDate, int adjustment) {
        try {
           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
           Date adjustedDate = df.parse(currentDate);
           Calendar c = Calendar.getInstance();
           c.setTime(adjustedDate);
           c.add(Calendar.DATE, adjustment);
           
           return df.format(c.getTime());
           
            
        } catch (ParseException ex) {
            Logger.getLogger(CalculationManager.class.getName()).log(Level.SEVERE, null, ex);
        }

       return null;
    }
    
    public void run(List<String> stocklist) {
        //load list of stocks to run

        for (String stock : stocklist) {
            //get last date from database
            String date = dbConnection.getMostRecentEntryDate(stock);
            if (date == null) {
                Logger.getLogger(StockDataUpdater.class.getName())
                        .log(Level.INFO, "Full Load of: " + stock);
                dbConnection.loadStocktoDB(stock);
            } else {
                try {
                    Logger.getLogger(StockDataUpdater.class.getName())
                            .log(Level.INFO, "Updating " + stock + " since: " + date);
                    dbConnection.loadStocktoDB(
                            YahooQuoteSource.fetchEOD(stock,
                                    dateAdjuster(date, 1), //Add a day to the last entry
                                    new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
                } catch (Exception ex) {
                    Logger.getLogger(StockDataUpdater.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
    
    public static  void main(String[] args) {
        StockDataUpdater sdu = new StockDataUpdater();
        sdu.run(generateStockList());
    }
    
}
