package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;


/**
* @ClassName: GetRelationSchemeListTrans 
* @Description: TODO(读取方案列表) 
* @author lis 
* @date 2015-7-15 下午05:15:19
 */
public class GetRelationSchemeListTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			ArrayList<LazyDynaBean> schemeList=new ArrayList<LazyDynaBean>();
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			SalaryInOutBo inOutBo=new SalaryInOutBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			schemeList = inOutBo.getReadRelations();
			this.getFormHM().put("schemeList",schemeList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
