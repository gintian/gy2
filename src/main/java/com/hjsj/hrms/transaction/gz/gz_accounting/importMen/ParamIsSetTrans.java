package com.hjsj.hrms.transaction.gz.gz_accounting.importMen;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:引入单位\部门变动人员 参数是否已被设置</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 21, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ParamIsSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			GzAmountXMLBo bo=new GzAmountXMLBo(this.getFrameconn(),1);
			String salaryid=(String)this.getFormHM().get("salaryid");
			String isSalaryManager=(String)this.getFormHM().get("isSalaryManager");
			HashMap map=bo.getValuesMap();
			if(map!=null)
			{
				if(map.get("chg_set")!=null&&((String)map.get("chg_set")).length()>0)
				{
					this.getFormHM().put("isExist","1");
				}
				else
					this.getFormHM().put("isExist","0");
			}
			else
			{
				this.getFormHM().put("isExist","0");
			}
			this.getFormHM().put("isSalaryManager", isSalaryManager);
			this.getFormHM().put("salaryid",salaryid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
