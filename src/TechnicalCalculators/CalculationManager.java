/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TechnicalCalculators;

import DataLoaders.SQLDBTools;
import DataContainers.StockData;
import DataLoaders.YahooQuoteSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author kpriddy
 */
public class CalculationManager {
    
    private String stockname;
    private StockData stockData;
    private HashMap<String,CalculationResult> resultList;
    private int cacheSize=20;
    private SQLDBTools dbConnection;
    
    public CalculationManager() {
        super();
        dbConnection = new SQLDBTools();
        resultList = new HashMap();
        stockData = new StockData();
    }
    
    public void setStockname(String stock) {
        this.stockname = stock;
        stockData = dbConnection.readStockfromDB(stock);
        //stockData = YahooQuoteSource.fetchEOD(stock);
    }
/*
 *   Parser.  Needs to receive and execute test descriptions:
 * 
 *   Comparisons:
 *    none - pass the results of the specified test
 *    A >= B
 *    A >= Fixed value
 *     ( for the inverses: < or <= just flip the order.  just ignore the = case)
 *   Sample cases:
 *    rsi < 20.0
 *    sma5 < sma10
 *    
 *   Test applied to a test:
 *   B=f(A)
 *   Sample case:
 *    ema(5) of %k stochastic
 * 
 *   Conjunctions:
 *    A* && B* where * is any comparison.
 *    A* || B*  
 *   
 */    
    public CalculationResult parse(ArrayList<String> commandLine) {
        Stack<CalculationResult> tests = new Stack<>();
        Stack<String> comparisonOp = new Stack<>();
        Stack<String> conjunctionOp = new Stack<>();
        ArrayList<String> accumulator = new ArrayList<>();
        
        for (String current : commandLine) {
            switch (current) {
                case "<":
                case "<=":
                    comparisonOp.push("<");
                    tests.add(evaluateAccumulator(accumulator));
                    break;
                case ">":
                case ">=":
                    comparisonOp.push(">");
                    tests.add(evaluateAccumulator(accumulator));
                    break;
                    
                case "&&":
                    conjunctionOp.push("&&");
                    tests.add(evaluateAccumulator(accumulator));
                    tests.add(evaluateComparison(tests, comparisonOp.pop()));
                    break;
                case "||":
                    conjunctionOp.push("||");
                    tests.add(evaluateAccumulator(accumulator));
                    tests.add(evaluateComparison(tests, comparisonOp.pop()));
                    break;                    
                default:
                    accumulator.add(current);                    
            }
        }
        
        //finish
        if(!accumulator.isEmpty()) {
            tests.add(evaluateAccumulator(accumulator));
        }
        if(!comparisonOp.isEmpty()) {
            tests.add(evaluateComparison(tests, comparisonOp.pop()));
        }
        
        if(!conjunctionOp.isEmpty()) {
            tests.add(evaluateConjunction(tests, conjunctionOp.pop()));
        }
        return tests.pop();
    }
    
    private CalculationResult evaluateConjunction(Stack<CalculationResult> tests, String comparisonOp) {
        double[] result2 = tests.pop().getCalcResults();
        double[] result1 = tests.pop().getCalcResults();
        int mSize = Math.min(result1.length, result2.length);
        double[] mResult = new double[mSize];
        CalculationResult returnMe = new CalculationResult();
        
        switch(comparisonOp) {
            case "&&":
                for(int i=0 ; i<mSize ; i++) {
                    if (result1[i] ==1 &&  result2[i] ==1) {
                        mResult[i] = 1;
                    } else {
                        mResult[i] = 0;
                    }
                }
                break;
            case "||":
                for(int i=0 ; i<mSize ; i++) {
                    if (result1[i] ==1 ||  result2[i] ==1) {
                        mResult[i] = 1;
                    } else {
                        mResult[i] = 0;
                    }
                }
                break;
            default: 
                System.out.print("Conjunction error on op:" + comparisonOp);
        }
        returnMe.setCalcResults(mResult, comparisonOp);
        return returnMe;
    }
    
    private CalculationResult evaluateComparison(Stack<CalculationResult> tests, String comparisonOp) {
        //These get popped in reverse order, so the second gets popped first.  This would
        //obviously have the opposite desired effect on a comparison!  So we will just reverse the
        //retrieval order.
        double[] result2 = tests.pop().getCalcResults();
        double[] result1 = tests.pop().getCalcResults();
        int mSize = Math.min(result1.length, result2.length);
        double[] mResult = new double[mSize];
        CalculationResult returnMe = new CalculationResult();
        
        switch(comparisonOp) {
            case ">":
                for(int i=0 ; i<mSize ; i++) {
                    if (result1[i] > result2[i]) {
                        mResult[i] = 1;
                    } else {
                        mResult[i] = 0;
                    }
                }
                break;
            case "<":
                for(int i=0 ; i<mSize ; i++) {
                    if (result1[i] < result2[i]) {
                        mResult[i] = 1;
                    } else {
                        mResult[i] = 0;
                    }
                }
                break;
            default: 
                System.out.print("Comparison error on op:" + comparisonOp);
        }
        returnMe.setCalcResults(mResult, comparisonOp);
        return returnMe;
    }
    
