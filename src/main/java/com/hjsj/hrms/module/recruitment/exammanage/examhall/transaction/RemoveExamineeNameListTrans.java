package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamineeNameListBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：RemoveExamineeNameListTrans 
 * 类描述：考生名单--移除考场
 * 创建人：sunming 
 * 创建时间：2015-11-3
 * 
 * @version
 */
public class RemoveExamineeNameListTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			/**选中的待移除考生**/
			ArrayList list = (ArrayList) this.getFormHM().get("ids");
			/**考场id**/
			String hallId = (String) this.getFormHM().get("hall_id");
			ExamineeNameListBo bo = new ExamineeNameListBo(this.getFrameconn(),this.userView);
			//移除考生名单
			bo.removeExamineeNameList(list,hallId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
