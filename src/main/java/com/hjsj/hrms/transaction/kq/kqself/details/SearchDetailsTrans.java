package com.hjsj.hrms.transaction.kq.kqself.details;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchDetailsTrans extends IBusiness{

	private ArrayList getYear() throws GeneralException
	{
		ArrayList rlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ystr = new StringBuffer();
		try{
			  ystr.append("select distinct kq_year from kq_duration order by kq_year desc ");
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
	private ArrayList getField(String table)
	{
		  ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		  ArrayList list=new ArrayList();
		  for(int i=0;i<fieldlist.size();i++)
			  {	
			     FieldItem field=(FieldItem)fieldlist.get(i);
			     FieldItem field_n=(FieldItem)field.cloneItem();
			     
			     field_n.setValue("");
			     field_n.setViewvalue("");
				 if("q03z0".equals(field_n.getItemid()))
				 {
					 String mm=field_n.getItemdesc().replaceAll(field_n.getItemdesc(),ResourceFactory.getProperty("kq.sys.mon"));
					 field_n.setItemdesc(mm);
				 }
				 if("b0110".equals(field_n.getItemid())|| "a0101".equals(field_n.getItemid())|| "e0122".equals(field_n.getItemid())
				         || "e01a1".equals(field_n.getItemid())|| "nbase".equals(field_n.getItemid())|| "a0100".equals(field_n.getItemid())
				         || "q03z3".equals(field_n.getItemid())|| "state".equals(field_n.getItemid()))
				     field_n.setVisible(false);
				else if("q03z0".equals(field_n.getItemid()))
				    field_n.setVisible(true);
				else
				{
					if("1".equals(field_n.getState()))
					{
					    field_n.setVisible(true);
					}else
					{
					    field_n.setVisible(false);
					}
				}
				 
				 list.add(field_n);
			 }
		  
		      FieldItem fields= new FieldItem();
			  fields.setItemdesc(ResourceFactory.getProperty("kq.self.sourse"));
			  fields.setItemid("scope");
			  fields.setCodesetid("0");
			  fields.setVisible(true);
			  list.add(fields); 
		return list;
	}
	public void execute() throws GeneralException {
		 String ky_year = (String)this.getFormHM().get("kq_year"); 
		 if (ky_year == null || ky_year.length() <= 0 ) {
			 ky_year = RegisterDate.getCurrKqYear(this.frameconn);
		 }
		 HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		 String table = (String) hm.get("table");
		 ArrayList yearlist=this.getYear();		 
		 ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);	
		 StringBuffer sql_str = new StringBuffer();
		 StringBuffer cond_str = new StringBuffer();
		 HashMap map = new HashMap();
		 String columns = "";		 
	     sql_str.append("select ");
		 for (int i = 0; i < fieldlist.size(); i++)
		  {
			 FieldItem field = (FieldItem) fieldlist.get(i);
			 if("q03z0".equals(field.getItemid()))
			 {
				 sql_str.append(field.getItemid()+",");
                 columns="q03z0,"+columns;
			 }
			 else if("state".equals(field.getItemid()))
			 {
				 sql_str.append("");
			 }else{
			 columns=columns+field.getItemid().toString()+",";
			 sql_str.append(field.getItemid());
		     sql_str.append(",");
			 }				  
		  }
		  String have_accepted = "";
		  columns=columns+"scope";
          sql_str.append("scope");
          KqParam kqParam = KqParam.getInstance();
          String self_accept_month_data = kqParam.getSelfAcceptMonthData();
          if("1".equalsIgnoreCase(self_accept_month_data)){
			 DbWizard dbWizard = new DbWizard(frameconn);
			 have_accepted = dbWizard.isExistField("Q05", "accepted", false) == true ? "1" : "0";
			 
			  if("1".equals(have_accepted))
				  columns=columns+",accepted";
			  if("1".equals(have_accepted))
				  sql_str.append(",accepted");
          }
          this.getFormHM().put("have_accepted", have_accepted);
		  cond_str.append(" from Q05 where a0100 ='");
		  cond_str.append(userView.getA0100());
		  cond_str.append("' and UPPER(nbase)='"+userView.getDbname().toUpperCase()+"'");		 
	      if(!(ky_year==null|| "".equals(ky_year)))
		  {
	    	  cond_str.append(" and q03z0 like '"+ky_year+"-%'");			 
		  }else
		  {
			  if(yearlist!=null&&yearlist.size()>0)
			  {
				  CommonData yearvo=(CommonData)yearlist.get(0);
				  ky_year=yearvo.getDataValue();
				  cond_str.append(" and q03z0 like '"+ky_year+"-%'");	
			  }
			  
		  }
	      /**月汇总表中存在以 -PT结束的数据，这样的数据不属于月汇总的数据不应该查询出来 wangy**/
	      cond_str.append(" and not q03z0 like '%-PT'");
	      /**结束**/
	      map=count_Leave();
		  this.getFormHM().put("flist", this.getField(table));
		  this.getFormHM().put("sql",sql_str.toString());
		  this.getFormHM().put("com",columns);
		  this.getFormHM().put("where",cond_str.toString()); 
		  this.getFormHM().put("slist",this.getYear());
		  this.getFormHM().put("kqItem_hash",map);
		  this.getFormHM().put("kq_year", ky_year);
		 

		  
	}
	/**
	  * 考勤规则的一个hashmap集
	  * @return
	  * @throws GeneralException
	  */
	 public HashMap count_Leave() throws GeneralException
	 {
	    	RowSet rs=null;	    	
	    	String kq_item_sql="select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";    	    	
	    	
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	
	    	HashMap hashM=new HashMap();
	    	String fielditemid="";
	    	try
	    	{
	    	   rs =dao.search(kq_item_sql);
	    	   while(rs.next())
	    	   { 
	    		   HashMap hashm_one=new HashMap();	    		  
	    		   if(rs.getString("fielditemid")==null||rs.getString("fielditemid").length()<=0)
	    			   continue;
	    		   ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    
	    		   for(int i=0;i<fielditemlist.size();i++)
	   	    	   {
	   	   	          FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	   	   	          fielditemid=rs.getString("fielditemid");	   	   	          
	   	   	          if(fielditemid.equalsIgnoreCase(fielditem.getItemid()))
	   	   	          {
	   	   	            //System.out.println(fielditemid+"---------"+fielditem.getItemid());
	   	   	            hashm_one.put("fielditemid",rs.getString("fielditemid"));
		    		    hashm_one.put("has_rest",PubFunc.DotstrNull(rs.getString("has_rest")));
		    		    hashm_one.put("has_feast",PubFunc.DotstrNull(rs.getString("has_feast")));
		    		    hashm_one.put("item_unit",PubFunc.DotstrNull(rs.getString("item_unit")));
		    		    hashm_one.put("sdata_src",PubFunc.DotstrNull(rs.getString("sdata_src")));
		    		    hashM.put(fielditemid,hashm_one);
		    		    continue;
	   	   	          }
	   	    	   }
	    		   
	    	   }
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(e);
	    	}finally{
	        	if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
	        }
	    	return hashM;	    	
	 }
}
