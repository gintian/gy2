package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.hire.ExecuteExcel;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:ExportFileTrans.java</p>
 * <p>Description:考核体系导出Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-26 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */


public class ExportFileTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String strSql1=(String)this.getFormHM().get("strsql");
	String strSql2=(String)this.getFormHM().get("strwhere");
	String strSql="";
	if(strSql1!=null && strSql2!=null && strSql1.length()>0 && strSql2.length()>0)
	    strSql=strSql1+" "+strSql2;
	
	String recTable = (String)this.getFormHM().get("recTable");
	
	ArrayList fieldList = DataDictionary.getFieldList(recTable, Constant.USED_FIELD_SET);
	ExecuteExcel executeExcel = new ExecuteExcel(this.frameconn, this.getUserView(), recTable);
	String outName = executeExcel.createExcel(fieldList, strSql, "2");
	outName = outName.replaceAll(".xls", "#");
	this.getFormHM().put("outName", outName);
    }

}
