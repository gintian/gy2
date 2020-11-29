package com.hjsj.hrms.transaction.kq.kqself.details;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SearchMonthDetailsHourTrans extends IBusiness{
	private ArrayList operList(String mm) throws GeneralException
	{
		ArrayList operlist=new ArrayList();
		StringBuffer strb=new StringBuffer();
		ContentDAO duration_dao = new ContentDAO(this.getFrameconn());
		strb.append("select kq_year,kq_duration,kq_start,kq_end,finished from kq_duration where kq_year='");
		strb.append(mm);
		strb.append("' order by kq_duration");
		String tem=(String)this.getFormHM().get("tem");
		String finished="";
		try {
			this.frowset = duration_dao.search(strb.toString());           
			while (this.frowset.next()) {
				
				if(tem==null|| "".equals(tem))
				{
					finished=this.frowset.getString("finished");
					if(finished!=null&& "0".equals(finished))
					{
						tem=this.frowset.getString("kq_year")+"-"+this.frowset.getString("kq_duration");
						this.getFormHM().put("tem",tem);
					}
				}					
				CommonData durationvo= new CommonData(this.frowset.getString("kq_year")+"-"+this.frowset.getString("kq_duration"),this.frowset.getString("kq_duration")+'('+ PubFunc.FormatDate(this.frowset.getDate("kq_start")).replaceAll("-","\\.")+'-'+PubFunc.FormatDate(this.frowset.getDate("kq_end")).replaceAll("-","\\.")+')');
				operlist.add(durationvo);
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
       
		
		return operlist;
	}
	
	private ArrayList getYear() throws GeneralException
	{
		ArrayList rlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ystr = new StringBuffer();
		ystr.append("select distinct kq_year from kq_duration where finished='0'");
		try{
			
			
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
        
		
		return rlist;
	}
	private ArrayList getField(String table)
	{
		  ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		  ArrayList list=new ArrayList();
		  for(int i=0;i<fieldlist.size();i++)
			  {	
			     FieldItem field=(FieldItem)fieldlist.get(i);
				 field.setValue("");
				 field.setViewvalue("");
				 if("b0110".equals(field.getItemid())|| "a0101".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "e01a1".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "q03z3".equals(field.getItemid())|| "q03z5".equals(field.getItemid())|| "state".equals(field.getItemid()))
				   field.setVisible(false);
				 else if("q03z0".equals(field.getItemid()))
						field.setVisible(true);
				 else
				 {
					if("1".equals(field.getState()))
					{
						field.setVisible(true);
					}else
					{
						field.setVisible(false);
					}
				 }
				 FieldItem field_n=(FieldItem)field.cloneItem();
				 list.add(field_n);
			 }
		
		return list;
	}
	
	 private String getFirstOfList(ArrayList list) 
	   {
			CommonData vo = (CommonData) list.get(0);
			return vo.getDataValue();
		}
	 private void SetSql(String year,String flag,String month,String more)throws GeneralException
	 {
		 if(month==null||month.length()<=0)
		 {
			 month="";
		 }  
		 if(month.indexOf(year)==-1)
		   {
			   month=year+"-"+RegisterDate.getNoSealDuration(this.getFrameconn(),year);
		   }
		  StringBuffer sql = new StringBuffer();
		  StringBuffer cond = new StringBuffer();
		  String columns = "";
		  ArrayList fieldlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
		  sql.append("select ");
		  for (int i = 0; i < fieldlist.size(); i++)
		  {
		     FieldItem field = (FieldItem) fieldlist.get(i);
			 columns=columns+field.getItemid().toString()+",";
			 sql.append(field.getItemid());
			 if (i!= fieldlist.size()- 1)
			    sql.append(",");
			 
		  }
			 
		 cond.append(" from Q03");
	     cond.append(" where a0100 ='");
		 cond.append(userView.getA0100());
		 cond.append("' and UPPER(nbase)='");
		 cond.append(userView.getDbname().toUpperCase()+"'");
		 
		 if("2".equals(flag))
		  {
			 if(more==null||more.length()<=0)
			  {
				 ArrayList sessionlist=RegisterDate.sessionDate(this.getFrameconn());
		    	   if(sessionlist!=null&&sessionlist.size()>0)
		    	   {
		    		   CommonData vo =(CommonData)sessionlist.get(0);
		    		   more=vo.getDataValue();
		    	   }else
		    	   {
		    		   Calendar now = Calendar.getInstance();
					   Date cur_d=now.getTime();
					   more=DateUtils.format(cur_d,"yyyy-MM"); 
		    	   }
			  }
			  ArrayList datelist= RegisterDate.getOneDurationDate(this.getFrameconn(),more);				  
			  String kq_start =datelist.get(0).toString();
			  String kq_end = datelist.get(datelist.size()-1).toString();
			  cond.append(" and q03z0 >='"+kq_start+"'");			   
			  cond.append(" and q03z0 <='"+kq_end+"'");
			  
		  }else
		  {
		       if(month==null||month.length()<=0)
		       {
		    	   ArrayList sessionlist=RegisterDate.sessionDate(this.getFrameconn());
		    	   if(sessionlist!=null&&sessionlist.size()>0)
		    	   {
		    		   CommonData vo =(CommonData)sessionlist.get(0);
		    		   month=vo.getDataValue();
		    	   }else
		    	   {
		    		   Calendar now = Calendar.getInstance();
					   Date cur_d=now.getTime();
					   month=DateUtils.format(cur_d,"yyyy-MM"); 
		    	   }
		    	   
		       }
		       ArrayList datelist= RegisterDate.getOneDurationDate(this.getFrameconn(),month);				  
			   String kq_start =datelist.get(0).toString();
			   String kq_end = datelist.get(datelist.size()-1).toString();
			   cond.append(" and q03z0 >='"+kq_start+"'");			   
			   cond.append(" and q03z0 <='"+kq_end+"'");
		    } 
		   this.getFormHM().put("sql",sql.toString());
		   this.getFormHM().put("com",columns);
		   this.getFormHM().put("where",cond.toString()); 
		   this.getFormHM().put("orderby","order by q03z0");
	 }

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList nlist=new ArrayList();
		 
		try{
		       HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		       String more =(String) hm.get("kq_month");
		       String year=(String)this.getFormHM().get("kq_years");
		       String tems=(String)this.getFormHM().get("tem");
		       String flag=(String)this.getFormHM().get("flag");
		       HashMap map = new HashMap();
		       
		       if("2".equals(flag))
		         { 
			    	 this.getFormHM().put("mess","2");
				       
		         }else{
		             this.getFormHM().put("mess","1"); 
		 	     }
		      
		        this.getFormHM().put("flist", this.getField("Q03"));
				this.getFormHM().put("slist",this.getYear());
				if((year==null|| "".equals(year)))
			    {
				        ArrayList years =this.getYear();
			            if (years.size()!=0)
			            {	
			              String tme=this.getFirstOfList(years);
			              year=tme;
			              nlist=this.operList(tme);
			            }else
			            {
			            	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
			            }
			   }else{
			 	  	     nlist=this.operList(year);
			   }
				map=count_Leave();
			   this.SetSql(year,flag,tems,more);
			   this.getFormHM().put("tlist",nlist);
			   this.getFormHM().put("kqItem_hash",map);
	    }
	    catch(Exception sqle)
		{
		  sqle.printStackTrace();
		  throw GeneralExceptionHandler.Handle(sqle);
	    }
	}
	/**
	  * 考勤规则的一个hashmap集
	  * @return
	  * @throws GeneralException
	  */
	 public HashMap count_Leave() throws GeneralException
	 {
//		    conn= AdminDb.getConnection();
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }
	    	return hashM;	    	
	 }
}