    private CalculationResult evaluateAccumulator(ArrayList<String> accum) {
        String[] data = new String[accum.size()];

        for (int i = 0; i < data.length; i++) {
            data[i] = accum.remove(0);
        }
        
        return getResult(data);
    }
    
    
    public String getStockname() {
        return stockname;
    }
    
    public StockData getStockData() {
        return stockData;
    }
    
    public HashMap getResultList() {
        return resultList;
    }

    public void setResultList(HashMap resultList) {
        this.resultList = resultList;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public CalculationResult getResult(String[] testConfig) {
        
            ArrayList<String> aListString = convertToAList(testConfig);
            String testname = convertToString(aListString);
            
        if (testConfig[0].equalsIgnoreCase("compare")) {
            return runComparisons(aListString);
        } else {
            //Check pseudoCache to see if it has already been run
            if (resultList.get(testname) == null) {
                //if not run the test and add it to the cache.
                runTest(aListString);
            }
            //WARN: watch for a race condition here - if the pseudocache is not updated
            // before this (it should not since it is single-threaded) then we will return the wrong
            // result!!!!
            return resultList.get(testname);
        }
    }
    
    private ArrayList<String> convertToAList(String[] input) {
        ArrayList<String> newName = new ArrayList<>();
        newName.addAll(Arrays.asList(input));
        
        return newName;
    }
    
    private CalculationResult runComparisons(ArrayList<String> tests) {
        CalculationResult comparisonResult = new CalculationResult();
        
        
        return comparisonResult;        
    }
    
    public String convertToString(ArrayList<String> input) {
        StringBuilder builder = new StringBuilder();
        for (String s : input) {
            builder.append(s);
        }
        return builder.toString();
    }
    
    /*
     * This needs to do the bulk of the class work:
     *  - run the tests and store it in the pseudocache
     *    - parse the testname
     *    - run the test
     *    - store results in a CalculationResult.
     *  - make sure it does not overload the cache
     */
    private void runTest(ArrayList<String> testname) {
        CalculationResult local = new CalculationResult();
        local.setDefinition(convertToString(testname));

        switch (testname.remove(0)) {
            case "ema": {
                double[] output = Calculators.ema(stockData.getCloses(), Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "ema");
                break;
            }
            case "sma": {
                double[] output = Calculators.sma(stockData.getCloses(), Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "sma");
                break;
            }
            case "macd": {
                double[] output = Calculators.macd(stockData.getCloses(),
                        Integer.parseInt(testname.remove(0)),
                        Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "macd");
                break;
            }
            case "macdindicator": {
                double[] output = Calculators.macdIndicator(stockData.getCloses(),
                        Integer.parseInt(testname.remove(0)),
                        Integer.parseInt(testname.remove(0)),
                        Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "macdIndicator");
                break;
            }
            case "macdcombined": {
                double[][] output = Calculators.macdCombined(stockData.getCloses(),
                        Integer.parseInt(testname.remove(0)),
                        Integer.parseInt(testname.remove(0)),
                        Integer.parseInt(testname.remove(0)));
                String[] resultNames = {"macd", "macdIndicator"};
                local.setCalcResults(output, resultNames);
                break;
            }
            case "roc": {
                double[] output = Calculators.roc(stockData.getCloses(), Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "roc");
                break;
            }
            case "rocPercent": {
                double[] output = Calculators.rocPercent(stockData.getCloses(), Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "rocPercent");
                break;
            }
            case "rsi": {
                double[] output = Calculators.rsi(stockData.getCloses(), Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "rsi");
                break;
            }
            case "williamsR": {
                double[] output = Calculators.williamsR(stockData.getCloses(),
                        stockData.getHighs(),
                        stockData.getLows(),
                        Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "williamsR");
                break;
            }
           case "mfi": {
                double[] output = Calculators.mfi(stockData.getCloses(),
                        stockData.getHighs(),
                        stockData.getLows(),
                        stockData.getVolumes(),
                        Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "williamsR");
                break;
            }
           case "stochasticK": {
                double[] output = Calculators.stochasticK(stockData.getCloses(),
                        stockData.getHighs(),
                        stockData.getLows(),
                        Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "stochasticK");
                break;
            }     
           case "stochasticD": {
                double[] output = Calculators.stochasticD(stockData.getCloses(),
                        stockData.getHighs(),
                        stockData.getLows(),
                        Integer.parseInt(testname.remove(0)),
                        Integer.parseInt(testname.remove(0)));
                local.setCalcResults(output, "stochasticD");
                break;
            }
            default: {
                double threshold = Double.parseDouble(local.getDefinition());
                double[] output = new double[3000];
                for (int i=0 ; i<3000 ; i++) {
                    output[i]=threshold;
                }
                
                local.setCalcResults(output, "fixed");
                //System.out.println("Unable to find the calculator for test: " + local.getDefinition());
            }
        }
        resultList.put(local.getDefinition(), local);


    }
}
