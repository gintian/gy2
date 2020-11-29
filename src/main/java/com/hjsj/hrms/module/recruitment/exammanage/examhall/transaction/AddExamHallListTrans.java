package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：AddExamHallListTrans
 * 类描述：添加考场--添加按钮
 * 创建人：sunming 
 * 创建时间：2015-11-30
 * 
 * @version
 */
public class AddExamHallListTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			/**批次编号**/
			String batchId = String.valueOf(this.getFormHM().get("batchId"));
			/**选中的考场**/
			ArrayList alist = (ArrayList) this.getFormHM().get("array");
			
			ExamHallBo bo = new ExamHallBo(this.getFrameconn(),this.userView);
			ArrayList list = bo.getAddExamHallList(batchId,alist);
			this.getFormHM().put("list", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
