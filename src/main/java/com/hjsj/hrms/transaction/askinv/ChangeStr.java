/*
 * Created on 2005-5-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import java.io.IOException;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeStr {
	
	
//	编码转换
	public static String ToGbCode (String str)throws IOException{
            
            String isChangeToGBK ="true";

            //isChangeToGBK = PropertyManager.getProperty("isChangeToGBK");
            if (isChangeToGBK.compareTo("true") == 0 ){	
                if(str!=null){
                              str = new String(str.getBytes("ISO-8859-1"),"GBK");         		
		              }
            }
     		return str;
	}	

}
