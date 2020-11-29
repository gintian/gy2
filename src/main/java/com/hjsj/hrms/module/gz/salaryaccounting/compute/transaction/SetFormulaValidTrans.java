package com.hjsj.hrms.module.gz.salaryaccounting.compute.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
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

	@Override
    public void execute() throws GeneralException {
		String batch=(String)this.getFormHM().get("batch");//0:单个 1：批量
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String itemid=(String)this.getFormHM().get("itemid");
		String flag=(String)this.getFormHM().get("flag");
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("update salaryformula set useflag='");
			buf.append(flag);
			buf.append("' where salaryid=");
			buf.append(salaryid);
			/**单个设置计算公式有效*/
			if("0".equalsIgnoreCase(batch))
			{
				buf.append(" and itemid='");
				buf.append(itemid);
				buf.append("'");
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
