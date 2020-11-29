/*
 * Created on 2005-10-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchDeptPosListTrans extends IBusiness {

	public static int searchDbServer()
    {
		try{
           String dbname = SystemConfig.getProperty("dbserver");
           if(dbname == null || "".equals(dbname))
              return 1;
           if("mssql".equals(dbname))
              return 1;
           if("db2".equals(dbname))
              return 3;
           if("oracle".equals(dbname))
              return 2;
           return !"sybase".equals(dbname) ? 1 : 4;
		}
        catch(Exception ee){
        	ee.printStackTrace();	
        }
        return 1;
        
    }
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		 String deptpossql = "";
		 String deptName = "";
		 int  db = 1;
		 db = searchDbServer();
		 switch(db)
	     {
	        case 1: // 'mssql'
	        	deptpossql = "select codeitemid, codeitemdesc  from organization where codeitemid in (select pos_id from zp_position where dept_id like '" + this.userView.getManagePrivCodeValue() + "%')";
	            break;

	        case 2: // '\002'
	        case 3: // '\003'
	        	deptpossql = "select codeitemid, codeitemdesc  from organization where codeitemid in (select pos_id from zp_position  where dept_id like '" + this.userView.getManagePrivCodeValue() + "%')";
	            break;
	     }

		 this.getFormHM().put("deptpossql",deptpossql);
	}

}
