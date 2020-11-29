package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class GetSqlTrans extends IBusiness {

	public void execute() throws GeneralException {
		 ArrayList relationList=(ArrayList)this.getFormHM().get("relation");
		 // A0101§§A§§0§§A01
		 ArrayList fielditemidList=(ArrayList)this.getFormHM().get("fielditemid");
		 ArrayList operateList=(ArrayList)this.getFormHM().get("operate");
		 ArrayList values=(ArrayList)this.getFormHM().get("values");
		 
		 EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		 String dbname=employActualize.getZP_DB_NAME();  //应用库前缀		
		
		 String sql=getSQL(relationList,fielditemidList,operateList,values,dbname);
		 this.getFormHM().put("sql",sql);
		 
		 StringBuffer columns_str=new StringBuffer("");
		 for(int i=0;i<fielditemidList.size();i++)
		 {
			 String temp=(String)fielditemidList.get(i);
			 String[] temps=temp.split("§§");
			 columns_str.append(","+temps[0]);
		 }
		 
		 this.getFormHM().put("columns_str",columns_str.substring(1));
	}
	
	
	
	
	public String getSQL(ArrayList relationList,ArrayList fielditemidList,ArrayList operateList,ArrayList values ,String dbname)
	{
		
		StringBuffer sql_select=new StringBuffer("select "+dbname+"A01.a0100,"+dbname+"A01.a0101"+Sql_switcher.concat()+"'('"+Sql_switcher.concat()+Sql_switcher.isnull("codeitem.codeitemdesc","'未选'")+Sql_switcher.concat()+"')'"+" a0101");
		StringBuffer sql_from=new StringBuffer(" from "+dbname+"A01 left join (select * from codeitem where codesetid='36') codeitem on "+dbname+"A01.state=codeitem.codeitemid ");
		StringBuffer sql_where=new StringBuffer("");
		
		HashMap tableMap=new HashMap();
		tableMap.put("A01","1");
		
		for(int i=0;i<relationList.size();i++)
		{
			
			String relation=(String)relationList.get(i);
			String fielditemid=(String)fielditemidList.get(i);
			String operate=(String)operateList.get(i);
			String value=(String)values.get(i);
			
			String[] afielditems=fielditemid.split("§§");
			if(!"a0101".equalsIgnoreCase(afielditems[0]))
			{
				if("D".equals(afielditems[1]))
					sql_select.append(","+Sql_switcher.numberToChar(Sql_switcher.year(afielditems[0]))+Sql_switcher.concat()+"'.'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month(afielditems[0]))+Sql_switcher.concat()+"'.'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day(afielditems[0]))+"  "+afielditems[0].toLowerCase());
				else
					sql_select.append(","+afielditems[0].toLowerCase());
			}
			String tempName=dbname+afielditems[3];
			if(tableMap.get(afielditems[3])==null)
			{
				tableMap.put(afielditems[3],"1");
				
				StringBuffer viewSql=new StringBuffer("");
				viewSql.append("(SELECT * FROM ");
				viewSql.append(tempName);
				viewSql.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM ");
				viewSql.append(tempName);
				viewSql.append(" B WHERE ");
				viewSql.append(" A.A0100=B.A0100  )) ");
				viewSql.append(tempName);
				
				sql_from.append(" left join "+viewSql.toString());
				sql_from.append(" on "+dbname+"A01.a0100="+tempName+".a0100");
			}
			sql_where.append(getNodeSql(relation,fielditemid,operate,value));
		}
		if(sql_where.length()>3)
			return  sql_select.toString()+sql_from.toString()+" where "+sql_where.substring(4);	
		else
			return sql_select.toString()+sql_from.toString();
	}
	
	
	
	public String getNodeSql(String relation,String fielditemid,String operate,String value)
	{
		
		StringBuffer node_sql=new StringBuffer("");
		String[] fielditems=fielditemid.split("§§");
		if("*".equals(relation))
			node_sql.append(" and ");
		else
			node_sql.append(" or  ");

		if("N".equalsIgnoreCase(fielditems[1]))
			node_sql.append(fielditems[0]+operate+value);
		else if("A".equalsIgnoreCase(fielditems[1]))
		{
			if("0".equals(fielditems[2]))
			{
				if(value.indexOf("*")!=-1||value.indexOf("?")!=-1)
				{
					value=value.replaceAll("\\*","\\%");
					node_sql.append(fielditems[0]+" like "+"'"+value+"'");
				}
				else
					node_sql.append(fielditems[0]+operate+"'"+value+"'");
			}
			else
			{
				
					node_sql.append(fielditems[0]+operate+"'"+value+"'");
			
			}
		}
		else
			node_sql.append(getDataValue(fielditems[0],operate,value));
		return node_sql.toString();
	}

	
	
	public String getDataValue(String fielditemid,String operate,String value)
	{
		StringBuffer a_value=new StringBuffer("");
		GregorianCalendar d = new GregorianCalendar();
		
		try
		{

				if("=".equals(operate))
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
					a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
					a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(" ) ");
				}
				else 
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(") ) ");
				}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_value.toString();
	}
	
	
}
