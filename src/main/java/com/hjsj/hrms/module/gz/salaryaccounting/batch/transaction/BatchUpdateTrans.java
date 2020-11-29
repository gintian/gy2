package com.hjsj.hrms.module.gz.salaryaccounting.batch.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.batch.businessobject.BatchUpdateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：BatchUpdateTrans 
 * 类描述：执行批量修改交易类
 * 创建人：sunming
 * 创建时间：2015-7-23
 * @version
 */
public class BatchUpdateTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		//薪资id
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		//薪资项目
		String itemid=(String)this.getFormHM().get("itemid");
		itemid=SafeCode.decode(itemid);
		//定义的公式
		String formula=(String)this.getFormHM().get("formula");	
		formula=SafeCode.decode(formula);
		formula = PubFunc.hireKeyWord_filter(formula);
		//定义的条件
		String cond=(String)this.getFormHM().get("cond");
		cond=SafeCode.decode(cond);
		//前台过滤条件
		String whl=(String)this.getFormHM().get("whl");
        whl=SafeCode.decode(whl);
		try
		{
			BatchUpdateBo gzbo=new BatchUpdateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			gzbo.batchUpdateItem(itemid, formula, cond,whl.trim()); 
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
