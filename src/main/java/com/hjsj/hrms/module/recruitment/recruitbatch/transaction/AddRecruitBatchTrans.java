package com.hjsj.hrms.module.recruitment.recruitbatch.transaction;

import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：AddRecruitBatchTrans 
 * 类描述：加载修改招聘批次页面
 * 创建人：sunming 
 * 创建时间：2015-10-27
 * 
 * @version
 */
public class AddRecruitBatchTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			RecruitBatchBo bo = new RecruitBatchBo(this.getFrameconn(),this.userView);
			/**type=1 新增 type=2 修改**/
			String type = (String) this.getFormHM().get("type");
			String id = "";
			ArrayList fields = bo.getField();
			if("1".equals(type)){
				ArrayList list = bo.getFormatList();
				this.getFormHM().put("list", list);
			}else{
				id = (String) this.getFormHM().get("id");
				ArrayList list = bo.getRecruitBatchList(id,fields);
				this.getFormHM().put("list", list);
				
				/**修改的权限控制**/
				boolean priv = this.userView.hasTheFunction("3110002");
				this.getFormHM().put("priv", priv);
			}
			this.getFormHM().put("id", id);
			this.getFormHM().put("type", type);
			this.getFormHM().put("fields", fields);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
