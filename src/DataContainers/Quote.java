/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataContainers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author kpriddy
 */
@DynamoDBTable(tableName="Stock")
public class Quote {
    private String stockname;
    private long date;
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;
    private double adj_Close;
    private String textDate;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
    
    public Quote() {
        super();
        stockname="badstock";
        date=0;
        textDate=null;
        open=0;
        high=0;
        low=0;
        close=0;
        volume=0;
        adj_Close=0;
    }
    
    Quote(String stockname, String date) {
        super();
        this.stockname = stockname;
        this.textDate=date;
        open=0;
        high=0;
        low=0;
        close=0;
        volume=0;
        adj_Close=0;
    }

  
    @DynamoDBHashKey(attributeName="stockSymbol") 
    public String getStockname() {
        return stockname;
    }

    public void setStockname(String stockname) {
        this.stockname = stockname;
    }
    
    @DynamoDBRangeKey(attributeName="date") 
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
        Date interim = new Date((long) date);
        textDate = sdf.format(interim);
    }
    
    
    @DynamoDBAttribute(attributeName="textDate") 
    public String getTextDate() {
        return textDate;
    }

    public void setTextDate(String date) {
        this.textDate = date;
        this.date = Date.valueOf(date).getTime();
    }
    
    @DynamoDBAttribute(attributeName="open") 
    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    @DynamoDBAttribute(attributeName="high") 
    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    @DynamoDBAttribute(attributeName="low") 
    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    @DynamoDBAttribute(attributeName="close") 
    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    @DynamoDBAttribute(attributeName="volume") 
    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @DynamoDBAttribute(attributeName="adjclose") 
    public double getAdj_Close() {
        return adj_Close;
    }

    public void setAdj_Close(double adjClose) {
        this.adj_Close = adjClose;
    }

    @Override
    public String toString() {
        StringBuilder returnMe = new StringBuilder();
        returnMe.append(stockname);
        returnMe.append(" Date: ").append(textDate);
        returnMe.append(" Date(ms): ").append(date);
        returnMe.append("  Open: ").append(open);
        returnMe.append("  High: ").append(high);
        returnMe.append("  Low:").append(low);
        returnMe.append("  Close: ").append(close);
        returnMe.append("  Volume: ").append(volume);
        returnMe.append("  Adj_Close: ").append(adj_Close);
        return returnMe.toString();
    }
}
