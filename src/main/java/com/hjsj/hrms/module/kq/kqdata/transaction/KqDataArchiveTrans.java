package com.hjsj.hrms.module.kq.kqdata.transaction;


import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataArchiveService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataArchiveServiceImpl;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class KqDataArchiveTrans extends IBusiness{
	
	private enum TransType{
		/**获取考勤归档方案*/
		initItemMapping,
		/**保存考勤归档方案*/
		saveItemMapping
	}
	
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String return_code = "success";
		HashMap return_data = new HashMap();
		String type = (String) this.formHM.get("type");
		KqDataArchiveService kqDataArchiveService = new KqDataArchiveServiceImpl(this.userView, this.frameconn);
		if(type.equalsIgnoreCase(TransType.initItemMapping.toString())){
			return_data = kqDataArchiveService.getKqDataArchive();
			this.formHM.put("return_code", return_code);
			this.formHM.put("return_data",return_data);
		}else if(type.equalsIgnoreCase(TransType.saveItemMapping.toString())){
			String fieldsetid = (String) this.formHM.get("fieldsetid");
			ArrayList mappingList = (ArrayList) this.formHM.get("mapping_list");
			boolean flag = kqDataArchiveService.saveKqDataArchive(fieldsetid, mappingList);
			if(flag)
				this.formHM.put("return_code", return_code);
			else{
				this.formHM.put("return_code","fail");
				this.formHM.put("return_msg", ResourceFactory.getProperty("kq.archive.scheme.savearichveschemeerror"));
			}
		}
	}
	
}
