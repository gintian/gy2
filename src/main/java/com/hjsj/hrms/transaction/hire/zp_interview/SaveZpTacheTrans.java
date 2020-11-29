/*
 * Created on 2005-9-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_interview;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveZpTacheTrans</p>
 * <p>Description:保存面试环节</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 16, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveZpTacheTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ArrayList zpPosTachelist=(ArrayList)this.getFormHM().get("selectedlist");
		if(zpPosTachelist==null||zpPosTachelist.size()==0)
            return;
		RecordVo vo=(RecordVo)this.getFormHM().get("zpPosTachevo");
        if(vo==null)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
	    try
	    { 
	       for(int i=0;i<zpPosTachelist.size();i++)
           {
	       	  DynaBean rv=(LazyDynaBean)zpPosTachelist.get(i);
	          String sql="update zp_pos_tache set tache_id ="+vo.getString("tache_id")+" where a0100 ='"+rv.get("a0100")+"' and zp_pos_id='" + rv.get("zp_pos_id") + "'";
	          dao.update(sql,list);
           }
	    }
	    catch(SQLException sqle)
	    {
	       	 sqle.printStackTrace();
	    	 throw GeneralExceptionHandler.Handle(sqle);            
        }
	}

}
