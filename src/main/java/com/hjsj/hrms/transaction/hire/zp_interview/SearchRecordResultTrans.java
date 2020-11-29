/*
 * Created on 2005-9-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_interview;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:SearchRecordResultTrans</p>
 * <p>Description:查询面试纪录</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 16, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchRecordResultTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String a0100 = (String)hm.get("a_a0100id"); 
        ArrayList list = new ArrayList();
        //ExecuteSQL executeSQL = new ExecuteSQL();
	    try
	    {
	    	String sql = "select log_id,a0100,staff_name,description from zp_process_log where a0100 = '"+a0100+"'";
	    	List rs = ExecuteSQL.executeMyQuery(sql,this.getFrameconn());
	    	for(int i=0;rs!=null&&i<rs.size();i++){
	    		LazyDynaBean rec=(LazyDynaBean)rs.get(i);
	    		RecordVo vo=new RecordVo("zp_process_log");
	    		vo.setString("log_id",rec.get("log_id").toString());
	    		vo.setString("a0100",rec.get("a0100").toString());
	    		vo.setString("staff_name",rec.get("staff_name").toString());
	    		vo.setString("description",PubFunc.nullToStr(rec.get("description").toString()));
	    		list.add(vo);
	    	}
	        this.getFormHM().put("description","");
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("zpProcessLoglist",list); 
	    }

	}

}
