package com.hjsj.hrms.transaction.kq.kqself.apply;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewMyAnnualApplysTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String table = (String) hm.get("table");
		String plan_id=(String)hm.get("plan_id");
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		StringBuffer sql_str = new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
		String columns = "";
		cond_str.append(" from ");
		cond_str.append(table);			
		cond_str.append(" where q2901='"+plan_id+"'");
		cond_str.append(" and a0100='"+this.userView.getA0100()+"'");
		cond_str.append("and nbase='"+this.userView.getDbname()+"'");
		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++)
		{
			  FieldItem field = (FieldItem) fieldlist.get(i);
			  /*
			   * 会导致说明，审批意见不显示，故删除此段
			   * if(field.getItemtype().equals("M"))
				  continue;
			   */			  
			  columns=columns+field.getItemid().toString()+",";
			  sql_str.append(field.getItemid()+",");			 
		}
		sql_str.setLength(sql_str.length()-1);
		this.getFormHM().put("sql",sql_str.toString());
		this.getFormHM().put("com",columns);
		this.getFormHM().put("plan_id",plan_id);
		this.getFormHM().put("where",cond_str.toString()); 
		setTite(table);
	}
	private void setTite(String table)
	{
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		ArrayList list=new ArrayList();		
		for(int i=0;i<fieldlist.size();i++)
		{
					
		   	   FieldItem field=(FieldItem)fieldlist.get(i);
			   field.setValue("");
			   field.setViewvalue("");
			  /* if(field.getItemtype().equalsIgnoreCase("M"))
				   field.setVisible(false);
			   else if(field.getItemid().equals("b0110")||field.getItemid().equals("e0122")||field.getItemid().equals("a0100")||field.getItemid().equals("q1501")||field.getItemid().equals("q3107")||field.getItemid().equals("q31z7")) 
				  field.setVisible(false);
				else
				  field.setVisible(true)*/;
			   if("1".equals(field.getState()))
			   {
				  field.setVisible(true);
			   }else
				field.setVisible(false);
			   FieldItem field_n=(FieldItem)field.cloneItem();
			   list.add(field_n.clone());   
		}
		this.getFormHM().put("tlist",list);
	}
	
}
