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

import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchOutlineTrans extends IBusiness {

	 /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String pointid=(String)hm.get("pointid");
        String status=(String)hm.get("status");
        if(status==null || "".equals(status))
        {
        	status="0";
        }
        
        //取得调查表id及名称
	  	 String itemid=(String)hm.get("itemid");
	  	 if(itemid!=null)
	  	 {
	  		 this.getFormHM().put("itemid",itemid);
	  	 }
	  	 else
	  	 {
	  	 	itemid=this.getFormHM().get("itemid").toString();
	  	 }
	  	  	 
	  	//String itemName=(String)hm.get("itemName");
	  	String itemName="";
	  	ContentDAO dao=new ContentDAO(this.getFrameconn());
	  	try
	  	{
	    	 RecordVo avo=new RecordVo("investigate_item");
	     	 avo.setString("itemid", itemid);
	     	 avo=dao.findByPrimaryKey(avo);
	     	 itemName=avo.getString("name");
	  	}catch(Exception e)
	  	{
	  		e.printStackTrace();
	  	}
	  	
	  	if(!(itemName==null || "".equals(itemName)))
	  	{
	  		/*
	  		try
			{
	  			itemName=ChangeStr.ToGbCode(itemName);
			}
	  		catch(IOException ex)
			{
	  			ex.printStackTrace();
			}
			*/
	  		this.getFormHM().put("itemName",itemName);	  		
	  	}
	      	
      
        String flag=(String)this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        RecordVo vo=new RecordVo("investigate_point");
        if("1".equals(flag))
            return;
        cat.debug("------>investigate_point_id====="+itemid);
        try
        {
           vo.setString("pointid",pointid);
           vo=dao.findByPrimaryKey(vo);
           vo.setString("status",status);
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("outlineTb",vo);
        }
    }

}
