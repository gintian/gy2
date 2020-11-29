package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DateCountLinkage 
 * 类描述： 获取业务日期和次数
 * 创建人：zhaoxg
 * 创建时间：Dec 22, 2015 2:00:56 PM
 * 修改人：zhaoxg
 * 修改时间：Dec 22, 2015 2:00:56 PM
 * 修改备注： 
 * @version
 */
public class DateCountLinkage extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
		String opt = (String) this.getFormHM().get("opt");		
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		if("date".equals(opt)){
			int id = Integer.parseInt(salaryid);
			ArrayList list = spbo.getOperationDateListSP(id);
			this.getFormHM().put("data", list);
		}else if("count".equals(opt)){
			String date_count = (String) this.getFormHM().get("date_count");
			ArrayList list = spbo.getOperationCoundList(date_count,salaryid);
			this.getFormHM().put("data", list);
		}else{
			this.getFormHM().put("data", new ArrayList());
		}
	}

}
