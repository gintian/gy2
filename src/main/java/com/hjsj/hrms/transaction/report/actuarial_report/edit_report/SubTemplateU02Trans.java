package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SubTemplateU02Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String Report_id=(String)this.getFormHM().get("report_id");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String id=(String)this.getFormHM().get("id");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String flag="true";
			try
			{
				 
				dao.update("delete from tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+id+" and report_id='"+Report_id+"'");
				String sql="insert into  tt_calculation_ctrl (report_id,id,flag,unitcode) values ('"+Report_id+"',"+id+",1,'"+unitcode+"')";
				dao.update(sql);
				String[] temps=Report_id.split("_");
				sql="update u02 set editflag=0 where unitcode='"+unitcode+"' and id="+id+" and escope='"+temps[1]+"'";
				dao.update(sql);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			this.getFormHM().put("flag","1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
