package com.hjsj.hrms.transaction.performance.implement.kh_object;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 获取查询指标类
 * JinChunhai
 */

public class GetFieldsTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		String tableName = (String) this.getFormHM().get("tableName");
		PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(), this.getUserView());
		ArrayList fieldList = bo.getFieldlist(tableName);
		this.getFormHM().put("fieldList", fieldList);
		
    }
    
}
