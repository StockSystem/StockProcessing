
package DataLoaders;

import DataContainers.Quote;
import DataContainers.StockData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class YahooQuoteSource {

       // Yahoo URL
    private final static String YAHOO_URL = "http://ichart.finance.yahoo.com/table.csv?s={0}&d={1}&e={2}&f={3}&g=d&a={4}&b={5}&c={6}&ignore=.csv";


    public YahooQuoteSource() {
    }

   public static final StockData fetchEOD(String stock) {
        try {
            return fetchEOD(stock,"1995-01-01", "2015-12-31");
        } catch (Exception ex) {
            Logger.getLogger(YahooQuoteSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
   
      public static final StockData fetchEOD(String stock, Boolean DB) {
        if(!DB) {
            try {
            return fetchEOD(stock,"1995-01-01", "2015-12-31");
        } catch (Exception ex) {
            Logger.getLogger(YahooQuoteSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        } else {
            SQLDBTools fetcher = new SQLDBTools();
            return fetcher.readStockfromDB(stock);
        }
        
        return null;
   }
    public static final StockData fetchEOD(String stock, String startDate, String endDate) throws Exception {
        
        StockData stockData;
        
        stockData = new StockData(stock);
        // parameters

        /*
         String beginYear = String.valueOf(Integer.parseInt(startDate.substring(0,4)));
         String beginMonth = String.valueOf(Integer.parseInt(startDate.substring(5,7)));
         String beginDay = String.valueOf(Integer.parseInt(startDate.substring(8)));
        
         String year = String.valueOf(Integer.parseInt(endDate.substring(0,4)));
         String month = String.valueOf(Integer.parseInt(endDate.substring(5,7)));
         String day = String.valueOf(Integer.parseInt(endDate.substring(8)));
         */
        String beginYear = String.valueOf(Integer.parseInt(startDate.substring(0, 4)));
        String beginMonth = String.valueOf(Integer.parseInt(startDate.substring(5, 7)) - 1);
        String beginDay = String.valueOf(Integer.parseInt(startDate.substring(8)));
        
        String year = String.valueOf(Integer.parseInt(endDate.substring(0, 4)));
        String month = String.valueOf(Integer.parseInt(endDate.substring(5, 7)) - 1);
        String day = String.valueOf(Integer.parseInt(endDate.substring(8)));
        // open url
        String urlStr = MessageFormat.format(
                YAHOO_URL,
                stock,
                month,
                day,
                year,
                beginMonth,
                beginDay,
                beginYear);
        
        URL url = new URL(urlStr);
       // URL url = new URL("http://ichart.finance.yahoo.com/table.csv?s=YHOO&d=6&e=26&f=2013&g=d&a=3&b=12&c=1996&ignore=.csv");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();  //throw out header line
            Quote currentQuote;
            while ((line = reader.readLine()) != null) {
                currentQuote = new Quote();
                StringTokenizer str = new StringTokenizer(line, ",");
                currentQuote.setStockname(stock);
                currentQuote.setDate(str.nextToken());
                // get data
                currentQuote.setOpen(Double.parseDouble(str.nextToken()));
                currentQuote.setHigh(Double.parseDouble(str.nextToken()));
                currentQuote.setLow(Double.parseDouble(str.nextToken()));
                currentQuote.setClose(Double.parseDouble(str.nextToken()));
                currentQuote.setVolume(Integer.parseInt(str.nextToken()));
                currentQuote.setAdj_Close(Double.parseDouble(str.nextToken()));
                
                stockData.addCandle(currentQuote);
            }
        } catch (IOException iOException) {
            Logger.getLogger(DynamoDBTools.class.getName()).log(
                    Level.INFO, "Data retrieval error from Yahoo.  Probably requested dates outside of their range. ", iOException);
        } catch (NumberFormatException numberFormatException) {
        }
        
        System.out.println("Fetched " + stockData.getCandles().size() + " quotes for " + stock);
        
        return stockData;
    }

    public static void main(String [] args) {
        StockData test = null;
        try {
            test=fetchEOD("yhoo","2015-07-15", "2015-07-24");
        } catch (Exception ex) {
            Logger.getLogger(YahooQuoteSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(Quote current : test.getCandles()) {
            System.out.println(current.toString());
        }
    }
}