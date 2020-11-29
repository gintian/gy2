/*
 * Created on 2005-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchOutlineListTrans extends IBusiness {

	 public void execute() throws GeneralException {
	  	
	  	 HashMap hm=(HashMap)this.getFormHM();//.get("requestPamaHM"); 
	  	 //取得调查表id及名称
	  	 String itemid=(String)hm.get("itemid");
	  	 if(itemid==null || "".equals(itemid))
	  	 {
	  	 	if(this.getFormHM().get("itemid")!=null)
	  	 	{
	  	 		itemid=this.getFormHM().get("itemid").toString();
	  	 	}
	  	 	else
	  	 	{
	  	 		itemid="0";
	  	 	}
	  	 }
	  	 else
	  	 {
	  	    this.getFormHM().put("itemid",itemid);
	  	 }
	  	String itemName=(String)hm.get("itemName");
	  	if(!(itemName==null || "".equals(itemName)))
	  	{
//	  		try
//			{
//	  			itemName=ChangeStr.ToGbCode(itemName);
//			}
//	  		catch(IOException ex)
//			{
//	  			ex.printStackTrace();
//			}
	  		this.getFormHM().put("itemName",itemName);
	  	}
	  	
	  	StringBuffer strsql=new StringBuffer();
	    strsql.append("select * from investigate_point where itemid='"+itemid+"'  order by pointid ");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("investigate_point");
	          vo.setString("pointid",this.getFrowset().getString("pointid"));
	         
	          String temp=this.getFrowset().getString("itemid");
	          if(temp==null || "".equals(temp))
	          {
	          	vo.setString("itemid","0");
	          }
	          else
	          {
	            vo.setString("itemid",this.getFrowset().getString("itemid"));
	          }
	          temp=this.getFrowset().getString("name");
	          if(temp==null || "".equals(temp))
	          {
	          		vo.setString("name","...");
	          }
	          else
	          {
	        	  vo.setString("name",this.getFrowset().getString("name"));
	          }
	          temp=this.getFrowset().getString("status");
	          if(temp==null || "".equals(temp))
	          {
	          	vo.setString("status","0");
	          }
	          else
	          {
	          	if("1".equals(temp))
	          	{
	          	vo.setString("status","1");
	          	}
	          	else
	          	{
	          		vo.setString("status","0");
	          	}
	          }
	          list.add(vo);
	      }
	      this.getFormHM().put("outlinelist",list);
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	       
	    }
        

	  }
}
