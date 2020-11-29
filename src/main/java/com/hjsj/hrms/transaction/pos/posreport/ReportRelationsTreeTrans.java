package com.hjsj.hrms.transaction.pos.posreport;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * 
 *<p>Title:ReportRelationsTreeTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 20, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class ReportRelationsTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 

	    /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        String dbname="";
        cond.append("select pre,dbname from dbname where pre in (");
        for(int i=0;i<dblist.size();i++)
        {
            if(i!=0)
                cond.append(",");
            else
            	dbname=(String)dblist.get(i);
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        /**应用库前缀过滤条件*/
        this.getFormHM().put("dbcond",cond.toString());
	    this.getFormHM().put("report_relations","yes");
	    String constant = "";
	    StringBuffer sqlstr = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			if(Sql_switcher.searchDbServer()== Constant.ORACEL)
			{
	    		sqlstr.append(" select * from constant where constant = 'PS_SUPERIOR' ");
			}
			else
			{			
				sqlstr.append(" select * from [constant] where [constant] = 'PS_SUPERIOR' ");
			}
	    	ResultSet rset = dao.search(sqlstr.toString());
	    	while(rset.next())
	        {
	        	if(Sql_switcher.searchDbServer()== Constant.ORACEL)
				{
	        		constant = Sql_switcher.readMemo(rset,"str_value");
				}
				else
				{			
					constant = rset.getString("str_value");
				}
	    	
	        }         
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("constant",constant);
		
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
	    String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
		this.getFormHM().put("seprartor", seprartor);
	}

}
