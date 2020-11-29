package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SearchGzDataSubmitTypeTrans 
 * 类描述： 获取工资子集提交方式
 * 创建人：zhaoxg
 * 创建时间：Oct 15, 2015 3:33:38 PM
 * 修改人：zhaoxg
 * 修改时间：Oct 15, 2015 3:33:38 PM
 * 修改备注： 
 * @version
 */
public class SearchGzDataSubmitTypeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String type=(String)this.getFormHM().get("type");    // 1:工资发放  2：工资审核 3：薪资汇总审批 zhaoxg add 2015-2-2		
		try
		{
			ArrayList list=new ArrayList();
			if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			/**取得全部的计算公式*/
			list=gzbo.getSubmitTypeList(salaryid);
			this.getFormHM().put("data", list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		

	
	}

}
