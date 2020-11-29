package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;

public class StartMailModelJob implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		Connection conn = null;
		try {
			RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
	        if(sms_vo==null) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));
			}
	        String param=sms_vo.getString("str_value");
	        if(param==null|| "".equals(param)) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));
			}
			conn = AdminDb.getConnection();
			SmsBo smsbo=new SmsBo(conn);
			smsbo.acceptMessage();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(conn);
		}
	}
}
