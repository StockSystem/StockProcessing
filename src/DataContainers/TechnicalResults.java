/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataContainers;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kpriddy
 */
public class TechnicalResults {
    private String name;
    private List<PointCalculationResult> results;
    
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

    public List<PointCalculationResult> getResults() {
        return results;
    }

    public void setResults(List<PointCalculationResult> results) {
        this.results = results;
    }    
    
    public void addResult(double date, double value) {
        results.add(new PointCalculationResult(date,value));
    }
}
