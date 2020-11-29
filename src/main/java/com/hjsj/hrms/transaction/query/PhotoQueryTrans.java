package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:PhotoQueryTrans</p>
 * <p>Description:显示照片交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 16, 2005:8:51:53 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PhotoQueryTrans extends IBusiness {

    /**
     * 
     */
    public PhotoQueryTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String photo_other_view=sysbo.getValue(Sys_Oth_Parameter.PHOTO_OTHER_VIEW);
		if(photo_other_view==null||photo_other_view.length()<=0)
			photo_other_view="";
		this.getFormHM().put("photo_other_view", photo_other_view);
    }

}
