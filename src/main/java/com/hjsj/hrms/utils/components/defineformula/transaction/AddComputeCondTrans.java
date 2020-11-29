package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：AddComputeCondTrans 
 * 类描述：获取新增公式下拉框 
 * 创建人：zhaoxg
 * 创建时间：Jun 5, 2015 9:42:00 AM
 * 修改人：zhaoxg
 * 修改时间：Jun 5, 2015 9:42:00 AM
 * 修改备注： 
 * @version
 */
public class AddComputeCondTrans extends IBusiness {


	public void execute() throws GeneralException {
		String id = (String)this.getFormHM().get("id");//薪资类别id 或者人事异动模版id
		
		String module = (String)this.getFormHM().get("module");//模块号，1是薪资模块  2：薪资总额  3：人事异动  4...其他
		try {
			DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
			if("1".equals(module)){
				id = PubFunc.decrypt(SafeCode.decode(id));
				ArrayList list = bo.getFormulaCombox(id);
				this.getFormHM().put("data", list);
			}else if("3".equals(module)){//人事异动,gaohy
				ArrayList list = bo.getFormulaComboxTemp(id);
				this.getFormHM().put("data", list);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
