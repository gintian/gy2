package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetConditionPersonnelTrans extends IBusiness {

	public void execute() throws GeneralException {
		String sql=(String)this.getFormHM().get("sql");
		String column_str=(String)this.getFormHM().get("column_str");
		column_str=resetColumnStr(column_str);
		column_str=clearMultipleColumns(column_str);
		
		String codeid=(String)this.getFormHM().get("codeid");
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		ArrayList positionList=employActualize.getPositionList(codeid);
		ArrayList tableColumnsList=employActualize.getColumnsList(column_str);
		
		this.getFormHM().put("columns",column_str);
		this.getFormHM().put("positionList",positionList);
		this.getFormHM().put("tableColumnsList",tableColumnsList);
		this.getFormHM().put("select_str",sql.substring(0,sql.indexOf("from")));
		this.getFormHM().put("from_str",sql.substring(sql.indexOf("from")));
		

	}

	
	
	public String resetColumnStr(String columnStr)
	{
		columnStr=columnStr.replaceAll("A0100,","");
		columnStr=columnStr.replaceAll("A0100","");
		
		String str="";
		if(columnStr.indexOf("A0101")==-1)
		{
			str="a0100,a0101,"+columnStr;
		}
		else
		{
			str="a0100,a0101,"+columnStr.replaceAll(",A0101","");
		}
	//	str+=",codeitemdesc";
		return str;
		
	}
	
	
	public String clearMultipleColumns(String columnStr)
	{
		columnStr=columnStr.toLowerCase();
		StringBuffer str=new StringBuffer("");
		String[] temps=columnStr.split(",");
		HashMap map=new HashMap();
		for(int i=0;i<temps.length;i++)
		{
			if(map.get(temps[i])==null)
			{
				str.append(",");
				str.append(temps[i].toLowerCase());
				map.put(temps[i],"1");
			}
		}
		return str.substring(1);
	}
	
	
	public static void main(String[] arg)
	{
		GetConditionPersonnelTrans a=new GetConditionPersonnelTrans();
		String column="A0100,A0101,A9023,A0402,A0323,A0402";
		System.out.println(a.clearMultipleColumns(column));
		
	}
	
	
}
