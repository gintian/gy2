package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.RecordConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存列表显示方式
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 4, 2007:11:48:24 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveRecordConstantTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		RecordConstant recordConstant=new RecordConstant(this.getFrameconn());
		String str_value=(String)this.getFormHM().get("recardconstant");
        if(str_value==null||str_value.length()<=0)
        	str_value="0";
		recordConstant.save("up",str_value);
	}
	

}
