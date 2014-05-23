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

/**
 *
 * @author kpriddy
 */
@DynamoDBTable(tableName="Technicals")
public class PointCalculationResult {
    String testname;
    String date;
    double value;
    
    public PointCalculationResult() {
        super();
        this.date="";
        this.value=0;
    }
    
    public PointCalculationResult(String date, double value) {
        super();
        this.date=date;
        this.value=value;
    }

    public PointCalculationResult(String testname,String date, double value) {
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
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDBAttribute(attributeName="value") 
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    
}
