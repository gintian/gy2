package com.hjsj.hrms.utils.components.fielditemmultiselector.transaction;

import com.hjsj.hrms.utils.components.fielditemmultiselector.businessobject.GetFieldItemBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 加载多选指标组件的左侧待选指标
 * <p>Title: GetFieldSetTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-29</p>
 * @author sunm
 * @version 1.0
 */

public class GetFieldSetTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			String fieldset = (String) this.getFormHM().get("fieldset");
			//调用的模块=ZP：招聘模块
			String module = (String) this.getFormHM().get("module");
			GetFieldItemBo bo = new GetFieldItemBo(this.getFrameconn());
			ArrayList list = bo.getFieldSetList(fieldset, module);
			this.getFormHM().put("data", list);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
