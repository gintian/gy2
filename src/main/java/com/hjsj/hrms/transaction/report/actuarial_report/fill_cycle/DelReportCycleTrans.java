package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class DelReportCycleTrans extends IBusiness {

	
	public void execute() throws GeneralException {
	
		 ArrayList reportlist=(ArrayList)this.getFormHM().get("selectedreportlist");
	        if(reportlist==null||reportlist.size()==0)
	            return;
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        try
	        {
	            dao.deleteValueObject(reportlist);
	            StringBuffer buffer = new StringBuffer();
	            for(int i=0;i<reportlist.size();i++){
	            	RecordVo vo = (RecordVo)reportlist.get(i);
		            buffer.setLength(0);
		            buffer.append(" delete from U01 where id="+vo.getInt("id"));
		            dao.update(buffer.toString());
		            buffer.setLength(0);
		            buffer.append(" delete from U02 where id="+vo.getInt("id"));
		            dao.update(buffer.toString());
		            buffer.setLength(0);
		            buffer.append(" delete from U03 where id="+vo.getInt("id"));
		            dao.update(buffer.toString());
		            buffer.setLength(0);
		            buffer.append(" delete from U04 where id="+vo.getInt("id"));
		            dao.update(buffer.toString());
		            buffer.setLength(0);
		            buffer.append(" delete from U05 where id="+vo.getInt("id"));
		            dao.update(buffer.toString());
		            buffer.setLength(0);
		            buffer.append(" delete from tt_calculation_ctrl where id="+vo.getInt("id"));
		            dao.update(buffer.toString());
	            }
	            
	        }
		    catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }

	}
	




}
