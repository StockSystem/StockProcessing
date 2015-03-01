/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
@DynamoDBTable(tableName="Technicals")
public class PointCalculationResult {
    String testname;
    double date;
    double value;
    String textDate;
    
    public PointCalculationResult() {
        super();
        this.date=0;
        this.value=0;
    }
    
    public PointCalculationResult(double date, double value) {
        super();
        this.date=date;
        this.value=value;
    }

    public PointCalculationResult(String testname,double date, double value) {
        super();
        this.testname = testname;
        this.date=date;
        this.value=value;
    }
    
     @DynamoDBHashKey(attributeName="testname")
    public String getTestname() {
        return testname;
    }

    public void setTestname(String testname) {
        this.testname = testname;
    }

    @DynamoDBRangeKey(attributeName="date")
    public double getDate() {
        return date;
    }
    
    @DynamoDBAttribute(attributeName="textDate") 
    public String getTextDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        Date interim = new Date((long) date);
        return sdf.format(interim);
    }

    public void setDate(double date) {
        this.date = date;
    }
    
    public void setTextDate(String date) {
        this.textDate = date;
        this.date = Date.valueOf(date).getTime();
    }

    @DynamoDBAttribute(attributeName="value") 
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    
}
