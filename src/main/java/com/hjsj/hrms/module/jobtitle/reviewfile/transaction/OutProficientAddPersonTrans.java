package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.OutProficientBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 上会材料-单独生成账号密码-引入专家
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class OutProficientAddPersonTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			String msg = "";
			OutProficientBo outProficientBo = new OutProficientBo(this.getFrameconn());
			
			String w0301 = (String)this.getFormHM().get("w0301");//会议编号
			w0301 = PubFunc.decrypt(w0301);
			String w0501 = (String)this.getFormHM().get("w0501");//申报编号
			w0501 = PubFunc.decrypt(w0501);
			ArrayList<String> personidList = new ArrayList<String>();
			personidList = (ArrayList<String>)this.getFormHM().get("personidList");//人员编号list
			
			msg = outProficientBo.createPerson(w0301, w0501, personidList);
				
			
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
