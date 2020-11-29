package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.machine.DateAnalyseImp;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchDataAnalyseTrans extends IBusiness implements DateAnalyseImp{

	public void execute() throws GeneralException 
	{
		String a_code=(String)this.getFormHM().get("a_code");
		String nbase=(String)this.getFormHM().get("nbase");
		String strSql=(String)this.getFormHM().get("strSql");
		String column=(String)this.getFormHM().get("column");
    	ArrayList fieldList=(ArrayList)this.getFormHM().get("fieldList");
    	String temp_Table=(String)this.getFormHM().get("temp_Table");
    	if(temp_Table==null||temp_Table.length()<=0)
    	{
    		throw GeneralExceptionHandler.Handle(new GeneralException("","没有找到原始数据分析临时表，请重新分析数据！","",""));
    	}
		if(a_code==null||a_code.length()<=0)
	    {
	    	   a_code="UN";
	    }
		String kind="2";
		if(a_code.indexOf("UN")!=-1)
		{
			kind="2";
		}else if(a_code.indexOf("UM")!=-1)
		{
			kind="1";
		}else if(a_code.indexOf("@K")!=-1)
		{
			kind="0";
		}else if(a_code.indexOf("EP")!=-1)
		{
			kind="-1";
		}
		String code="";
		if(a_code.length()>2)
		{
			code=a_code.substring(2);
		}
		if("-1".equals(kind))
		{
			code=nbase+code;
		}
		StringBuffer whereStr=new StringBuffer();
		whereStr.append(" from "+temp_Table+" where 1=1");
		if("1".equals(kind))
		{
			whereStr.append(" and e0122 like '"+code+"%'");
		}else if("0".equals(kind))
		{
			whereStr.append(" and e01a1 like '"+code+"%'");	
		}else if("2".equals(kind))
		{
			whereStr.append(" and b0110 like '"+code+"%'");	
		}else if("-1".equals(kind))
		{
			whereStr.append(" and a0100 = '"+code+"' and nbase='"+nbase+"'");
		}		
		this.getFormHM().put("strSql",strSql);
		this.getFormHM().put("whereStr",whereStr.toString());
		this.getFormHM().put("fieldList",fieldList);
		this.getFormHM().put("column",column);
		this.getFormHM().put("order","order by nbase,b0110,e0122");
	}

}
