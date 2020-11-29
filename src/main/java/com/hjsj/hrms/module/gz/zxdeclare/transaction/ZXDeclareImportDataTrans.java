package com.hjsj.hrms.module.gz.zxdeclare.transaction;

import com.hjsj.hrms.module.gz.zxdeclare.businessobject.IDeclareService;
import com.hjsj.hrms.module.gz.zxdeclare.businessobject.impl.DeclareServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.HashMap;
import java.util.Map;

public class ZXDeclareImportDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap return_data = new HashMap();
		String type = (String) this.formHM.get("type");
		IDeclareService iDeclareService = new DeclareServiceImpl(this.frameconn, this.userView);
		if("import".equalsIgnoreCase(type)){//获取模板存放路径
			try {
				String filePath = iDeclareService.getImportPath(this.userView);
				filePath = PubFunc.encrypt(filePath);
				return_data.put("savepath", filePath);
				this.formHM.put("return_code", "success");
				this.formHM.put("return_data", return_data);
			} catch (GeneralException e) {
				e.printStackTrace();
				this.formHM.put("return_code", "fail");
				this.formHM.put("return_msg", e.getErrorDescription());
			}
		}else if("importData".equalsIgnoreCase(type)){
			HashMap fileHM = PubFunc.DynaBean2Map((MorphDynaBean)(this.formHM.get("file")));
			String filename = (String) fileHM.get("filename");
			filename = PubFunc.decrypt(filename);
			String fileid = (String) fileHM.get("fileid");
			try {
				Map paramMap = iDeclareService.importZXDeclareData(fileid,this.userView);
				String rzeFilename = (String) paramMap.get("rzFileName");
				String errorCount = (String) paramMap.get("errorCount");
				String successCount = (String) paramMap.get("successCount");
				this.formHM.put("return_code", "success");
				this.formHM.put("errorCount", errorCount);
				this.formHM.put("successCount", successCount);
				this.formHM.put("eFileName",rzeFilename);
			} catch (GeneralException e) {
				e.printStackTrace();
				this.formHM.put("return_code", "fail");
				this.formHM.put("return_msg", e.getErrorDescription());
			}
		}
	}

}
