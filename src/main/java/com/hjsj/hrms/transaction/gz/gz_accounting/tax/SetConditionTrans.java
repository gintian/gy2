package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SetConditionTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		StringBuffer buf=new StringBuffer();
		
		ArrayList factorlist =(ArrayList)this.getFormHM().get("factorlist");
		if(factorlist!=null)
		{
    		for(int i=0;i<factorlist.size();i++)
	    	{
	    		Factor factor = (Factor)factorlist.get(i);			
			    factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
			    factor.setOper(PubFunc.keyWord_reback(factor.getOper()));
    		}
		}
		this.getFormHM().put("factorlist",factorlist);
//		String like = (String)this.getFormHM().get("like");
//		this.getFormHM().remove("like");
//		this.getFormHM().put("like","false");
		String conditionSQL = "";
		if(factorlist!=null && factorlist.size()>0)
		{
			conditionSQL = this.getQuerySql(factorlist,this.getUserView().getUserName(),"false");
			/* 安全问题：sql-in-url 所得税管理 xiaoyun 2014-9-12 start */
			conditionSQL = SafeCode.encode(PubFunc.encrypt(conditionSQL));
			/* 安全问题：sql-in-url 所得税管理 xiaoyun 2014-9-12 end */
			this.getFormHM().put("condtionsql",conditionSQL);
		}

	}
	public String getQuerySql(ArrayList factorlist,String username,String like)
	{
		String sql = "";
		try
		{
			ArrayList fieldlist = new ArrayList();
			Map map = this.getExpression(factorlist);
			String logs = (String)map.get("logs");
			String factors = (String)map.get("factors");
			HashMap fieldItemMap = this.getfieldItemMap(factorlist);
			FactorList factorlists = new FactorList(logs,factors,username,fieldItemMap);
			String fromTable=(String)this.getFormHM().get("fromTable");
			if(fromTable==null)
				fromTable="gz_tax_mx";
			sql = factorlists.getSingleTableSqlExpression(fromTable);
//			System.out.println(sql);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return sql;
	}
	/**
	 * 获得expression
	 * @param factorlist
	 * @return
	 */
	public Map getExpression(ArrayList factorlist)
	{
		Map map = new HashMap();
		String factors = "";
		String logs = "";
		StringBuffer logsb = new StringBuffer();
		StringBuffer fieldnamesb = new StringBuffer();
		int t = 0;
		for(int i=0;i<factorlist.size();i++)
		{
			Factor factor = (Factor)factorlist.get(i);			
			if(!(factor.getLog()==null || "".equals(factor.getLog())))
			{
				t++;
				logsb.append(factor.getLog()+t);
				fieldnamesb.append(factor.getFieldname()+factor.getOper()+factor.getValue()+"`");
			}
		}
		logs = logsb.substring(1);
		factors = fieldnamesb.toString();
		map.put("logs",logs);
		map.put("factors",factors);
		return map;
	}
	public HashMap getfieldItemMap(ArrayList factorlist)
	{
		HashMap map = new HashMap();
		TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
		ArrayList tempfieldlist = taxbo.getFieldlist();
		ArrayList fielditem = taxbo.getitemlist(tempfieldlist);
		for(int i=0;i<fielditem.size();i++)
		{	
		 	Field field = (Field)fielditem.get(i);
		 	FieldItem item = new FieldItem();
		 	item.setUseflag("1");
			if(field.getDatatype()==DataType.DATE)
			{
				item.setItemtype("D");
			}
			else if(field.getDatatype()==DataType.STRING)
			{
				item.setItemtype("A");
			}
			else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
			{
				item.setItemtype("N");
			}
			else if(field.getDatatype()==DataType.CLOB)
			{
				item.setItemtype("M");
			}
			else 
				item.setItemtype("A");
			item.setCodesetid(field.getCodesetid());
			item.setItemid(field.getName());						
			item.setItemdesc(field.getLabel());
			map.put(field.getName(),item);		
		}
		return map;
	}
}
