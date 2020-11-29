package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class HrSyncExportTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		// 人员
		String dbnamestr = hsb.getTextValue(hsb.BASE);
		dbnamestr = hsb.getDBMess(dbnamestr);
		// 基本指标
		String fieldstr=hsb.getTextValue(hsb.FIELDS);	
		ArrayList list=hsb.getFields(fieldstr);
		fieldstr=hsb.getMess(list);		
		// 汉字转换
		String code = hsb.getAttributeValue(hsb.CODE);	
		this.getFormHM().put("code",code);
		
		String outname = hsb.exportExcel(code);
		this.getFormHM().put("outName",PubFunc.encrypt(outname));
	}

}
