package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:SavePartRestrictParam.java</p>
 * <p>Description:保存考核计划参数设置部分指标设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-02-12 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SavePartRestrictParam extends IBusiness
{

    public void execute() throws GeneralException
    {

	ArrayList badly_partRestrict = (ArrayList)this.getFormHM().get("Badly_partRestrict");
	ArrayList fine_partRestrict =(ArrayList)this.getFormHM().get("Fine_partRestrict");
	
	this.getFormHM().put("Badly_partRestrict", badly_partRestrict);
	this.getFormHM().put("Fine_partRestrict", fine_partRestrict);
    }

}
