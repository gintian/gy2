package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchPlanInstituteTrans extends IBusiness {
	
	private void getTit(String table)
	{
		ArrayList fieldlist_Constant = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
		ArrayList fieldlist=new ArrayList();
		for(int i=0;i<fieldlist_Constant.size();i++)
		{
			FieldItem field=(FieldItem)fieldlist_Constant.get(i);
			if(!"q3107".equals(field.getItemid())&&!"q31z7".equals(field.getItemid()))
			{
				field.setValue("");
				field.setViewvalue("");				
				/*if(field.getItemid().equals("b0110")||field.getItemid().equals("e01a1")||field.getItemid().equals("q3101")||field.getItemid().equals("e0122")||field.getItemid().equals("nbase")||field.getItemid().equals("a0100")) 
			      field.setVisible(false);
				else
			      field.setVisible(true);*/
				if("1".equals(field.getState()))
					field.setVisible(true);
				else
					field.setVisible(false);
				fieldlist.add(field.cloneItem());
			}
		}
		this.getFormHM().put("flist", fieldlist);
	}

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String table = (String) hm.get("table");
		String nia = (String)hm.get("year2");
		String yea=(String)this.getFormHM().get("year");
		if(yea==null){
//			try {
//				String sql="select distinct min(kq_year)as kq_year from kq_duration where finished=0";
//				this.frowset=dao.search(sql.toString());
//				while(this.frowset.next()){
//					yea=this.frowset.getString("kq_year");
//				}
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			String currYear = RegisterDate.getCurrKqYear(this.frameconn);
			if (currYear != null && currYear.length() > 0) {
				yea = currYear;
			}
		}
		if(nia==null){
			try {
				String sql="select distinct MAX(kq_year)as kq_year from kq_duration order by kq_year desc";
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next()){
					nia=this.frowset.getString("kq_year");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
		StringBuffer sql_str = new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
	    String columns = "";
	    
		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++)
		{
			FieldItem field = (FieldItem) fieldlist.get(i);
			if(!"q3107".equals(field.getItemid())&&!"q31z7".equals(field.getItemid()))
			{
			  columns=columns+field.getItemid().toString()+",";
			  sql_str.append(field.getItemid());
			  sql_str.append(",");
			}
		}
		int sql_length=sql_str.toString().trim().length();
		sql_str.setLength(sql_str.length()-1);
		String select_sql=sql_str.toString();
		int columns_length=columns.length();
		columns=columns.substring(0,columns_length-1);
		  cond_str.append(" from ");
		  cond_str.append(table);
		  cond_str.append(" where a0100 ='");
		  cond_str.append(userView.getA0100());
		  cond_str.append("' and UPPER(nbase)='");
		  cond_str.append(userView.getDbname().toUpperCase()+"'");
		  //cond_str.append(" and approve_result='01' and status='01'");
		  if(!(nia==null|| "".equals(nia)))
			{
			  cond_str.append(" and q2903='");
			  cond_str.append(nia+"'");
			}
		this.getTit(table);
		this.getFormHM().put("sql",select_sql);
	    this.getFormHM().put("com",columns);
		this.getFormHM().put("where",cond_str.toString());
		this.getFormHM().put("slist", this.getYear());
		this.getFormHM().put("year",yea);     
	}
	private ArrayList getYear() throws GeneralException
	{
		ArrayList rlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ystr = new StringBuffer();
		try{
			ystr.append("select distinct kq_year from kq_duration order by kq_year desc");
			this.frowset =dao.search(ystr.toString());
			while (this.frowset.next())
			{
				CommonData yearvo = new CommonData(this.frowset.getString("kq_year"),this.frowset.getString("kq_year"));
				rlist.add(yearvo);
			}
		}
        catch(SQLException sqle)
		 {
		     sqle.printStackTrace();
		     throw GeneralExceptionHandler.Handle(sqle);
		  }
        if(rlist.size()==0)
        {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
      		int str = 0;
      		int nm=Integer.parseInt(sdf.format(new java.util.Date()));
      		for(int i=5;i>0;i--)
      		{
      		    str=nm-i;
      		    CommonData yearvos = new CommonData(String.valueOf(str),String.valueOf(str));
      		    rlist.add(yearvos);
      		}
      		for(int i=0;i<5;i++)
      		{
      		    str=nm+i;
      		    CommonData yearvo = new CommonData(String.valueOf(str),String.valueOf(str));
      		    rlist.add(yearvo);
   
      		}
        }
		
		return rlist;
	}
}
