/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TechnicalCalculators;

import java.util.Arrays;

/**
 *
 * @author kpriddy
 */
public class Calculators {
    
       
    
    public static double average(double[] input) {
        double total=0;
        
        for (double current : input) {
            total+=current;
        }
        
        return total/input.length;
    }
    
    /*
     * NOTE:  All calculators assume oldest value is LAST and newest
     * value has index=0!!!!
     */
    
    /*
     * SMA - Simple moving average
     * 
     * Just calculates the average for <range> days back from the current
     * date.  It is moving, because each day looks back at the last <range>
     */
    public static double[] sma(double[] input, int range) {
        double[] result = new double[input.length-(range-1)];
        double total=0;
        
        for(int i=input.length; i>=range ; i--) {
            result[i-range] = average(Arrays.copyOfRange(input,i-range,i));
            total = 0;
        }
        return result;
    }
    
    /*
     * ema - Exponential Moving Average
     * 
     * Similar to SMA, but there is an exponential weight to make more 
     * recent days have a stronger weight on the average.
     * 
     * ema = Price(t) * k + ema(y) * (1 – k)
     * t = today, y = yesterday, N = number of days in ema, k = 2/(N+1)
     */
    public static double[] ema(double[] input, int range) {
        double[] result = new double[input.length-(range-1)];
        double k = 2.0/(range+1);
        
        //Step 1: Calculate the Simple average for the furthest out date
        result[result.length-1] = average(Arrays.copyOfRange(input, result.length-1, input.length-1));
        
        double t,y;
        //Step 2: Calculate ema from then on:
        for(int i = result.length-2 ; i>=0 ; i--) {
            result[i] = input[i]*k + result[i+1]*(1-k);
            t= input[i];
            y=result[i+1];
        }
        
        return result;
    }
    
    /*
     * macd - Moving Average Convergence/Divergence
     * Simply the difference between two ema's.
     * Generally, it has another ema applied to the result called
     * the macd-Indicator.  That will be a separate method.
     */
    public static double[] macd(double[]input, int ema1, int ema2) {
        double[] emar1 = ema(input,ema1);
        double[] emar2 = ema(input,ema2);
       
        int size=Math.min(emar1.length,  emar2.length);
        
        double[] result = new double[size];
        
        for(int i=0 ; i<size ; i++) {
            result[i] = emar1[i]-emar2[i];
        } 
        return result;
    }
    
    /*
     * macdIndicator takes a macd and applies a ema to it
     * to derive the indicator.  Not very efficient to run this separately
     * if the two are to be compared, as it will calculate macd (and the ema's)
     * twice!!!
     */
    public static double[] macdIndicator(double[]input, int ema1, int ema2, int indicator) {
        double[] macd = macd(input,ema1,ema2);
        
        return ema(macd, indicator);
    }
    
    /*
     * macdCombined returns a 2D array of both the macd and its indicator to 
     * minimize repetitive calculations if the calling code can absorb it.
     */
    public static double[][] macdCombined(double[]input, int ema1, int ema2, int indicator) {
        double[] macdvalue = macd(input,ema1,ema2);
        double[] indicatorValue = ema(macdvalue, indicator);
        
        //By definition the ema will be shorter than the original value, so we will use its length
       // for a nice, even set of arrays!
        double[][] result = new double[2][indicatorValue.length-1];
        result[0] = Arrays.copyOfRange(macdvalue ,0, indicatorValue.length-1);
        result[1] = indicatorValue;
        
        return result;
    }
    
    /*
     * max - simple maximum of an array
     */
    
    public static double max(double[] input) {
        double max=input[0];
        
        for(double current: input) {
            if (current > max) {
                max=current;
            }
        }
        
        return max;
    }
    
    /*
     * max - simple maximum of an array
     */
    
    public static double min(double[] input) {
        double min=input[0];
        
        for(double current: input) {
            if (current < min) {
                min=current;
            }
        }
        
        return min;
    }
    
    /*
     * MFI = Money flow index = RSI with volume
     *   * 1. Typical Price = (High + Low + Close)/3
     *   * 2. Raw Money Flow = Typical Price x Volume
     *   * 3. Money Flow Ratio = (14-period Positive Money Flow)/(14-period Negative Money Flow)
     *   * 4. Money Flow Index = 100 - 100/(1 + Money Flow Ratio)
     */
    
    public static double[] mfi(double[] close, double [] high, double[] low,  int[] volume, int range) {
       double[] rawMF = new double[close.length-1]; //volume * ((h+l+c)/3)
       double[] mfi = new double[close.length-range];
       int[] direction = new int[close.length-1];
       
       for (int i=0 ; i<close.length-1 ; i++) {
           rawMF[i] = volume[i] * ((high[i] + low[i] + close[i])/3);
           if (close[i]>close[i+1]) {
               direction[i]=1;
           } else {
               direction[i]=-1;
           }
       }
       
       for (int i=0 ; i<mfi.length ; i++) {
           mfi[i] = 100 - (100/
                                (1+calcMFR(Arrays.copyOfRange(rawMF, i, i+range),Arrays.copyOfRange(direction, i, i+range))));
       }
       
       return mfi;
        
    }
    
    private static double calcMFR(double[] input, int[] direction) {
        double positiveFlow=0;
        double negativeFlow=0;
        
        for (int i=0 ; i<input.length ; i++) {
            if (direction[i]>0) {
                positiveFlow+=input[i];
            }
            else {
                negativeFlow+=input[i];
            }
        }
        
        return positiveFlow/negativeFlow;
    }
    
