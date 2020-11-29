package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class ReportStructAnalyseTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			String username = (String) this.getFormHM().get("username1");
			if(username==null|| "".equals(username)){
				username = this.userView.getUserName();
				if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			}
			TnameBo tnameBo=new TnameBo(this.getFrameconn(),tabid,this.userView.getUserId(),username,"");
			tnameBo.anaylseReportStruct(tabid);
			
			ArrayList all = tnameBo.getAllParam();
			ArrayList param0 = (ArrayList) all.get(0);
			ArrayList param1 = (ArrayList) all.get(1);
			DbWizard dbw=new DbWizard(this.frameconn);
			ContentDAO dao=new ContentDAO(this.frameconn);
			//清理没用的全局参数			
				for(int i=0;i<param0.size();i++)
				{
					Table table=new Table("tt_p");
					if (dbw.isExistField("tt_p",param0.get(i).toString().toLowerCase(), false)) {
						Field field=new Field(param0.get(i).toString().toLowerCase());
						table.addField(field);
						dbw.dropColumns(table);
					}
				}
				


			//清理表类参数
			if (param1.size() > 0) {
				RowSet rs = dao.search("select TSortId from tsort");
				while (rs.next()) {
					DbWizard dbw1=new DbWizard(this.frameconn);
					for (int i = 0; i < param1.size(); i++) {
						Table table1 = new Table("tt_s" + rs.getString("TSortId"));
						if (dbw1.isExistField("tt_s" + rs.getString("TSortId"),param1.get(i).toString().toLowerCase(), false)) {
							Field field = null;
							field = new Field(param1.get(i).toString().toLowerCase());
							table1.addField(field);
							dbw1.dropColumns(table1);
						}
					}					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
