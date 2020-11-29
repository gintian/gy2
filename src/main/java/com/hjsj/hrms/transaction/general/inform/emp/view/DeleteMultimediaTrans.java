package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 *<p>Title:DeleteMultimediaTrans</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */

public class DeleteMultimediaTrans extends IBusiness {

	public  void execute()throws GeneralException
	{
//		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String a0100 = (String)this.getFormHM().get("a0100");
		String dbname = (String)this.getFormHM().get("dbname");
//		String i9999 = (String)this.getFormHM().get("i9999");
		String kind = (String)this.getFormHM().get("kind");
		String isvisible=(String)this.getFormHM().get("isvisible");
		String multimediaflag = (String)this.getFormHM().get("multimediaflag");
		this.getFormHM().put("a0100",a0100);
//		this.getFormHM().put("dbname",dbname);
//		this.getFormHM().put("i9999",i9999);
		this.getFormHM().put("flag",multimediaflag);
		this.getFormHM().put("isvisible",isvisible);
//		System.out.println(i9999);
//		if(!(a0100==null || a0100.equals("")))
//		{
//			if(!(i9999==null || i9999.equals("")))
//			{
//				this.deleteMultimedia(a0100,dbname,i9999,dao);
//			}
//		}
		 ArrayList mediainfolist=(ArrayList)this.getFormHM().get("selectedlist");
	        if(mediainfolist==null||mediainfolist.size()==0)
	            return;
	        String sql="";
	        if("6".equals(kind))
			{
	        	sql="delete from " + dbname + "a00 ";
			}else if("0".equals(kind))
			{
				sql="delete from k00 ";
			}else if("9".equals(kind)){
				sql = "delete from h00 ";
			}else 
			{
				sql="delete from b00 ";
			}
	        
	        Connection conn = null;
	         try
		     {
	         	conn=this.getFrameconn();
	         	ContentDAO dao = new ContentDAO(conn);
	        	if(mediainfolist.size()>0)
	        	{
	        		LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(0); 
	        		if("6".equals(kind))
	    			{
	        			sql +="  where  a0100='"  + a0100 + "' and i9999 in(";
	    			}else if("0".equals(kind))
	    			{
	    				sql +="  where  e01a1='"  + a0100 + "' and i9999 in(";
	    			}else if("9".equals(kind)){
	    				sql += " where  h0100='"  + a0100 + "' and i9999 in(";
	    			}else 
	    			{
	    				sql +="  where  b0110='"  + a0100 + "' and i9999 in(";
	    			}
	        		
	        	}
	        	for(int i=0;i<mediainfolist.size();i++)
	           {
	        		if(i!=0)
	        			sql+= ",";
	            	LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(i); 
	           	   sql+= rec.get("i9999").toString(); 
	           }        	
	           sql+=")";
	           //System.out.println(sql);
	           if(mediainfolist.size()>0)
	           {
	        	   dao.update(sql);
	           }
	            	
	        }
	         catch(SQLException sqle)
			 {
			    sqle.printStackTrace();
			    throw GeneralExceptionHandler.Handle(sqle);
			 }
			 finally{
			   try{
			   	}catch(Exception e){}	
			 }
			
	}
	/*
	 * 
	 */
	public String[] getStringArr (String str)
	{
		String[] Stringarr = null;
		int tempnum = str.split("\\/").length;
		if(tempnum>0)
		{
			Stringarr = str.split("\\/");
		}
		return Stringarr;
	}
	/**
	 * 删除照片
	 * @param a0100
	 * @param dbpre
	 * @param i9999
	 * @param dao
	 */
	public void deleteMultimedia(String a0100,String dbpre,String i9999,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" delete "+dbpre+"a00 ");
		sb.append(" where a0100='"+a0100+"' ");
		sb.append(" and i9999=");
		sb.append(i9999);
//		System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
