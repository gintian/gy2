package com.hjsj.hrms.transaction.performance.evaluation.uniformRate;


import com.hjsj.hrms.businessobject.performance.uniformRate.UniformRateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:统一打分</p>
 * <p>Description:调用保存信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 28, 2008:10:41:26 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class UniformRateUpdateTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("gz_table_table");  //获取表名;
		String per_plan = name.substring(11, name.length()); //切割字段;
		cat.debug("table name="+name);
		ArrayList slist=(ArrayList)hm.get("gz_table_record");
		UniformRateBo pe=new UniformRateBo(this.getFrameconn(),slist,per_plan,this.userView);
		ArrayList per_planList = pe.getPer_plan(per_plan); //取得考核模板表的ID号;
		ArrayList pointListDB = pe.getPointListDB(per_planList);
		
		try {
			
			pe.getUpdateList(slist, pointListDB);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.updateValueObject(slist);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
