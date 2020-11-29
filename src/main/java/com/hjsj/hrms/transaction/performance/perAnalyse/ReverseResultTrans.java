package com.hjsj.hrms.transaction.performance.perAnalyse;

import com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:统计结果反查</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 19, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ReverseResultTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String plan_id=(String)this.getFormHM().get("planIds");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, plan_id);
			if(!_flag){
				return;
			}
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String id=(String)hm.get("id");
			id = PubFunc.keyWord_reback(id);
			PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.getFrameconn(),this.userView);
			RecordVo planVo=bo.getPlanVo(plan_id);
			int object_type=planVo.getInt("object_type");
			ArrayList reverseDataList=bo.getReversResult_list(plan_id,id,object_type);
			String statTitle = bo.getStatTitle(id);
			this.getFormHM().put("statTitle",statTitle);
			this.getFormHM().put("reverseDataList",reverseDataList);
			this.getFormHM().put("object_type",String.valueOf(object_type));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
