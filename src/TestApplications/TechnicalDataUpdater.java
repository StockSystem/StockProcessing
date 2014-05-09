/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TestApplications;

import DataContainers.TechnicalResults;
import DataLoaders.SQLDBTools;
import TechnicalCalculators.CalculationManager;
import TechnicalCalculators.CalculationResult;
import java.util.ArrayList;

/**
 *
 * @author kpriddy
 */
public class TechnicalDataUpdater {
    //DB values
    SQLDBTools dbConnection;
            
            
    public TechnicalDataUpdater() {
        super();
        dbConnection = new SQLDBTools();
    }
    
    public void run() {
        //load list of stocks to run
        ArrayList<String> stocklist = new ArrayList<>();
        stocklist.add("shld");
       // stocklist.add("rgr");
        ArrayList<String> testlist = new ArrayList<>();
        testlist.add("sma");
        String[] standardParameters = {"40"};
        
        
       CalculationManager calculator = new CalculationManager();
       for (String stock : stocklist) {
           calculator.setStockname(stock);
           ArrayList<String> testString = new ArrayList();
          for(String test : testlist) {
           //run calculations and load to db
              testString.add(test);
              for (String current : standardParameters) {
                  testString.add(current);
                  String techtable=stock +"_" +test;
                  CalculationResult myResult = calculator.parse(testString);
                  System.out.println(myResult.getDefinition());
                  double[] output = myResult.getCalcResults(0);
                  String[] dates = calculator.getStockData().getDates();
                  TechnicalResults techResults = new TechnicalResults(myResult.getDefinition());
                  
                  for (int i=0 ; i<output.length ; i++) {
                      techResults.addResult(dates[i], output[i]);
                  }
                  dbConnection.loadTechDatatoDB(techtable,techResults);
                  testString.remove(testString.size()-1);
              }
              testString.remove(testString.size()-1);
              
            //dbConnection.loadStocktoDB(stock);
          }
        }
        
        
    }
    
    public static  void main(String[] args) {
        TechnicalDataUpdater sdu = new TechnicalDataUpdater();
        sdu.run();
    }
    
}
