package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.cardview.businessobject.CardViewBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * 获取信息
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
public class GetQnIdByPlanId extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			String type = (String)this.getFormHM().get("type");//区分
			
			if("1".equals(type)){//获取问卷号
				String planId = (String)this.getFormHM().get("planId");//计划号
				//planId= PubFunc.decrypt((SafeCode.decode(planId)));
				
				CardViewBo cardViewBo = new CardViewBo(this.frameconn, this.userView);
				String qnId = cardViewBo.getQnId(planId);
				
				this.getFormHM().put("qnId", qnId);
			} else if("2".equals(type)){//获取赞成、反对、弃权账号
				String text = "";
				
				String w0301 = (String)this.getFormHM().get("w0301");
				w0301= PubFunc.decrypt((w0301));
				ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
				Map<String, String> map = reviewFileBo.getPersonSet(w0301);
				
				
				this.getFormHM().put("approvalPersonSet", map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
