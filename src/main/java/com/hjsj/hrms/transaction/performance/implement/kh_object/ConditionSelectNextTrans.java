package com.hjsj.hrms.transaction.performance.implement.kh_object;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 条件选择考核对象下一步
 * 
 * JinChunhai
 */

public class ConditionSelectNextTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
        String[] selectedField=(String[])this.getFormHM().get("right_fields");        
		ArrayList selectedFieldList=new ArrayList();
		ArrayList fieldlist=new ArrayList();
		
		//zgd 2015-1-13 条件选择类型 general=通用查询 start
	    String expression = "";
		//zgd 2015-1-13 条件选择类型 general=通用查询 end
	
		for(int i=0;i<selectedField.length;i++)
		{
			String temp=selectedField[i];
			String[] array=temp.split("<@>");
			DynaBean bean = new LazyDynaBean();
			bean.set("itemid",array[0]);
			bean.set("itemdesc",array[1]);
			bean.set("itemtype",array[2]);
			bean.set("table_name", array[4]);
			String itemsetid="0";
			if(array.length>3)
			    itemsetid=array[3];
	
			bean.set("itemsetid",itemsetid);
			
			selectedFieldList.add(bean);	
			
			CommonData datavo = new CommonData(array[0] + "<@>" + array[1] + "<@>" + array[2] + "<@>" + itemsetid + "<@>" +  array[4], array[1]);
		    fieldlist.add(datavo);
		    
		  //zgd 2015-1-13 条件选择类型 general=通用查询 start
		    if("".equalsIgnoreCase(expression)){
		    	expression = String.valueOf(i+1);
		    }else{
		    	expression += "*" + String.valueOf(i+1);
		    }
			//zgd 2015-1-13 条件选择类型 general=通用查询 end
		}
		//zgd 2015-1-13 条件选择类型 general=通用查询 start
		this.getFormHM().put("expression", expression);
		//zgd 2015-1-13 条件选择类型 general=通用查询 end
		//
		String dbpre=(String) this.getFormHM().get("dbpre");
		this.getFormHM().put("dbpre", dbpre);
		this.getFormHM().put("selectedFieldList",selectedFieldList);
		this.getFormHM().put("rightlist",fieldlist);
		
		
		
    }

}
