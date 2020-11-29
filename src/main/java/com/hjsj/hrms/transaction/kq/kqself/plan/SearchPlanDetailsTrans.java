package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPlanDetailsTrans extends IBusiness {
	
	private void setTite(String table)
	{
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		ArrayList list=new ArrayList();
		for(int i=0;i<fieldlist.size();i++)
		   {
					
		   	   FieldItem field=(FieldItem)fieldlist.get(i);
			   field.setValue("");
			   field.setViewvalue("");
			   if("b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "q1501".equals(field.getItemid()))
				  field.setVisible(false);
				else
				  field.setVisible(true);
			   
			   FieldItem field_n=(FieldItem)field.cloneItem();
			   list.add(field_n);
		 }
		this.getFormHM().put("tlist", list);
	}

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String more = (String) hm.get("akq_year");  
		String table = (String) hm.get("dtable");
		
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		StringBuffer sql_str = new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
		String columns = "";
		
		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++)
		{
			  FieldItem field = (FieldItem) fieldlist.get(i);
			  columns=columns+field.getItemid().toString()+",";
			  sql_str.append(field.getItemid());
			  if (i != fieldlist.size()- 1)
			     sql_str.append(",");
		}
		    cond_str.append(" from ");
			cond_str.append(table);
			cond_str.append(" where a0100 ='");
			cond_str.append(userView.getA0100());
			cond_str.append("' and nbase='");
			cond_str.append(userView.getDbname());
			cond_str.append("' and q2903='");
			cond_str.append(more);
			cond_str.append("'");

			this.setTite(table);
			this.getFormHM().put("sql",sql_str.toString());
		    this.getFormHM().put("com",columns);
		    this.getFormHM().put("where",cond_str.toString());  

	}

}
