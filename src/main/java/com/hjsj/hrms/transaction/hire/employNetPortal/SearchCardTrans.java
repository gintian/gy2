package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 招聘前台查看成绩等登记表
 * Title:SeachCardTrans
 * Description:招聘前台登记表
 * Company:hjsj
 * Create time:Oct 31, 2014:4:49:14 PM
 * @author zhaogd
 * @version 6.x
 * 
 * @modify zhaoxj 2015-11-12 从朝阳卫生局包中移植过来
 */
public class SearchCardTrans extends IBusiness {

	public void execute() throws GeneralException {
	    try {
    	    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    	    
    		String a0100 = userView.getUserId();
    		
    		EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn());
    		String nbase = bo.getZpkdbName();
    		
    		String tabid = bo.getScoreTabId();    		
    		if(tabid==null || tabid.length()==0){
    			throw new GeneralException("配置文件中没有配置登记表号！");
    		}
    		
    		if (!bo.canQueryScore(nbase, a0100)) {
    		    throw new GeneralException("成绩还未到发布日期，请耐心等待！");
    		}
    		
    		hm.remove("a0100");
    		hm.remove("dbname");
    		this.getFormHM().put("tabid", tabid);
    	    this.getFormHM().put("userbase", nbase);
    	    this.getFormHM().put("a0100", a0100);
	    } catch (Exception e) {
	        throw GeneralExceptionHandler.Handle(e);
        }
	}
}
