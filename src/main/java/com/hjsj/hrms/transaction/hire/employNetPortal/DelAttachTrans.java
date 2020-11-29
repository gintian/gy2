package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class DelAttachTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String a0100 = this.userView.getA0100();
			String nbase=(String)map.get("nbase");
			String id = (String) map.get("id");
			String filename = (String) map.get("filename");
			a0100=PubFunc.getReplaceStr(a0100);
			nbase=PubFunc.getReplaceStr(nbase);
			id = PubFunc.decrypt(id);
			filename = PubFunc.hireKeyWord_filter_reback(filename);
			EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn(),"1");
			//根据文件名删除简历附件  jingq upd 2015.08.05
			ResumeFileBo resumeFileBo = new ResumeFileBo(this.getFrameconn(), this.userView);
			String path = resumeFileBo.getPath(nbase, a0100)+filename;
			resumeFileBo.deleteFile(id, path, "");
			String isOnlyChecked="0";
			String onlyField="";
			if(bo.isOnlyChecked())
			{
				isOnlyChecked="1";
				onlyField=EmployNetPortalBo.isOnlyChecked;
			}
			this.getFormHM().put("onlyField",onlyField);
			this.getFormHM().put("isOnlyCheck", isOnlyChecked);
			String writeable=(String)this.getFormHM().get("writeable");
			this.getFormHM().put("writeable", writeable);
			this.getFormHM().put("a0100", a0100);
			this.getFormHM().put("dbName",nbase);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
