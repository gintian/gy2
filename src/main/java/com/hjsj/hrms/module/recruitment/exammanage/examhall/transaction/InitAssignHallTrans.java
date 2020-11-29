package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：InitAssignHallTrans 
 * 类描述：分排考场--personpicker控件单位权限控制
 * 创建人：sunming 
 * 创建时间：2015-11-20
 * 
 * @version
 */
public class InitAssignHallTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String batchId = (String) this.getFormHM().get("batchId");
			this.userView.getHm().put("batchId", batchId);
			RecruitPrivBo bo = new RecruitPrivBo();
			/**单位编码**/
			String b0110 = bo.getB0110(this.userView);
			/**单位权限**/
			String b0110Priv="";
			if("HJSJ".equals(b0110)){
				b0110Priv="";
			 }else{
				String[] s = b0110.split("`");
				for(int i=0;i<s.length;i++){
					b0110Priv += ","+s[i].substring(2);
				}
				b0110Priv = b0110Priv.substring(1);
			 }
			this.getFormHM().put("b0110", b0110Priv);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
