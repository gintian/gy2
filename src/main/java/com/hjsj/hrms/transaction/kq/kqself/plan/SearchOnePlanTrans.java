package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchOnePlanTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String table = (String) hm.get("dtable");
		String plan_id=(String)hm.get("plan_id");
		String select_flag=(String)this.getFormHM().get("select_flag");
		String select_name=(String)this.getFormHM().get("select_name");
		this.getFormHM().put("select_flag",select_flag);
		this.getFormHM().put("select_name",select_name);
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		StringBuffer sql_str = new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
		String columns = "";
		String a_code=(String)this.getFormHM().get("a_code");
		if(a_code==null||a_code.length()<=0)
	    {
	    	   a_code="UN";
	    }
		String kind="2";
		String code="";
		if(a_code!=null&&a_code.length()>0)
		{
			String codesetid=a_code.substring(0,2);
			if("UN".equalsIgnoreCase(codesetid))
			{
				kind="2";
			}else if("UM".equalsIgnoreCase(codesetid))
			{
				kind="1";
			}else if("@K".equalsIgnoreCase(codesetid))
			{
				kind="0";
			}
			if(a_code.length()>=3)
			{
				code=a_code.substring(2);
			}else
			{
				code=this.userView.getUserOrgId();
			}
		}
		ArrayList kq_dbase_list=setKqPerList(code,kind);
		String select_pre=(String)this.getFormHM().get("select_pre");
		if(select_pre==null||select_pre.length()<=0)
		{
			if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
				select_pre="all";
		}
		ArrayList sql_db_list=new ArrayList();
		if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		{
			sql_db_list.add(select_pre);
		}else
		{
			sql_db_list=kq_dbase_list;
		}
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		String where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++)
		{
			  FieldItem field = (FieldItem) fieldlist.get(i);
			  if(!"q31z7".equals(field.getItemid())&&!"q3107".equals(field.getItemid())&& "M".equals(field.getItemtype()))
				  continue;			  
			  columns=columns+field.getItemid().toString()+",";
			  sql_str.append(field.getItemid()+",");			 
		}
		sql_str.setLength(sql_str.length()-1);
		cond_str.append(" from ");
		cond_str.append(table);			
		cond_str.append(" where q2901='"+plan_id+"'");
		if("1".equals(kind))
		{
			cond_str.append(" and e0122 like '"+code+"%'");
		}else if("0".equals(kind))
		{
			cond_str.append(" and e01a1 like '"+code+"%'");	
		}else
		{
			cond_str.append(" and b0110 like '"+code+"%'");	
		}
		cond_str.append(where_c);
		cond_str.append(" and " + table + "z5 <> '01'");
		StringBuffer sql=new StringBuffer();
		for(int i=0;i<sql_db_list.size();i++)
		{
			String nbase=sql_db_list.get(i).toString();
			sql.append(sql_str.toString());
			sql.append(cond_str.toString());
			String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
			sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
			sql.append(" and a0100 in(select a0100 "+whereIN+") "); 
			sql.append(" union all ");
		}
		sql.setLength(sql.length()-11);		
		this.setTite(table);
		this.getFormHM().put("sql",sql.toString());
		this.getFormHM().put("com",columns);
		this.getFormHM().put("plan_id",plan_id);
		this.getFormHM().put("a_code", a_code);
		//this.getFormHM().put("where",cond_str.toString());  
		
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
			   /*if(field.getItemtype().equalsIgnoreCase("M"))
				   field.setVisible(false);
			   else if(field.getItemid().equals("b0110")||field.getItemid().equals("e0122")||field.getItemid().equals("a0100")||field.getItemid().equals("q1501")||field.getItemid().equals("q3107")||field.getItemid().equals("q31z7")) 
				  field.setVisible(false);
				else
				  field.setVisible(true);*/
			   if("1".equals(field.getState()))
					field.setVisible(true);
				else
					field.setVisible(false);
			   FieldItem field_n=(FieldItem)field.cloneItem();
			   list.add(field_n.clone());   
		 }
		this.getFormHM().put("tlist", list);
	}
    private ArrayList setKqPerList(String code,String kind)throws GeneralException
    {
    	ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
    	KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);		
		this.getFormHM().put("kq_dbase_list",kq_dbase_list);
		this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseList(kq_dbase_list));
		return kq_dbase_list;
    }
}
