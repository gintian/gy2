package com.hjsj.hrms.transaction.gz.gz_accounting.voucher;

import com.hjsj.hrms.businessobject.gz.GzVoucherBo;
import com.hjsj.hrms.businessobject.gz.GzVoucherSendBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SendVoucherMessageTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String time=(String)this.getFormHM().get("time");
			String type=(String)this.getFormHM().get("type");
			String pn_id=(String)this.getFormHM().get("pn_id");
			GzVoucherBo bo = new GzVoucherBo(this.getFrameconn(),this.getUserView());
			String status=(String)this.getFormHM().get("status");
			String _code=(String)this.getFormHM().get("a_code");
			
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			if(!safeBo.isVoucherPriv(pn_id))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.noVoucherAuthority")+"!"));
			
			GzVoucherSendBo sendBo = new GzVoucherSendBo(this.getFrameconn(),this.getUserView());
			String[] options = sendBo.getOptions();
			if("2".equals(options[0])){
				String flag=bo.sendMessage(time, pn_id, status, _code);
				this.getFormHM().put("flag",flag);
			}else{
				String flag = sendBo.sendMessages(pn_id, time);
				this.getFormHM().put("flag",flag);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
