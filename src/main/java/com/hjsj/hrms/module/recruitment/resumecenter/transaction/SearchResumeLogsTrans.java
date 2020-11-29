package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeLogBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:SearchResumeTrans</p>
 * <p>Description:查询操作日志信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:2016-01-29</p>
 * @author zx
 * @version 1.0
 * 
 */
public class SearchResumeLogsTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try{
			String a0100 = (String) this.getFormHM().get("a0100");
			while(isEncrypt(a0100))
				a0100 = PubFunc.decrypt(a0100);
			String nbase = (String) this.getFormHM().get("nbase");
			nbase = PubFunc.decrypt(nbase);
			String positionid = (String) this.getFormHM().get("positionid");
			while(isEncrypt(positionid))
				positionid = PubFunc.decrypt(positionid);
			
			ResumeLogBo bo = new ResumeLogBo(this.frameconn, this.userView);
			String jsonStr = bo.searchLogs(positionid, a0100, nbase);
			this.getFormHM().put("jsonStr", jsonStr);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	 /**
     * 判断id是否加密过
     * @param id
     * @return true 加密过
     * @throws GeneralException 
     */
    private static boolean isEncrypt(String id) throws GeneralException{
    	Pattern pattern = Pattern.compile("\\d+");
    	Matcher matcher = pattern.matcher(id);
    	return !matcher.matches();
    }
}
