package com.hjsj.hrms.transaction.hire.demandPlan.hireOrder;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:DeleteSelectRecordTrans
 * </p>
 * <p>
 * Description:删除选中的记录
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2009-5-12:下午03:48:28
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 */
public class DeleteSelectRecordTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	ContentDAO dao =null;
   
	dao = new ContentDAO(this.getFrameconn());
	try {
		 StringBuffer z04 = new StringBuffer();
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			if(hm.get("z0400")==null|| "".equals(hm.get("z0400"))){
				return;
			}
			
			String[]z0400s = hm.get("z0400").toString().split(",");
			for(int i=0;i<z0400s.length;i++){
				z04.append(" or z0400 ='"+z0400s[i]+"'");
				
			}
		dao.delete(" delete from z04 where"+z04.substring(3), new ArrayList());
	} catch (SQLException e) {
		throw GeneralExceptionHandler.Handle(e);
	}
	/*
	HashMap hm = this.getFormHM();
	String name = (String) hm.get("data_table_table");
	cat.debug("table name=" + name);
	ArrayList list = (ArrayList) hm.get("data_table_record");	
	String setname = name;	

	try
	{
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    dao.deleteValueObject(list);

	} catch (Exception ex)
	{
	    throw GeneralExceptionHandler.Handle(ex);
	}
	*/
    }

}
