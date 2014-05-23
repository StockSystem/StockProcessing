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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        testlist.add("sma_10");
        testlist.add("sma_15");
        testlist.add("rsi_7");

        //create calculationManager 
       calculator = new CalculationManager();
       calculator.setStockname(stockname);
       //TODO: change this to specify a time range?
       String lastStockDate= dbConnection.getMostRecentEntryDate(stockname);
        
        //foreach test:
       for (String test : testlist) {
        //      check for latest date
           String currentTest = stockname + "_" + test;
           String lastTechDate=dbConnection.getMostRecentEntryDate(currentTest);
           if (lastTechDate ==null) {
               runTechUpdate(stockname, test, "1995-01-01");
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
        String[] dates = calculator.getStockData().getDates();
        
        for (int i=0 ; i<result.length ; i++) {
            DynamoDBTools.loadTechnicaltoDB(
                    new PointCalculationResult(stockname+"_"+test, 
                                                            dates[i],
                                                            result[i]));
         }
    }
    
    public void run() {
        ArrayList<String> stocklist = new ArrayList<>();
        stocklist.add("shld");
        
        for (String currentStock : stocklist) {
            run(currentStock);
        }
    }
    
       public static  void main(String[] args) {
        TechnicalDataUpdater sdu = new TechnicalDataUpdater();
                 //load list of stocks to run
        ArrayList<String> stocklist = new ArrayList<>();
        stocklist.add("shld");
       // stocklist.add("rgr");
        sdu.run();
    }
    
}
