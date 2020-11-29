package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.util.ArrayList;

public class SaveInterviewerPositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		String a0100=(String)this.getFormHM().get("a0100");
		String posID=(String)this.getFormHM().get("posID");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.delete("delete from zp_pos_tache where a0100='"+a0100+"'",new ArrayList());
			dao.delete("delete from z05 where a0100='"+a0100+"'",new ArrayList());
			dao.delete("delete from zp_test_template where a0100='"+a0100+"'",new ArrayList());
			
		    String sql = "insert into zp_pos_tache (a0100,zp_pos_id,thenumber,apply_date,status) values(?,?,?,?,?)";
		    ArrayList values = new ArrayList();
		    Date date = new Date(new java.util.Date().getTime());
		    values.add(a0100);
		    values.add(posID);
		    values.add(1);
		    values.add(date);
		    values.add("0");
			/*PreparedStatement stm = this.getFrameconn().prepareStatement(sql);
			stm.setString(1, a0100);
			stm.setString(2, posID);
			stm.setInt(3, 1);
			stm.setDate(4, date);
			stm.setString(5, "0");
			stm.execute();*/
		    dao.insert(sql, values);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("保存失败！"));	
		}
		
		
		

	}

}
