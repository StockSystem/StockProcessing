/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TestApplications;

import DataLoaders.DynamoDBTools;
import java.util.ArrayList;

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
    
    private static ArrayList<String> generateStockList() {
        
        ArrayList<String> stocklist = new ArrayList<>();
        stocklist.add("shld");
        stocklist.add("rgr");
        stocklist.add("acxm");
        
        return stocklist;
    }
    
    public void run(ArrayList<String> stocklist) {
        //load list of stocks to run
        
       for (String stock : stocklist) {
            dbConnection.dailyStockUpdatetoDB(stock);
        }
        
        
    }
    
    public static  void main(String[] args) {
        StockDataUpdater sdu = new StockDataUpdater();
        sdu.run(generateStockList());
    }
    
}
