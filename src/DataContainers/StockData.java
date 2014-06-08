/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataContainers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author kpriddy
 */
public class StockData {
    private String name;
    private List<Quote> candles;
    private boolean sorted = false;
    private Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}"); //yyyy-mm-dd
    
    public StockData() {
        candles = new ArrayList<>();
    }
    
    public StockData(String name) {
        this.name = name;
        candles = new ArrayList<>();
    }

   public int candleCount() {
       return candles.size();
   }
    
    public void addCandle(Quote lCandle) {
        candles.add(lCandle);
    }
    
    public void setCandles(List<Quote> inCandles) {
        this.candles = (List<Quote>) inCandles;
    }
    
    public void sortCandlesByDate() {
        sorted= true;
    }
    
    public List<Quote> getCandles() {
        if (!sorted) {
            sortCandlesByDate();
        }
        return candles;       
    }
    
    public List<Quote> getCandlesInReverseOrder() {        
        if (!sorted) {
            sortCandlesByDate();
        }
        //TODO: provide reverse functionality here.  Skipping as I don't thing
        // it is really needed.  Even if it is easy!
        
        return candles; 
    }
    
    public Quote getCandleByDate(String date) {

        if (datePattern.matcher(date).matches()) {
            for (Quote current : candles) {
                if (current.getTextDate().matches(date)) {
                    return current;
                }
            }
            System.err.println("Invalid date for " + name + " and date " + date);
            return null;
        }
        System.err.println("Invalid date format for " + name + " and date " + date);
        return null;
    }
    
    
    public String[] getDates() {
        String[] rOpens = new String[candles.size()];
        for (int i=0 ; i<rOpens.length ; i++) {
            rOpens[i] = candles.get(i).getTextDate();
        }
        return rOpens;
    }
    
    public double[] getDatesMillis() {
        double[] rOpens = new double[candles.size()];
        for (int i=0 ; i<rOpens.length ; i++) {
            rOpens[i] = candles.get(i).getDate();
        }
        return rOpens;
    }
    
    public double[] getOpens() {
        double[] rOpens = new double[candles.size()];
        for (int i=0 ; i<rOpens.length ; i++) {
            rOpens[i] = candles.get(i).getOpen();
        }
        return rOpens;
    }
    
    public double[] getCloses() {
        double[] rOpens = new double[candles.size()];
        for (int i=0 ; i<rOpens.length ; i++) {
            rOpens[i] = candles.get(i).getClose();
        }
        return rOpens;
    }
    
    public double[] getHighs() {
        double[] rOpens = new double[candles.size()];
        for (int i=0 ; i<rOpens.length ; i++) {
            rOpens[i] = candles.get(i).getHigh();
        }
        return rOpens;
    }
    
    public double[] getLows() {
        double[] rOpens = new double[candles.size()];
        for (int i=0 ; i<rOpens.length ; i++) {
            rOpens[i] = candles.get(i).getLow();
        }
        return rOpens;
    }
    
    public int[] getVolumes() {
        int[] rOpens = new int[candles.size()];
        for (int i=0 ; i<rOpens.length ; i++) {
            rOpens[i] = candles.get(i).getVolume();
        }
        return rOpens;
    }
    
    public double[] getAdjCloses() {
        double[] rOpens = new double[candles.size()];
        for (int i=0 ; i<rOpens.length ; i++) {
            rOpens[i] = candles.get(i).getAdj_Close();
        }
        return rOpens;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
