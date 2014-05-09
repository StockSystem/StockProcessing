/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataContainers;

import java.util.ArrayList;

/**
 *
 * @author kpriddy
 */
public class TechnicalResults {
    private String name;
    private ArrayList<PointCalculationResult> results;
    
    public TechnicalResults() {
        results = new ArrayList<>();
    }
    
    public TechnicalResults(String name) {
        this.name = name;
        results = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PointCalculationResult> getResults() {
        return results;
    }

    public void setResults(ArrayList<PointCalculationResult> results) {
        this.results = results;
    }    
    
    public void addResult(String date, double value) {
        results.add(new PointCalculationResult(date,value));
    }
}
