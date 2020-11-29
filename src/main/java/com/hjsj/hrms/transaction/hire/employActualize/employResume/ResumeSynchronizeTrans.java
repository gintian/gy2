package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.InnerHireBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.HashSet;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:简历同步</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 20, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ResumeSynchronizeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String a0100=(String)this.getFormHM().get("a0100");
			String nbase=(String)this.getFormHM().get("nbase");
			String[] a0100s=a0100.split("#");
			HashSet set=new HashSet();
			for(int i=0;i<a0100s.length;i++)
			{
				if(a0100s[i].trim().length()>0)
					set.add(a0100s[i]);
			}
			
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=xmlBo.getAttributeValues();
			
			RecordVo vo=ConstantParamter.getConstantVo("SS_EMAIL");
			String email_field=vo.getString("str_value");
			InnerHireBo innerHireBo=new InnerHireBo(this.getFrameconn());
			innerHireBo.synchronizeResume(set,email_field,nbase);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
