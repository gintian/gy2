package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:DeletePositionApplyTrans</p>
 * <p>Description:删除简历附件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-07-31</p>
 * @author zx
 * @version 1.0
 */
public class DeleteResumeFilesTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		boolean isOK = false;
		String id ="";
		String path = "";
		try{
			id = PubFunc.decrypt((String)this.getFormHM().get("id"));
			path = (String)this.getFormHM().get("path");//解密
			ResumeFileBo  resumeFileBo = new ResumeFileBo(this.frameconn, this.userView);
			isOK = resumeFileBo.deleteFile(id,path);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("isOK", isOK);
			this.getFormHM().put("id", PubFunc.encrypt(id));
		}
	}
}
