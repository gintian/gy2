package com.hjsj.hrms.transaction.gz.gz_accounting.voucher;

import com.hjsj.hrms.businessobject.gz.GzVoucherBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExportVoucherTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
//			 hashvo.setValue("type",type);	
//			  hashvo.setValue("time",timeInfo);
//			  hashvo.setValue("pn_id",document.getElementsByName("voucher_id")[0].value);
//			  hashvo.setValue("status",document.getElementsByName("status")[0].value);
//			  hashvo.setValue("a_code","${voucherForm._code}");
			String type=(String)this.getFormHM().get("type");
			String time=(String)this.getFormHM().get("time");
			String pn_id=(String)this.getFormHM().get("pn_id");
			String status = (String)this.getFormHM().get("status");
			String a_code=(String)this.getFormHM().get("a_code");
			String dbilltimes=(String)this.getFormHM().get("dbilltimes");
			String fileType=(String)this.getFormHM().get("fileType");
			GzVoucherBo bo = new GzVoucherBo(this.getFrameconn(),this.userView);
			
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			if(!safeBo.isVoucherPriv(pn_id))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.noVoucherAuthority")+"!"));
			
			String fileName=bo.exportFile(type, time, pn_id, status, a_code,fileType,dbilltimes);
			/* 安全问题 文件下载 财务凭证 导出 xiaoyun 2014-9-13 start */
			fileName = SafeCode.encode(PubFunc.encrypt(fileName));
			/* 安全问题 文件下载 财务凭证 导出 xiaoyun 2014-9-13 end */
			this.getFormHM().put("fileName",fileName);
			fileType = fileType.split(",")[0];
			this.getFormHM().put("fileType", fileType);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
