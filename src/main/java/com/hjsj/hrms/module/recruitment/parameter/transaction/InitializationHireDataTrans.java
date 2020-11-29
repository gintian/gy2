package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitializationHireDataTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		String msg="0";
		try
		{
			/**=1全部删除=0按选择的删除*/
			String isAllDelete=(String)this.getFormHM().get("isAllDelete");
			/**=0全部数据=1是按时间范围初始化*/
			String type=(String)this.getFormHM().get("type");
			/**招聘表的表名串*/
			String tableStr=(String)this.getFormHM().get("tableStr");
			/**人员主集和子集*/
			String setStr=(String)this.getFormHM().get("setStr");
			/**开始时间*/
			String stime=(String)this.getFormHM().get("stime");
			/**结束时间*/
			String etime=(String)this.getFormHM().get("etime");
			
			ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
			
			if("1".equals(isAllDelete))
			{
				bo.initHireData(type, stime, etime,"","");
			}
			else
			{
				if(setStr==null||setStr.trim().length()==0)
					setStr="#";
				bo.initHireData(type, stime, etime,"/"+tableStr.toUpperCase()+"/", setStr);
			}
			
		}
		catch(Exception e)
		{
			msg="1";
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			this.getFormHM().put("msg", msg);
		}
		
	}

}
