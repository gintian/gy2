package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetCultureCodeItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String codesetid=(String)this.getFormHM().get("codesetid");
			ArrayList  list = new ArrayList();
			if("#".equals(codesetid))
			{
				list.add(new CommonData("#","请选择..."));
				this.getFormHM().put("itemList",list);
			}
			else
			{
				ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
				list=parameterSetBo.getCodeItem(codesetid);
				this.getFormHM().put("itemList",list);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
