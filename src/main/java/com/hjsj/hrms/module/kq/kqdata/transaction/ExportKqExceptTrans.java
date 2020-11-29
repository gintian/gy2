package com.hjsj.hrms.module.kq.kqdata.transaction;

import com.hjsj.hrms.module.kq.kqdata.businessobject.KqExceptService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqExceptServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

/**
 * 出勤异常情况  输出Excel交易类
 * 
 * @date 2019.8.26
 * @author xuanz
 *
 */
public class ExportKqExceptTrans extends IBusiness { 
	
	@Override
	public void execute() throws GeneralException {
		try {
			String jsonStr = (String)this.formHM.get("jsonStr");
			//获取前台json数据
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);
			KqExceptService service =new KqExceptServiceImpl(this.getUserView(), this.getFrameconn());
			String fileName=service.exportKqExceptTable(jsonObj);
			this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
	      }catch (Exception e) {
	    	   e.printStackTrace();
		       throw GeneralExceptionHandler.Handle(e);
	      }
	}
}
