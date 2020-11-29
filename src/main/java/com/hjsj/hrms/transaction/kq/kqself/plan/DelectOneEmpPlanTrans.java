package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DelectOneEmpPlanTrans  extends IBusiness {

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String apply_id =(String)hm.get("apply_id");
        String sql="delete from q31 where q3101=? and q31z5=?";
        ArrayList list=new ArrayList();
        list.add(apply_id);
        list.add("01");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
			 dao.delete(sql,list);
			 
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	}
}
