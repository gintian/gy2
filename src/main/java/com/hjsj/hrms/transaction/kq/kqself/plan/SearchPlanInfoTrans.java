package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SearchPlanInfoTrans extends IBusiness {
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
	private ArrayList getItem(String table)
	{
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
		ArrayList list=new ArrayList();
		for(int i=0;i<fieldlist.size();i++)
		{
			
			FieldItem field=(FieldItem)fieldlist.get(i);
			field.setValue("");
			field.setViewvalue("");
			/*if(field.getItemid().equals("b0110")||field.getItemid().equals("e0122")||field.getItemid().equals("q29z7")) 
		      field.setVisible(false);
			else
		      field.setVisible(true);*/
			if("1".equals(field.getState()))
				field.setVisible(true);
			else
				field.setVisible(false);
			FieldItem field_n=(FieldItem)field.cloneItem();
			list.add(field_n);
		}
		return list;
	}

	public void execute() throws GeneralException {
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		ArrayList kq_dbase_list=kqUtilsClass.getKqPreList();	
		if (kq_dbase_list.size() == 0 || kq_dbase_list == null) 
		{
  		   throw new GeneralException(ResourceFactory.getProperty("kq.register.dbase.nosave"));
		}
		
		String table = "q29";;
		String yea=(String)this.getFormHM().get("year");
		if(yea==null){
			String currYear = RegisterDate.getCurrKqYear(this.frameconn);
			if (currYear != null && currYear.length() > 0) {
				yea = currYear;
			}
		} 
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
		StringBuffer sql_str = new StringBuffer();
		StringBuffer con = new StringBuffer();
		String columns = "";
		
		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++) 
		{
			FieldItem field = (FieldItem) fieldlist.get(i);
			columns=columns+field.getItemid().toString()+",";
			if("e0122".equals(field.getItemid()))
			{
				sql_str.append(Sql_switcher.isnull("e0122","''"));
			}else
			{
				sql_str.append(field.getItemid());
			}
			
			if (i != fieldlist.size() - 1)
				sql_str.append(",");
		}
		    String columnstr=columns.trim().substring(0,columns.length()-1);
			con.append(" from ");
			con.append(table);
			con.append(" where 1=1");	
			
			if(!(yea==null|| "".equals(yea)))
			{
				con.append(" and q2903='");
				con.append(yea);
				con.append("'");
			}else
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	      		yea = sdf.format(new java.util.Date());
				con.append(" and q2903='");
				con.append(yea);
				con.append("'");
			}
			
			 this.getFormHM().put("year",yea+"");
			 this.getFormHM().put("table",table);
			 this.getFormHM().put("tlist", this.getItem(table));
			 this.getFormHM().put("sql",sql_str.toString());
		     this.getFormHM().put("com",columnstr);
		     this.getFormHM().put("where",con.toString());  
		     this.getFormHM().put("order"," order by q2909 desc");  
		     this.getFormHM().put("slist", this.getYear());
	}

}
