package com.hjsj.hrms.test;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 15, 2005:10:16:45 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class TestRecordVo {

    public static void main(String[] args) {
    	Employee obj=new Employee();
    	obj.setName("xxxx");
    	obj.setSex("yyy");
        StringBuffer strcontent=new StringBuffer();    
        strcontent.append("{");
        try
        {
        	Map empmap=PropertyUtils.describe(obj);
            Iterator iterator=empmap.entrySet().iterator();
            while(iterator.hasNext())
            {
                 java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
                 String name=entry.getKey().toString();
                 Object value=entry.getValue();
                 System.out.println("name="+value.toString());
            }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        
//        Map beanmap=new BeanMap(obj);
//        Set keys=beanmap.keySet();
//        Iterator keyIterator=keys.iterator();
//        while(keyIterator.hasNext())
//        	
//        {
//          String propertyName=(String)keyIterator.next();
//          strcontent.append(propertyName);
//          strcontent.append(":");
//          Object midobj=beanmap.get(propertyName);
//          //strcontent.append(doExchangeByType(midobj));
//          strcontent.append(",");
//        }
        /**get out of ,*/
        strcontent.setLength(strcontent.length()-1);
        strcontent.append("}");
        System.out.println("===>"+strcontent.toString());    	
    }
}
