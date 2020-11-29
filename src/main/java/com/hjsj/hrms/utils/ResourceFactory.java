package com.hjsj.hrms.utils;

import java.util.ResourceBundle;
/**
 * <p>Title:资源中心</p>
 * <p>Description:为了国际化版本</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 11, 2005:3:35:41 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ResourceFactory {

    public ResourceFactory() {
    }
    private static ResourceBundle bundle;
    
    static
    {
      bundle=ResourceBundle.getBundle("MessageResources");    
    }   
    
    static public String getProperty(String label)
    {
      try
      {
        return bundle.getString(label);
      }
      catch(Exception ex)
      {
    	  return label;
      }
    }
    
}
