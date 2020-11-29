package com.hjsj.hrms.transaction.train;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:删除需求采集表及其明细信息</p>
 * <p>Description:对选取的的删除列表进行删除操作</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-14:14:42:22</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class DeleteInfoPickTrans extends IBusiness {

	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		  ArrayList infopicklist=(ArrayList)this.getFormHM().get("selectedlist");
	        if(infopicklist==null||infopicklist.size()==0)
	            return;
	        
	        //声明ArrayList对象
	        
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        try
			{
		        for(int i=0;i<infopicklist.size();i++)
		        {
		        	ArrayList list=new ArrayList();
		        	list.add(((DynaBean)infopicklist.get(i)).get("r1901").toString());
	          	    dao.delete("delete from R19 where R1901=?",list);
		        }
		        
		        for(int i=0;i<infopicklist.size();i++)
		        {
		        	ArrayList list=new ArrayList();
		        	list.add(((DynaBean)infopicklist.get(i)).get("r1901").toString());
	          	    dao.delete("delete from R22 where R2201=?",list);
		        }
	        }
		    catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }
		    
	}

}
