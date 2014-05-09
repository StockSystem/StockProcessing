/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DataContainers;

/**
 *
 * @author kpriddy
 */
public class PointCalculationResult {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    
}
