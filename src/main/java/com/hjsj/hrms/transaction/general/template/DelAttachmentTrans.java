package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;


public class DelAttachmentTrans extends IBusiness {



	public void execute() throws GeneralException {
		try {
			String username=this.userView.getUserName();
			String file_id =(String)this.getFormHM().get("file_id");
			/**基于安全平台改造,将加密的文件Id解密回来**/
			if(file_id!=null&&file_id.trim().length()>0){
				file_id = PubFunc.decrypt(SafeCode.decode(file_id));
			}
			if(file_id==null||file_id.trim().length()==0){
				this.getFormHM().put("ok", "0");
				return;
			}
				
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sb = new StringBuffer("");
			sb.append("delete from t_wf_file where create_user='"+username+"' and file_id="+file_id);
			dao.update(sb.toString());
			this.getFormHM().put("ok", "1");
		}catch (Exception e) {
			this.getFormHM().put("ok", "0");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}  
}
