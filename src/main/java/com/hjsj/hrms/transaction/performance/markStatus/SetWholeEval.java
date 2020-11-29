package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SetWholeEval.java</p>
 * <p>Description>:保存总体评价</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-05-31 下午03:56:27</p>
 * <p>@version: 1.0</p>
 * <p>@author: JinChunhai
 */

public class SetWholeEval extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String  planID=(String)this.getFormHM().get("planID");
			String  objectID=(String)this.getFormHM().get("objectID");
			String  mainbodyID=(String)this.getFormHM().get("mainbodyID");
			
			if(objectID!=null && objectID.trim().length()>0 && "~".equalsIgnoreCase(objectID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = objectID.substring(1); 
	        	objectID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
			if(mainbodyID!=null && mainbodyID.trim().length()>0 && "~".equalsIgnoreCase(mainbodyID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = mainbodyID.substring(1); 
	        	mainbodyID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
			
			String body_id=(String)this.getFormHM().get("body_id");
			String descctrl=(String)this.getFormHM().get("descctrl");
			if(descctrl!=null)
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.update("update per_mainbody set descctrl="+descctrl+" where  plan_id="+planID+" and object_id='"+objectID+"' and mainbody_id='"+mainbodyID+"'");
			}
			else
			{
				String  description="";
				if(this.getFormHM().get("description")!=null)
					description=SafeCode.decode(this.getFormHM().get("description").toString());
				MarkStatusBo markStatusBo=new MarkStatusBo(this.getFrameconn());
				markStatusBo.SetDesc(planID,objectID,mainbodyID,description, this.userView.getUserName(),body_id);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
