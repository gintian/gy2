package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SetFormulaValidTrans 
 * 类描述：设置公式状态
 * 创建人：zhaoxg
 * 创建时间：Jun 2, 2015 2:49:52 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 2, 2015 2:49:52 PM
 * 修改备注： 
 * @version
 */
public class SetFormulaValidTrans extends IBusiness {

	public void execute() throws GeneralException {
		String batch=(String)this.getFormHM().get("batch");//0:单个 1：批量
		String module = (String)this.getFormHM().get("module");//模块号，1是薪资
		String id=(String)this.getFormHM().get("id");//薪资类别id
		if (!"3".equals(module)){
			id = PubFunc.decrypt(SafeCode.decode(id));
		}
		String itemid=(String)this.getFormHM().get("itemid");//公式id
		String flag=(String)this.getFormHM().get("flag");//公式有效性
		
		String formulaType = (String)this.getFormHM().get("formulaType");//公式类别，2是审核公式
		DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
		try
		{
			if("2".equals(formulaType)){//人事和薪资一样，所以拿了出来，gaohy,2016-1-5
				bo.setGzSpFormulaValid(itemid, flag);
			}else if("1".equals(module)){//薪资，人事异动没有设置公式状态，有设置公式组状态
				bo.setGzFormulaValid(id, itemid, flag, batch);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
