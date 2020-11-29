/*
 * Created on 2006-4-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QueryPersonExamTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String querycondition=(String)this.getFormHM().get("querycondition");
		RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
		String dbpre = "";
        if(rv!=null)
        {
            dbpre=rv.getString("str_value");
        } 
        String strwhere="";
        if(querycondition!=null && querycondition.length()>0)
		    strwhere="from zp_exam_report," + dbpre + "a01 where zp_exam_report.a0100=" + dbpre + "a01.a0100 and " + dbpre+ "a01.a0101 like '%" + querycondition + "%'";
		else
			strwhere="from zp_exam_report," + dbpre + "a01 where zp_exam_report.a0100=" + dbpre + "a01.a0100";
			
        this.getFormHM().put("strwhere",strwhere);
	}

}
