/*
 * Created on 2005-10-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.param;


import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author xuj
 * 
 
 */
public class SaveCurThemeTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		try{
			//系统皮肤
			String themes = (String)this.getFormHM().get("themes");
			SysParamBo.setSysParamValue("THEMES", userView.getUserName(),themes);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
}
