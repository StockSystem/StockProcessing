/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TestApplications;

import DataContainers.PointCalculationResult;
import DataLoaders.DynamoDBTools;
import TechnicalCalculators.CalculationManager;
import TechnicalCalculators.CalculationResult;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kpriddy
 */
public class TechnicalDataUpdater {
    //DB values
    DynamoDBTools dbConnection;
    CalculationManager calculator;
            
            
    public TechnicalDataUpdater() {
        super();
        dbConnection = new DynamoDBTools();
    }
    
    public void run(String stockname) {

        ArrayList<String> testlist = new ArrayList<>();
        testlist.add("sma_5");
        testlist.add("sma_7");
        testlist.add("sma_10");
        testlist.add("sma_15");
        testlist.add("sma_20");
        testlist.add("sma_25");
        testlist.add("sma_50");
        testlist.add("rsi_5");
        testlist.add("rsi_7");
        testlist.add("rsi_10");
        testlist.add("rsi_15");
        testlist.add("rsi_20");
        testlist.add("rsi_25");
        testlist.add("rsi_50");
        testlist.add("williamsR_5");
        testlist.add("williamsR_7");
        testlist.add("williamsR_10");
        testlist.add("williamsR_15");
        testlist.add("williamsR_20");
        testlist.add("williamsR_25");
        testlist.add("williamsR_50");
        testlist.add("mfi_5");
        testlist.add("mfi_7");
        testlist.add("mfi_10");
        testlist.add("mfi_15");
        testlist.add("mfi_20");
        testlist.add("mfi_25");
        testlist.add("mfi_50");
        testlist.add("macd_5_20");
        testlist.add("macd_7_20");
        testlist.add("macd_10_20");
        testlist.add("macd_15_50");
        testlist.add("macd_20_50");
        testlist.add("macd_25_50");
        testlist.add("macd_50_100");
        testlist.add("macdindicator_5_20_3");
        testlist.add("macdindicator_7_20_3");
        testlist.add("macdindicator_10_20_3");
        testlist.add("macdindicator_15_50_3");
        testlist.add("macdindicator_20_50_3");
        testlist.add("macdindicator_25_50_3");
        testlist.add("macdindicator_50_100_3");

        //create calculationManager 
       calculator = new CalculationManager();
       calculator.setStockname(stockname);
       //TODO: change this to specify a time range?
       String lastStockDate= dbConnection.getMostRecentStockEntryDate(stockname);
       System.out.println("Last stock update: " + lastStockDate);
        
        //foreach test:
       for (String test : testlist) {
        //      check for latest date
           String currentTest = stockname + "_" + test;
           System.out.println(new Timestamp(System.currentTimeMillis()));
           System.out.println(" Running: " + currentTest);
           String lastTechDate=dbConnection.getMostRecentTechnicalEntryDate(currentTest);   //getMostRecentEntryDate(currentTest);
                System.out.println("Last technical update for " + currentTest + " = "+ lastTechDate);
           if (lastTechDate == null) {
               System.out.println("Last update was Empty.  Assuming no entries and running a full update.");
               runTechUpdate(stockname, test, "2010-01-01");
           } else
           if (lastTechDate.compareTo(lastStockDate) < 0) {
               runTechUpdate(stockname, test, lastTechDate);
           }
       }
    }
    
    private void runTechUpdate(String stockname, String test, String startdate) {
        //List<String> wordList = Arrays.asList(words); 
        List<String> testList = Arrays.asList(test.split("_"));
        CalculationResult cresults = calculator.parse(testList);
        double[] result = cresults.getCalcResults();
        long[] dates = calculator.getStockData().getDatesMillis();
        int stopIndex=0;
           
        //compare startdate to dates array and run add all entries from 0->startdate!
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long lastEntryMilliseconds = 0;
        try {
            Date date;
            date = sdf.parse(startdate);
            lastEntryMilliseconds = date.getTime();
        } catch (ParseException ex) {
            Logger.getLogger(TechnicalDataUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (int i=0 ; i<dates.length ; i++) {
            if (dates[i]<lastEntryMilliseconds) {
                System.out.println("Index: " + i + " date: " + dates[i] + " lastEntry: " + lastEntryMilliseconds);
                stopIndex=i;
                break;
            }
        }
        
        System.out.println(" Adding " + stopIndex + " entries");

        for (int i=0 ; i<stopIndex ; i++ ) { 
            DynamoDBTools.loadTechnicaltoDB(
                    new PointCalculationResult(stockname+"_"+test, 
                                                            dates[i],
                                                            result[i]));
         }
    }
    
    public void run() {
        ArrayList<String> stocklist = new ArrayList<>();
        stocklist.add("shld");
        stocklist.add("rgr");
        stocklist.add("acxm");
        
        for (String currentStock : stocklist) {
            run(currentStock);
        }
    }
    
       public static  void main(String[] args) {
        TechnicalDataUpdater sdu = new TechnicalDataUpdater();
        sdu.run();
    }
    
}
