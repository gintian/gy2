package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

public class ReleaseAndStopTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			String flag=(String)this.getFormHM().get("flag"); 
			String sp = "";
			String budget_id=(String)this.getFormHM().get("budget_id");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sqll = "select SPFlag from gz_budget_index where budget_id = "+budget_id+"";
			RowSet rs = dao.search(sqll.toString());
			while(rs.next()){
				sp=rs.getString("SPFlag");
			}
			String sql="update gz_budget_index set SPFlag="; 
					if("5".equals(flag))
							sql+="'04'"; 
					else if("6".equals(flag)&& "04".equals(sp))
							sql+="'09'"; 
					else 
						throw GeneralExceptionHandler.Handle(new Exception("只能对发布的预算进行暂停"));
					sql+=" where budget_id="+budget_id+"";	
							dao.update(sql);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
