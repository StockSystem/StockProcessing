/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TechnicalCalculators;

/**
 *
 * @author kpriddy
 * 
 * double[][] contains all results
 * String description tells us what each result is (e.g. total for a single result calc, upperBand for
 * Bollinger upper band, or MACDvalue and MACDindicator for MACD.
 */
public class CalculationResult {
    String definition;  //definition is the specific text string that uniquely identifies the source test
    double[][] calcResults;
    String[] identifiers;
    
    public void setCalcResults(double[] input, String description) {
        calcResults= new double[1][];
        calcResults[0] = input;
        
        identifiers = new String[1];
        identifiers[0] = description;
    }
    
    public void setCalcResults(double[][] input, String[] description) {
        calcResults=input;
        identifiers=description;
    }
    
    public double[] getCalcResults() {
        return getCalcResults(0);
    }
    
    public double[] getCalcResults(int index) {
        return calcResults[index];
    }
    
    public double[][] getCalcResultsAll() {
        return calcResults;
    }
    
    public double[] getCalcResults(String id) {
        for(int i=0 ; i<identifiers.length ; i++) {
            if (id.equalsIgnoreCase(identifiers[i])) {
                return calcResults[i];
            }
        }
        return null;
    }
    
    public String[] getIdentifiers() {
        return identifiers;
    }
    
    public String getIdentifier(int index) {
        return identifiers[index];
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setCalcResults(double[][] calcResults) {
        this.calcResults = calcResults;
    }
    
    
}
