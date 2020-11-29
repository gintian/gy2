/*
 * Created on 2005-5-9
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.businessobject.parse.parseinterface;

import com.hjsj.hrms.businessobject.parse.parsebusiness.CondAnalyse;
import com.hjsj.hrms.businessobject.parse.parsebusiness.Factor;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ParseInterface {
	//factor 的格式例如(A0100=2`B0110=3`) lexpr的格式例如(1+2)*3
	//cBase是人员库
	public String ParseResult(String Factor,String lexpr,String cBase)
	{
		String[] factors; 
		String Result="";
		String strTemp;
		CondAnalyse analy=new CondAnalyse();
		factors=analy.mySplit(Factor,"`");
		if(lexpr.length()>0) {
            Result="(";
        }
	    for(int j=0,l=0;j<lexpr.length();j++)
	    {
		   if("(".equals(lexpr.substring(j,j+1)))             //左扩号
		   {
			  Result +="(";
		   }
		   else if(")".equals(lexpr.substring(j,j+1)))        //右扩号
		   {
			  Result +=")";
		   }
		   else if("+".equals(lexpr.substring(j,j+1)))    //逻辑运算OR
		   {
			  Result +=" OR ";
		   }
		   else if("*".equals(lexpr.substring(j,j+1)))    //逻辑运算AND
		   {
			  Result +=" AND ";
		   }
	       else                                               //一个条件结果
		   {
			//one factor gengeral query sql one condition
			  Factor factor = new Factor(cBase,factors[l]);	
			  Result +=factor.getCresult();	                        
			  l++;	
		   }
	    }
	    if(lexpr.length()>0) {
            Result+=")";
        }
	   return Result;
	}	


}
