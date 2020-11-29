package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchCharFieldTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		try{
			String hireMajor = SafeCode.decode((String)this.getFormHM().get("hireMajor"));
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
			String isCharField = parameterSetBo.getIsCharField(hireMajor);//招聘专业是否是字符型字段
			this.getFormHM().put("isCharField", isCharField);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
