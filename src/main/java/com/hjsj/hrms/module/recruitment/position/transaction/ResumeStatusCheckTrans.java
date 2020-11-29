package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/***
 * 在点击职位推荐时进行校验当前选中人员
 * <p>Title: ResumeStatusCheckTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-9-1 上午10:09:13</p>
 * @author xiexd
 * @version 1.0
 */
public class ResumeStatusCheckTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String a0100s = (String)this.getFormHM().get("a0100s");
		try {
			PositionBo bo = new PositionBo(this.frameconn,new ContentDAO(this.frameconn),this.getUserView());
			a0100s = bo.getA0100s(a0100s);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.formHM.put("a0100s", a0100s);
			//this.formHM.put("object", this.getFormHM().get("object"));
			//this.formHM.put("record", this.getFormHM().get("record"));
			this.formHM.put("pageDescFro", this.getFormHM().get("pageDescFro"));
		}
	}

}
