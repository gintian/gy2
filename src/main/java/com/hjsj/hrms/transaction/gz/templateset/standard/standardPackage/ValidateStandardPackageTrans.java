package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardPackBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:验证新资标准包中是否包含标准 or 删除薪资标准包 </p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 6, 2007:4:07:36 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class ValidateStandardPackageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			
			String pkg_id=(String)this.getFormHM().get("pkg_id");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String  operate="";
			if(hm!=null)
				operate=(String)hm.get("operate");
			if(hm==null)  //验证新资标准包中是否包含标准
			{
				String info="0";   // 0:不包含  1:包含
				SalaryStandardPackBo bo=new SalaryStandardPackBo(this.getFrameconn());
				if(bo.isContainSalaryStandard(pkg_id))
					info="1";
				this.getFormHM().put("info",info);
			}
			else if("del".equals(operate))       //删除
			{
				ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
				LazyDynaBean abean=(LazyDynaBean)list.get(0);
				ContentDAO dao=new ContentDAO(this.getFrameconn()); 
				dao.delete("delete from gz_stand_pkg where pkg_id="+(String)abean.get("pkg_id"),new ArrayList());
				dao.delete("delete from gz_stand_history where pkg_id="+(String)abean.get("pkg_id"),new ArrayList());
				 
			}
			else if("resetName".equals(operate))  //重命名
			{
				String resetName=(String)this.getFormHM().get("resetName");
				ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
				LazyDynaBean abean=(LazyDynaBean)list.get(0);
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.update("update gz_stand_pkg set name='"+resetName+"' where  pkg_id="+(String)abean.get("pkg_id"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
