/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.ajax.TransVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:FlushTemplateDataTrans</p>
 * <p>Description:刷新模板数据</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 18, 200610:34:05 AM
 * @author chenmengqing
 * @version 4.0
 */
public class FlushTemplateDataTrans extends IBusiness {


	public void execute() throws GeneralException {
		TransVo transvo=(TransVo)this.getFormHM().get("transvo");
		if(transvo==null)
			throw new GeneralException(ResourceFactory.getProperty(""));
		try
		{
			String setname=transvo.getDatasetid();
			transvo.refreshPageCount(this.getFrameconn());
			int idx=setname.lastIndexOf("_");
			String tabid=setname.substring(idx+1);
			idx=setname.indexOf("templet_");
			if(idx==0)
			{
				
			}
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			tablebo.flushData(transvo);
			this.getFormHM().clear();
			this.getFormHM().put("transvo",transvo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
