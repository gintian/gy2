package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.sys.param.Sys_Infom_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 上传照片大小
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class GetPhotoParamTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		 Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
		 String photo_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.PHOTO,"MaxSize");
		 photo_maxsize=photo_maxsize!=null&&photo_maxsize.length()>=0?photo_maxsize:"-1";		
		 this.getFormHM().put("photo_maxsize", photo_maxsize);
	}

}