    /*
     * ROC = Rate of Change)
     * Calculates the trend (difference between two dates).
     * This Looks BACKWARDS at history.  So it is the increase from "range" 
     * days ago until now!
     */
    public static double[] roc(double[] input, int range) {
        double[] result = new double[input.length-range];
        
        for(int i=0 ; i<result.length ; i++) {
            result[i]=input[i]-input[i+range];
        }
        
        return result;
    }
    
    /*
     * PercentTrend
     * Calculates the trend, but normalizes it as a %age of the value at the oldest 
     * date in the range.
     */
    
    public static double[] rocPercent(double[] input, int range) {
        double[] result = new double[input.length-range];
        
        for(int i=0 ; i<result.length ; i++) {
            result[i]=100*(input[i]-input[i+range]) / input[i+range];
        }
        
        return result;
    }
    
        /*
     * RSI - Relative Strength Indicator
     * Oscillator between 0 and 100.  Utilizes "Relative Strength" with net change between
     * days to determine the strength of a trend.
     * RSI=100-(100/(1+RS))
     * RS = "Average up price over period"
     *         --------------------------------
     *         "Average down price over period"
     */
    
    public static double[] rsi(double[] input, int range) {
        double[] result = new double[input.length-range];
        
        for(int i=0 ; i<result.length ; i++) {
            result[i]= 100-(100/(1+rs(Arrays.copyOfRange(input, i, i+range))));
        }
        
        return result;
    }
    
    /*
     * RS = Relative Strength
     * This is just a helper method for rsi.  I haven't seen any technical analyses that
     * directly use the results for this, so I am not exposing it public.
     */
    private static double rs(double[] input) {
        double positiveChange=0;
        double negativeChange=0;
        double change=0;
        
        //calculate the positive and negative change totals
        for(int i=1 ; i<input.length ; i++) {
            change=input[i-1]-input[i];
            if(change>0) {
                positiveChange+=change;
            } else {
                negativeChange+=change;
            }  
        }
        //return the absolute value of the averages
        return (positiveChange/input.length)/(Math.abs(negativeChange)/input.length);        
    }
    
    /*
     * Stockastic %k is a momentum indicator
     * 
     * Calculation: 
     * %k = 100* (Current close - Lowest Low)/
     *              (Highest high - Lowest Low)
     */
    
    public static double[] stochasticK (double[] close, double [] high, double[] low,  int range) {
        double[] result = new double[close.length-range];
        double maxV, minV;
        
        for(int i=0 ; i < result.length ; i++) {
            maxV = max(Arrays.copyOfRange(high, i, i+range)); //highest high
            minV = min(Arrays.copyOfRange(low, i, i+range)); //lowest low
            
            result[i]= 100*(close[i]-minV)/(maxV-minV); 
        }
        return result;
    }
    
    public static double[] stochasticD(double[] close, double [] high, double[] low,  int range, int smaRange) {

        return sma(stochasticK (close, high, low, range), smaRange);
    }
    
    /*
     * Williams%R
     * An Oscillator with a percentile range (-100-0)
     * wR = (Highest price - Today)
     *         -------------------------              x      -100
     *         (Highest price-Lowest price)
     */
    public static double[] williamsR(double[] close, double [] high, double[] low,  int range) {
        double[] result = new double[close.length-range];
        double maxV, minV;
        
        for(int i=0 ; i < result.length ; i++) {
            maxV = max(Arrays.copyOfRange(high, i, i+range));
            minV = min(Arrays.copyOfRange(low, i, i+range));
            
            result[i]= -100*(maxV-close[i])/(maxV-minV); 
        }
        return result;
    }
  
    public static void main(String[] args) {
        double[] test = {10.6,10.8,10.7,10.6,10.5,10.4,10.3,10.2,10.2,10.4,10.5,10.6,10.7,10.8,10.9};
        double[] high = {11.6,11.8,11.7,11.6,11.5,11.4,11.3,11.2,11.2,11.4,11.5,11.6,11.7,11.8,11.9};
        double[] low = {9.6,9.8,9.7,9.6,9.5,9.4,9.3,9.2,9.2,9.4,9.5,9.6,9.7,9.8,9.9};
        
        
        double[] result = sma(test,1);
        System.out.println("SMA:");
        for (double current : result) {
            System.out.println(current);
        }
        
        System.out.println("ema:");
        result = ema(test,8);
        for (double current : result) {
            System.out.println(current);
        }
        
        System.out.println("macd:");
        result = macd(test,2,5);
        for (double current : result) {
            System.out.println(current);
        }  
        
        System.out.println("macdIndicator:");
        result = macdIndicator(test,2,5,2);
        for (double current : result) {
            System.out.println(current);
        }
          
        System.out.println("ROC:");
        result = roc(test,5);
        for (double current : result) {
            System.out.println(current);
        }
                
        System.out.println("ROC Percent:");
        result = rocPercent(test,5);
        for (double current : result) {
            System.out.println(current);
        }
        
        System.out.println("RSI:");
        result = rsi(test,7);
        for (double current : result) {
            System.out.println(current);
        }
        
        System.out.println("Williams%R:");
        result = williamsR(test,high, low, 4);
        for (double current : result) {
            System.out.println(current);
        }
        
        System.out.println("WStochastic %k:");
        result = stochasticK(test,high, low, 4);
        for (double current : result) {
            System.out.println(current);
        }
        
        System.out.println("Stochastic %d:");
        result = stochasticD(test,high, low, 4,3);
        for (double current : result) {
            System.out.println(current);
        }
    }
    
}
