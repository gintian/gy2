package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.EducePlanInfoExcel;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class EducePlanInfoExcelTrans  extends IBusiness {

	public void execute() throws GeneralException 
	{
		String plan_id =(String)this.getFormHM().get("plan_id");
		String select_name =(String)this.getFormHM().get("select_name");
		String select_pre =(String)this.getFormHM().get("select_pre");
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
		StringBuffer sql_str = new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		ArrayList kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);
		ArrayList fieldlist = DataDictionary.getFieldList("q31",Constant.USED_FIELD_SET);
		if(select_pre==null||select_pre.length()<=0)
		{
			if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
				select_pre=kq_dbase_list.get(0).toString();
		}
		ArrayList sql_db_list=new ArrayList();
		if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		{
			sql_db_list.add(select_pre);
		}else
		{
			sql_db_list=kq_dbase_list;
		}		
		String where_c=kqUtilsClass.getWhere_C("1","a0101",select_name);
		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++)
		{
			  FieldItem field = (FieldItem) fieldlist.get(i);
			  /*if(field.getItemtype().equals("M"))
				  continue;	*/
			  sql_str.append(field.getItemid()+",");			 
		}
		sql_str.setLength(sql_str.length()-1);
		cond_str.append(" from ");
		cond_str.append("q31");			
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
		cond_str.append(" and q31z5 <> '01'");
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
		sql.setLength(sql.length()-10);
		EducePlanInfoExcel educePlanInfoExcel=new EducePlanInfoExcel(this.getFrameconn(),this.userView);
		String excel_filename=educePlanInfoExcel.createTableExcel(sql.toString(), fieldlist,plan_id);
		//xiexd 2014.09.16加密文件名
		excel_filename = PubFunc.encrypt(excel_filename);
		this.formHM.put("excelfile",excel_filename);
	}	
}
