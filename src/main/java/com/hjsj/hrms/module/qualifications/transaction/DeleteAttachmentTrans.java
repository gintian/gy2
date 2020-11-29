package com.hjsj.hrms.module.qualifications.transaction;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.axis.utils.StringUtils;

public class DeleteAttachmentTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		boolean flag = false;
		String fileid = "";
		//20/3/6 xus vfs改造
		if(!StringUtils.isEmpty((String)this.getFormHM().get("fileid"))) {
			fileid = (String)this.getFormHM().get("fileid");
			try {
				flag = VfsService.deleteFile(this.userView.getUserName(), fileid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.getFormHM().put("isdelete", flag);

	}

}
