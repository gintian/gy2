package com.hjsj.hrms.transaction.train;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-15:19:31:44</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class DeleteInfoPickDetailTrans extends IBusiness {

	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		 ArrayList detaillist=(ArrayList)this.getFormHM().get("slt");
	        if(detaillist==null||detaillist.size()==0)
	            return;
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        try
	        {
	        	for(int i=0;i<detaillist.size();i++)
		        {
		        	ArrayList list=new ArrayList();
		        	list.add(((DynaBean)detaillist.get(i)).get("r2202").toString());
 	        	    dao.delete("delete from R22 where R2202=?",list);
		        }
	        	// this.getFormHM().put("judge","3");		//添加标志
	        	((RecordVo)this.getFormHM().get("infoPickDetailTb")).clearValues();
	        	this.getFormHM().put("first_date",new DateStyle());
	           // dao.deleteValueObject(detaillist);
	        }
		    catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }

	}

}
