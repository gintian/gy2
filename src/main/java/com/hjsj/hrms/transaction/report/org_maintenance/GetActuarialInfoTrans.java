package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;

public class GetActuarialInfoTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String isActuarialData="0";
			
			DbWizard dbWizard=new DbWizard(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(dbWizard.isExistTable("tt_cycle",false))
			{
				
				Calendar d=Calendar.getInstance();
				int yy=d.get(Calendar.YEAR);
				String ss = "select count(*) from tt_cycle where "+Sql_switcher.year("bos_date")+"<="+(yy+5)+" and "+Sql_switcher.year("bos_date")+">="+(yy-5);
				this.frowset=dao.search("select count(*) from tt_cycle where "+Sql_switcher.year("bos_date")+"<="+(yy+5)+" and "+Sql_switcher.year("bos_date")+">="+(yy-5));
				if(this.frowset.next())
				{
					if(this.frowset.getInt(1)>0)
						isActuarialData="1";
				}
			}
			this.getFormHM().put("isActuarialData", isActuarialData);
			//划转时不能选择有历史记录的基层单位
			StringBuffer str = new StringBuffer();
			if(dbWizard.isExistTable("u01",false))
			{
				
			this.frowset=dao.search("select * from u01 where unitcode not in  (select parentid from tt_organization  )");
			
			str.append(",");
			while(this.frowset.next())
			{
				str.append(this.frowset.getString("unitcode"));
				str.append(",");
			}
			}
			this.getFormHM().put("historyu01",str.toString() );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
