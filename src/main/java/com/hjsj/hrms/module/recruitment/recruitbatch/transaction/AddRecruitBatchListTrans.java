package com.hjsj.hrms.module.recruitment.recruitbatch.transaction;

import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：AddRecruitBatchListTrans 
 * 类描述：创建招聘批次页面-招聘渠道、招聘流程store加载类
 * 创建人：sunming 
 * 创建时间：2015-10-27
 * 
 * @version
 */
public class AddRecruitBatchListTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			/**type=1 招聘渠道   type=2 招聘流程**/
			String type = (String) this.getFormHM().get("type");
			String z0153Id = (String) this.getFormHM().get("z0153Id");
			RecruitBatchBo bo = new RecruitBatchBo(this.getFrameconn(),this.userView);
			ArrayList list = new ArrayList();
			list = bo.getRecruitChannelOrFlow(type,z0153Id);
			HashMap map = new HashMap();
            map.put("itemid", "");
            map.put("itemdesc", "请选择");
			list.add(0, map);
			this.getFormHM().put("data", list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
