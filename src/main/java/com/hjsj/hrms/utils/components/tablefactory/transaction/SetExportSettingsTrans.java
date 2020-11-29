package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.google.gson.Gson;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.ExportReportBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ExportSettingsModel;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class SetExportSettingsTrans  extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String subModuleId = (String)this.getFormHM().get("subModuleId");
        String data = (String)this.getFormHM().get("data");
        int settingsId=Integer.parseInt((String) this.getFormHM().get("settingsId"));


        ExportReportBO exportReportBO=new ExportReportBO(this.getUserView(),this.getFrameconn());

        Gson gson=new Gson();

        ExportSettingsModel exportSettingsModel=gson.fromJson(data,ExportSettingsModel.class);

        try {
            if(settingsId==0){
                exportReportBO.addExportPageOptions(exportSettingsModel,subModuleId);
            }else{
                exportReportBO.updateExportPageOptions(exportSettingsModel,settingsId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
