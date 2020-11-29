package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
/**
 * @Description:查询已选的上报单位
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:24:43 
 * @version: 1.0
 */
public class GetSelectedUnitTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			SetupSchemeBo bo = new SetupSchemeBo(userView,this.frameconn);
			JSONArray unitArray = bo.getSelectReportingUnit();
			String saveFilePath = FileUtil.getSaveFilePath();
			this.getFormHM().put("unitArray", unitArray);
			this.getFormHM().put("saveFilePath", saveFilePath);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
