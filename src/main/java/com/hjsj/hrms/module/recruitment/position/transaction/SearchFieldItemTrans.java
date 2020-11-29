package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 加载多选指标组件的下拉框
 * <p>Title: SearchFieldItemTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-29</p>
 * @author sunm
 * @version 1.0
 */
public class SearchFieldItemTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
	    try {
	    	String type = (String) this.getFormHM().get("type");
	    	ResumeFilterBo bo = new ResumeFilterBo(this.getFrameconn(),this.userView);
			ArrayList list = new ArrayList();
			if("1".equals(type)){
				list = bo.getFieldSet();
			}else{
				String value = (String) this.getFormHM().get("value");
				list = bo.getFieldItems(value);
			}
			this.getFormHM().put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
