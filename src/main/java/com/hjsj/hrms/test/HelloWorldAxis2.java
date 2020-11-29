/**
 * 
 */
package com.hjsj.hrms.test;

import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2008-1-28:下午12:59:31</p> 
 *@author cmq
 *@version 4.0
 */
public class HelloWorldAxis2 {
    private HashMap map = new HashMap();

    public double getPrice(String symbol) {
        Double price = (Double) map.get(symbol);
        if(price != null){
            return price.doubleValue();
        }
        return 42.00;
    }

    public void update(String symbol, double price) {
        map.put(symbol, new Double(price));
    }

}
