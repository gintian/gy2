package com.hjsj.hrms.transaction.pos.posreport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckParameter extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sqlstr = new StringBuffer();
	    	if(Sql_switcher.searchDbServer()== Constant.ORACEL)
	    		sqlstr.append(" select * from constant where constant = 'PS_SUPERIOR' ");
			else		
				sqlstr.append(" select * from [constant] where [constant] = 'PS_SUPERIOR' ");
	    	this.frecset = dao.search(sqlstr.toString());
	    	if(this.frecset.next())
	    	{
	    		String result = this.frecset.getString("str_value");
	    		if(!(result==null || "".equals(result) || "#".equals(result) ))
	    			this.getFormHM().put("result","yes");
	    		else
	    			this.getFormHM().put("result","no");
	    	}else
	    		this.getFormHM().put("result","no");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
