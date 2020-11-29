/*
 * Created on 2005-9-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_employ;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:DeleteHireEmployeeTrans</p>
 * <p>Description:删除员工录用</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class MoveToHireTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ArrayList zpPosTachelist=(ArrayList)this.getFormHM().get("selectedlist");
		if(zpPosTachelist==null||zpPosTachelist.size()==0)
            return;		
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
	    try
	    { 
	       for(int i=0;i<zpPosTachelist.size();i++)
           {
	       	  DynaBean rv=(LazyDynaBean)zpPosTachelist.get(i);
	          String sql="update zp_pos_tache set tache_id =4 where a0100 ='"+rv.get("a0100")+"'";
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
