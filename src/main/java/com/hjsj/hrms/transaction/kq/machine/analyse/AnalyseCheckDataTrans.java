package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class AnalyseCheckDataTrans  extends IBusiness{
	private String tab_Name="";
    public void execute() throws GeneralException
    {
    	String type=(String)this.getFormHM().get("type");//操作类型
    	String table=(String)this.getFormHM().get("table");
    	if(type==null||type.length()<=0)
    		throw GeneralExceptionHandler.Handle(new GeneralException("","找不到数据分析结果表","","")); 
    	if(table==null||table.length()<=0)
    		throw GeneralExceptionHandler.Handle(new GeneralException("","找不到数据分析结果表","","")); 
    	this.tab_Name=table;
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		if("result".equals(type))
        	{
        		String nbase=(String)this.getFormHM().get("nbase");
        		String a0100=(String)this.getFormHM().get("a0100");
        		String q03z0=(String)this.getFormHM().get("q03z0");
        		String flag=(String)this.getFormHM().get("flag");
        		StringBuffer sql=new StringBuffer();
        		sql.append("update "+this.tab_Name+" set flag='"+flag+"' where ");
        		sql.append(" nbase='"+nbase+"' and a0100='"+a0100+"' and q03z0='"+q03z0+"'");
        		dao.update(sql.toString());
        	}
    	}catch(Exception E)
    	{
    		E.printStackTrace();
    	}
    	
	}

}
