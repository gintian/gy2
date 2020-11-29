package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.HashMap;

public class SaveAttachTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String p0100=(String)this.getFormHM().get("p0100");
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)map.get("opt");
			WorkPlanViewBo bo = new WorkPlanViewBo(this.getUserView(),this.getFrameconn());
			if("1".equals(opt))
			{
				String fileName=(String)this.getFormHM().get("fileName");
				FormFile formFile=(FormFile)this.getFormHM().get("formFile");
				bo.saveAttach(formFile,Integer.parseInt(p0100), fileName);
			}
			else if("2".equals(opt)){
				String file_id=(String)map.get("file_id");
				bo.deleteAttach(Integer.parseInt(p0100),Integer.parseInt(file_id));
			}
			this.getFormHM().put("p0100",p0100);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
