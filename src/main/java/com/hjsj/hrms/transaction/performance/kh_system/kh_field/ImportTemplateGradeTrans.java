package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ImportTemplateGradeTrans.java</p>
 * <p>Description:导入或引入标准标度</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2008-07-21</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class ImportTemplateGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String pointsetid = (String)this.getFormHM().get("pointsetid");
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String tabid=(String)map.get("tabid");
			String fieldnumber = (String)this.getFormHM().get("fieldnumber");
		    String fieldname=(String)this.getFormHM().get("fieldname");
		    String pointkind=(String)this.getFormHM().get("pointkind");
	        String fieldvlidflag=(String)this.getFormHM().get("fieldvlidflag");
			String description=(String)this.getFormHM().get("description");
			String visible=(String)this.getFormHM().get("visible");
		    String status=(String)map.get("status");
			String pointtype=(String)this.getFormHM().get("pointtype");
			String hiddennumber=(String)this.getFormHM().get("hiddennumber");
			String type=(String)map.get("type");
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			ArrayList newgradeList=bo.getGradeTemplateList(subsys_id);
			this.getFormHM().put("fieldname", fieldname);
			this.getFormHM().put("pointkind", pointkind==null|| "".equals(pointkind)?"0":pointkind);
			this.getFormHM().put("fieldvlidflag", fieldvlidflag==null|| "".equals(fieldvlidflag)?"1":fieldvlidflag);
			this.getFormHM().put("description", description);
			this.getFormHM().put("visible", visible==null|| "".equals(visible)?"3":visible);
			this.getFormHM().put("status", status);
			this.getFormHM().put("pointtype", pointtype);
			this.getFormHM().put("fieldnumber", fieldnumber);
			this.getFormHM().put("hiddennumber", hiddennumber);
			this.getFormHM().put("pointsetid",pointsetid);
			this.getFormHM().put("display", "0".equals(pointkind)?"display:none;":"display:block;");
			this.getFormHM().put("type",type);
			this.getFormHM().put("newgradeList",newgradeList);
			this.getFormHM().put("isClose","2");
			this.getFormHM().put("tabid",tabid);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
