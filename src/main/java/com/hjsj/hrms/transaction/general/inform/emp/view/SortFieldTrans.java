package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.general.info.EmpMaintenanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:SortFieldList</p> 
 *<p>Description:指标排序</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SortFieldTrans extends IBusiness {

	public  void execute()throws GeneralException
	{
		try
		{
			/**指标排序*/
			String sortfieldstr=(String)this.getFormHM().get("sortfieldstr");
			String setname=(String)this.getFormHM().get("setname");
			EmpMaintenanBo embo = new EmpMaintenanBo(this.getFrameconn());
			
			if(!(sortfieldstr==null || "".equals(sortfieldstr)))
			{
				String[] sortfield = embo.getStringArr(sortfieldstr);
				embo.sortfield(sortfield,setname);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
