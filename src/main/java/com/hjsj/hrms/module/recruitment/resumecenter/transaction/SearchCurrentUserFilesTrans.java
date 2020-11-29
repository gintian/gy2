package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeletePositionApplyTrans</p>
 * <p>Description:查询当前登录人给指定人员上传的简历附件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-07-31</p>
 * @author zx
 * @version 1.0
 */
public class SearchCurrentUserFilesTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try{
			String node_id = (String) this.getFormHM().get("node_id");
			String link_id = (String) this.getFormHM().get("link_id");
			String a0100 = PubFunc.decrypt((String) this.getFormHM().get("a0100"));
			String nbase = PubFunc.decrypt((String) this.getFormHM().get("nbase"));
			
			ResumeFileBo rfb = new ResumeFileBo(this.frameconn, this.userView);
			ArrayList list = rfb.getCurrentUserFiles(a0100, nbase, link_id);
			this.getFormHM().put("nodeId",node_id);
			this.getFormHM().put("list",list);
			
			if(!this.userView.hasTheFunction("3110702"))
				this.getFormHM().put("download", false);
			else
				this.getFormHM().put("download", true);
			//有删除权限
			if(this.userView.hasTheFunction("3110703"))
				this.getFormHM().put("del", 1);
			else
				this.getFormHM().put("del", 0);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			
		}
	}
}
