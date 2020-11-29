package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:DeleteIpAddrTrans</p>
 * <p>Description:删除ip地址，ip_addr</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2005:5:43:18 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DeleteIpAddrTrans extends IBusiness {

    /**
     * 
     */
    public DeleteIpAddrTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        ArrayList list=(ArrayList)this.getFormHM().get("selectedlist");
        if(list==null)
            return;
        cat.debug("list size="+list.size());
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
            dao.deleteValueObject(list);
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(sqle);             
        }
    }

}
