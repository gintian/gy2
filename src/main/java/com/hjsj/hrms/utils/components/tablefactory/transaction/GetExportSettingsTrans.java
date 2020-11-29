package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.components.tablefactory.businessobject.ExportReportBO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class GetExportSettingsTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {


        String subModuleId = (String)this.getFormHM().get("subModuleId");

        ExportReportBO exportReportBO=new ExportReportBO(this.getUserView(),this.getFrameconn());

        try {

            String[] data=exportReportBO.getExportPageOptions(subModuleId);


            this.getFormHM().put("settingsId",data[0]);
            this.getFormHM().put("settings",data[1]);


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
