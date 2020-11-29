/*
 * Created on 2005-7-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.sys;

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
 * <p>Title:DeleteLogTrans</p>
 * <p>Description:删除日志，fr_txlog</p>
 * <p>Company:hjsj</p>
 * <p>create time:July 25, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class DeleteLogTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
     		ArrayList queryLoglist=(ArrayList)this.getFormHM().get("selectedlist");
	        if(queryLoglist==null||queryLoglist.size()==0)
	            return;
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        try
	        {
	        	ArrayList list=new ArrayList();
	        	for(int i=0;i<queryLoglist.size();i++)
	        	{
	        		DynaBean dbean=(LazyDynaBean)queryLoglist.get(i);
	        		RecordVo vo=new RecordVo("fr_txlog");
	        		vo.setString("sequenceno",(String)dbean.get("sequenceno"));
	        		list.add(vo);
	        	}
	            dao.deleteValueObject(list);
	        }
		    catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }


	}

}
