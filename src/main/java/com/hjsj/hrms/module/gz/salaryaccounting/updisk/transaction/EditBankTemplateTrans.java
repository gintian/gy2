package com.hjsj.hrms.module.gz.salaryaccounting.updisk.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.updisk.businessobject.BankDiskSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x 类名称：EditBankTemplateTrans 类描述：编辑银行报盘交易类 创建人：sunming 创建时间：2015-9-7
 * 
 * @version
 */
public class EditBankTemplateTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		try {
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			/**银行模板id**/
			String bankid = (String) this.getFormHM().get("bankid");
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(), Integer
					.parseInt(salaryid), this.userView);
			/**opt=0时，获取行首行末标示的map =1时，获取银行模板项目列表**/
			String opt = (String) this.getFormHM().get("opt");
			if("0".equals(opt)){
				String type = (String) this.getFormHM().get("type");
				//获取行首行末标示，及scope =0私有，=1共享
				HashMap map = bo.getCheckAndFormat(bankid);
				this.getFormHM().put("map", map);
			}else if("1".equals(opt)){
				// type=0 新增， type=1编辑
				String type = (String) this.getFormHM().get("type");
				//银行模板列表的项目
				ArrayList list = new ArrayList();
				if ("0".equals(type)) {
					list = bo.getBankItemInfo(bankid, 3);
				} else {
					list = bo.getBankItemInfo(bankid, 1);
				}
				this.getFormHM().put("data", list);
			}else{
				// 根据工资类别id得到类别下面的所有项目列表
				ArrayList list = (ArrayList) this.getFormHM().get("selectList");
				ArrayList bankitemList = bo.getAllBankItem(list,bankid);
				this.getFormHM().put("data", bankitemList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
