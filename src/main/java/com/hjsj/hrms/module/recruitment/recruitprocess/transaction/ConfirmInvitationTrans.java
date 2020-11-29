package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.RecruitProcessBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 接受或拒绝邀请
 * 招聘确认链接处理
 * @author wangjl
 */
public class ConfirmInvitationTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			//confirm 45号代码类的值
			String confirm = (String) hm.get("confirm");
			String a0100 = (String) hm.get("a0100");
			String zp_pos_id = (String) hm.get("zp_pos_id");
			String link_id = (String) hm.get("link_id");
			String status = (String) hm.get("status");
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			
			RecruitProcessBo bo = new RecruitProcessBo(this.frameconn, this.userView);
			boolean changeConfirmState = bo.changeConfirmState(confirm, a0100, dbname, zp_pos_id, link_id, status);
			if(changeConfirmState) {
				if("1".equals(confirm))
					this.formHM.put("message", "1");
				else
					this.formHM.put("message", "0");
			}else
				this.formHM.put("message", "2");
			
			 EmployNetPortalBo employBo = new EmployNetPortalBo(this.frameconn);
			 String applyMessage=employBo.getApplyMessage(a0100);
	         this.formHM.put("applyMessage", applyMessage);
				
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
