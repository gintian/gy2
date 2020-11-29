/*
 * Created on 2005-9-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_sumup;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveZpjobTrans</p>
 * <p>Description:保存招聘活动</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 18, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveZpjobTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo vo=(RecordVo)this.getFormHM().get("zpSumupvo");
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
		ArrayList itemlist = (ArrayList) this.getFormHM().get("SelectItemSave");
		int real_invite_amount = 0;
		if(vo==null)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_job set description='"+vo.getString("description")+"',real_invite_amount="+
							vo.getString("real_invite_amount")+" where zp_job_id='"+vo.getString("zp_job_id")+"'";
	        	dao.update(sql,list);
	        	for(int i=0;i<itemlist.size();i++){
	        		HashMap hm=(HashMap)itemlist.get(i); 
	        		String detail_id = (String)hm.get("typeKey");
	        		String realcharge = (String)hm.get("typeValue");
             		String strsql = "update zp_job_details set realcharge = "+realcharge+" where detail_id = "+detail_id;
             		dao.update(strsql,list);
	        	}  
	        	sql="select real_invite_amount from zp_job where plan_id='"+plan_id_value+"'";
	        	this.frowset = dao.search(sql);
	        	while(this.frowset.next()){
	        		real_invite_amount = real_invite_amount + this.frowset.getInt("real_invite_amount");
	        	}
	        	sql = "update zp_plan set real_invite_amount = "+real_invite_amount+" where plan_id = '"+plan_id_value+"'";
	        	dao.update(sql,list);
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}
