package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CourseApproveTrans extends IBusiness{

	public void execute() throws GeneralException {
		String sel = this.getFormHM().get("sel").toString();
		String [] sels = sel.split(",");
		String state = this.getFormHM().get("s").toString();
		String codeitem = (String)this.getFormHM().get("codeitem");
		String [] codeitems=null;
		if(codeitem!=null&&codeitem.length()>0)
			codeitems = codeitem.split(",");
		
		if("1".equals(state)){ //如果是批准			
			this.approve(codeitems);
		}else if("2".equals(state)){  //如果是驳回
			this.reject(sels);
		}
	}
	
	public void approve(String [] sels){ //批准
		ContentDAO cd = new ContentDAO(this.getFrameconn());
		for(int i = 0 ; i < sels.length ; i++){
			String[] items = sels[i].split(":");
			String itemid = items[0];
			String codeitemid = items[3];
			codeitemid = PubFunc.decrypt(SafeCode.decode(codeitemid));
			String sql = " update r50 set r5022 = '03',codeitemid='"+codeitemid+"' where r5000 = '"+itemid+"'";
			try {
				cd.update(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void reject(String [] sels){ //驳回
		ContentDAO cd = new ContentDAO(this.getFrameconn());
		for(int i = 0 ; i < sels.length ; i++){
			String sql = " update r50 set r5022 = '07' where r5000 = '"+PubFunc.decrypt(SafeCode.decode(sels[i]))+"'";
			try {
				cd.update(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
