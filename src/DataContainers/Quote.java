/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataContainers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.util.regex.Pattern;

/**
 *
 * @author kpriddy
 */
@DynamoDBTable(tableName="Stock")
public class Quote {
    private String stockname;
    private String date;
    private int year;
    private int day;
    private int month;
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;
    private double adj_Close;
    private Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}"); //yyyy-mm-dd
    
    public Quote() {
        super();
        stockname="badstock";
        date=null;
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
        this.date=date;
        setInternalDate(date);
        open=0;
        high=0;
        low=0;
        close=0;
        volume=0;
        adj_Close=0;
    }

    /*
    item.put("stockSymbol", new AttributeValue().withS(data.getName()));
    item.put("date", new AttributeValue().withS(data.getDates()[i]));
    item.put("open", new AttributeValue().withN(Double.toString(data.getOpens()[i])));
    item.put("high", new AttributeValue().withN(Double.toString(data.getHighs()[i])));
    item.put("low", new AttributeValue().withN(Double.toString(data.getLows()[i])));
    item.put("close", new AttributeValue().withN(Double.toString(data.getCloses()[i])));
    item.put("volume", new AttributeValue().withN(Integer.toString(data.getVolumes()[i])));
    item.put("adjclose", new AttributeValue().withN(Double.toString(data.getAdjCloses()[i])));
     */
    
    @DynamoDBHashKey(attributeName="stockSymbol") 
    public String getStockname() {
        return stockname;
    }

    public void setStockname(String stockname) {
        this.stockname = stockname;
    }
    
    
    @DynamoDBRangeKey(attributeName="date") 
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        setInternalDate(date);
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    private void setInternalDate(String dateString) {
        if (datePattern.matcher(dateString).matches()) {
            this.date=dateString;
        }       
        else {
            this.date=null;
            return;
        }  
        
        year = Integer.parseInt(dateString.substring(0, 4));
        month = Integer.parseInt(dateString.substring(5, 7));
        day = Integer.parseInt(dateString.substring(8));        
    } 
    
    @Override
    public String toString() {
        StringBuilder returnMe = new StringBuilder();
        returnMe.append(stockname);
        returnMe.append(" Date: ").append(date);
        returnMe.append("  Open: ").append(open);
        returnMe.append("  High: ").append(high);
        returnMe.append("  Low:").append(low);
        returnMe.append("  Close: ").append(close);
        returnMe.append("  Volume: ").append(volume);
        returnMe.append("  Adj_Close: ").append(adj_Close);
        return returnMe.toString();
    }
}
