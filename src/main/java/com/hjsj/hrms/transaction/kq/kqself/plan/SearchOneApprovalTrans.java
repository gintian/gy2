package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:个人假计划报批</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Feb 5, 2009:3:54:20 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SearchOneApprovalTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String apply_id =(String)hm.get("apply_id");
		apply_id = PubFunc.decrypt(apply_id);
		String param =(String)hm.get("param");
		param = PubFunc.decrypt(param);
		String dtable=(String)hm.get("dtable");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(apply_id!=null){
			//dao.update("update '"+dtable+"' set q31z5='02' where q3101='"+apply_id+"'",new ArrayList());
			dao.update("update "+dtable+" set q31z5='02'  where q3101='"+apply_id+"'", new ArrayList()); 
		}
		
		}catch(Exception e)
		 {
			 e.printStackTrace();
		 }    
	}

}
