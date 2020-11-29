package com.hjsj.hrms.module.gz.salaryaccounting.importmen.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.importmen.businessobject.ImportMenInfoBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @ClassName: HandImportMenTrans 
 * @Description: 手工引入
 * @author lis 
 * @date 2015-10-13 上午09:32:53
 */
public class HandImportMenTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String salaryid = (String) this.getFormHM().get("salaryid");	//薪资类别id
		String ids = (String) this.getFormHM().get("ids");//人员id串
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String actionType=(String) this.getFormHM().get("actionType");
		ImportMenInfoBo importMenInfoBo = new ImportMenInfoBo(this.getFrameconn(), this.userView, Integer.valueOf(salaryid));
		try
		{
			if("check".equalsIgnoreCase(actionType)){
				String msg=importMenInfoBo.isHaveRepeatedData(ids);
				this.getFormHM().put("msg", msg);
			}else if("import".equalsIgnoreCase(actionType)) {
				SalaryAccountBo accountBo = new SalaryAccountBo(this.getFrameconn(), this.userView, Integer.valueOf(salaryid));
				HashMap map = accountBo.getYearMonthCount();
				importMenInfoBo.importHandSelectedMen(ids, (String) map.get("ym"), (String) map.get("count"));//手工引入人员
				this.getFormHM().put("ff_bosdate", SafeCode.encode(PubFunc.encrypt((String) map.get("ym"))));
				this.getFormHM().put("count", SafeCode.encode(PubFunc.encrypt((String) map.get("count"))));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
//		finally {
//			try {
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
}