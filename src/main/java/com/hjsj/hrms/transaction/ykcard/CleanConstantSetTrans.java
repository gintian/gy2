package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 清除设置
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 24, 2007:3:03:07 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class CleanConstantSetTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		String orgid=this.userView.getUserOrgId();
     	if(orgid==null||orgid.length()<=0)
		{
			if(!this.userView.isSuper_admin())
			{
				this.getFormHM().put("flagmess","没有单位关联不能清空！");
				return;
			}
		}
		Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
		XmlParameter xml=new XmlParameter(this.getFrameconn(),userView.getUserOrgId(),"");
		boolean isCorrect=xml.cleanConstantSet(userView);
		xml.saveParameter();
		if(isCorrect)
		  this.getFormHM().put("flagmess","操作成功");
		else
		  this.getFormHM().put("flagmess","操作失败");
	}

}
