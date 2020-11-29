package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * @Description:增加、修改、删除校验规则
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:32:00 
 * @version: 1.0
 */
public class OperateValidateRulesTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String flag = (String) this.getFormHM().get("flag");
			SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
			if ("add".equals(flag)) {//新增校验规则
				String checkname = (String) this.getFormHM().get("checkname");
				checkname = PubFunc.keyWord_filter(checkname);
				String checkField = (String) this.getFormHM().get("checkField");
				String condition = (String) this.getFormHM().get("condition");
				condition = PubFunc.keyWord_reback(condition);
				int forcestate = (Integer) this.getFormHM().get("forcestate");
				int valid = (Integer) this.getFormHM().get("valid");
				int checkId = bo.getMaxCheckedid();
				//condition = SafeCode.decode(condition);
				bo.addValidateRules(checkId, checkname, checkField, condition, forcestate, valid);
			}else if ("updataAll".equals(flag)) {//修改校验规则全部参数
				int checkId = (Integer) this.getFormHM().get("checkId");
				String checkname = (String) this.getFormHM().get("checkname");
				checkname = PubFunc.keyWord_filter(checkname);
				String checkField = (String) this.getFormHM().get("checkField");
				String condition = (String) this.getFormHM().get("condition");
				condition = PubFunc.keyWord_reback(condition);
				int forcestate = (Integer) this.getFormHM().get("forcestate");
				int valid = (Integer) this.getFormHM().get("valid");
				//condition = SafeCode.decode(condition);
				bo.updataValidateRules(checkId, checkname, checkField, condition, forcestate, valid);
			}else if ("update".equals(flag)) {//修改校验规则部分参数
				String parameter = (String) this.getFormHM().get("parameter");
				//parameter = SafeCode.decode(parameter);
				String[] datArray=parameter.split(",");
				int checkId = Integer.parseInt(datArray[0]) ;
				int forcestate = Integer.parseInt(datArray[1]);
				int valid =  Integer.parseInt(datArray[2]);
				String condition = datArray[3];
				condition = PubFunc.keyWord_reback(condition);
				bo.updataValidateRules(checkId, forcestate, valid,condition);
			}else if ("del".equals(flag)) {//删除校验规则
				String checkedids = (String)this.getFormHM().get("checkedids");
				if (null!=checkedids&&!"".equals(checkedids)){
					bo.deleteValidateRules(checkedids);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
