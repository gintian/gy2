package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.sys.param.Sys_Infom_Parameter;
import com.hjsj.hrms.businessobject.sys.report.DyParameter;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
/**
 *<p>Title:DisplayPictureTrans</p> 
 *<p>Description:照片显示</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-8:上午09:25:26</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class DisplayPictureList extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{
			String A0100 = (String)this.getFormHM().get("a0100");
			String dbname = (String)this.getFormHM().get("dbname");
			this.getFormHM().put("a0100",A0100);
			this.getFormHM().put("dbname",dbname);
			
			 LazyDynaBean lazyDynaBean= (LazyDynaBean)DyParameter.getParameter(Sys_Oth_Parameter.PHOTO,"photo_w",this.frameconn);
			 String photo_w=(String)lazyDynaBean.get("photo_w");
			 photo_w=photo_w!=null&&photo_w.trim().length()>0?photo_w:"85";
			 
			 lazyDynaBean= (LazyDynaBean)DyParameter.getParameter(Sys_Oth_Parameter.PHOTO,"photo_h",this.frameconn);
			 String photo_h=(String)lazyDynaBean.get("photo_h");
			 photo_h=photo_h!=null&&photo_h.trim().length()>0?photo_h:"120";
			 
			 Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
			 String photo_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.PHOTO,"MaxSize");
			 photo_maxsize=photo_maxsize!=null&&photo_maxsize.length()>=0?photo_maxsize:"-1";
			 this.getFormHM().put("photo_maxsize", photo_maxsize);
			 
			 this.getFormHM().put("photo_w",photo_w);
			this.getFormHM().put("photo_h",photo_h);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
}
