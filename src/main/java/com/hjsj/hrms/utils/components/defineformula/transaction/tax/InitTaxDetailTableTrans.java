package com.hjsj.hrms.utils.components.defineformula.transaction.tax;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitTaxDetailTableTrans 
 * 类描述： 初始化税率表
 * 创建人：zhaoxg
 * 创建时间：Nov 25, 2015 6:00:00 PM
 * 修改人：zhaoxg
 * 修改时间：Nov 25, 2015 6:00:00 PM
 * 修改备注： 目前仅限计算公式使用
 * @version
 */
public class InitTaxDetailTableTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String taxid = (String) this.getFormHM().get("taxid");
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String opt = (String) this.getFormHM().get("opt");
		ArrayList list = new ArrayList();
		try{
			DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
			if("1".equals(opt)){
				String taxid1 = (String) this.getFormHM().get("taxid1");//由于store的load方法不能修改以及传递过的参数，所以用taxid1来代表修改过的税率表号
				taxid = taxid1==null?taxid:taxid1;
				list = bo.getTaxDetailTableList(taxid);
			}else if("2".equals(opt)){
				list = bo.getTaxTypeList();
			}else if("3".equals(opt)){
				list = bo.getIncomeList(salaryid);
			}else if("4".equals(opt)){
				list = bo.getTaxrate();
			}else if("5".equals(opt)){
				
			}
			this.getFormHM().put("data", list);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
