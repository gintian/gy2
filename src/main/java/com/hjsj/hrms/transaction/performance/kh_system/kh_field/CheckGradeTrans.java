package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:CheckGradeTrans.java</p>
 * <p>Description:判断标准标度代码是否重复</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2008-07-21</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class CheckGradeTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			String msg = "1";
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String grade_id=(String)this.getFormHM().get("grade_id");
			String type=(String)this.getFormHM().get("type");
			String hiddenid=(String)this.getFormHM().get("hiddenid");
			String saveandcontinue=(String)this.getFormHM().get("saveandcontinue");
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			if(!("2".equals(type)&&grade_id.equalsIgnoreCase(hiddenid)))//修改的时候不区分大小写  2013.11.26 pjf
			{
    			if(bo.isHaveThisRecord(grade_id,subsys_id))
	    		{
	    			msg = ResourceFactory.getProperty("kh.field.recode");
	    		}
			}
			this.getFormHM().put("msg",msg);
			this.getFormHM().put("saveandcontinue",saveandcontinue);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
