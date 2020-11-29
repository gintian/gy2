package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：AutoComputeExamineeTrans 
 * 类描述：考生分派自动统计
 * 创建人：sunming 
 * 创建时间：2015-11-17
 * 
 * @version
 */
public class AutoComputeExamineeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			/**需求单位**/
			String z0321 = (String) this.getFormHM().get("z0321");
			/**报考职位类别 z03中z0357**/
			String z0357 = (String) this.getFormHM().get("z0357");
			/**科目**/
			String subject = (String) this.getFormHM().get("subject");
			/**批次id**/
			String batchId = (String) this.getFormHM().get("batchId");
			ExamHallBo bo = new ExamHallBo(this.getFrameconn(),this.userView);
			ExamineeBo inBo = new ExamineeBo(this.frameconn,this.userView);
			inBo.isHasSubjects();
			int count = bo.toAutoComputeExaminee(z0321,z0357,subject,batchId);
			this.getFormHM().put("count", count);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
