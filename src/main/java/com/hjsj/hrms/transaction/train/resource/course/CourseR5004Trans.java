package com.hjsj.hrms.transaction.train.resource.course;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author liweichao
 *
 */
public class CourseR5004Trans extends IBusiness {

	public void execute() throws GeneralException {
		String itemid=(String)this.getFormHM().get("codeitemid");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RecordVo recordVo = new RecordVo("codeitem");
		if(itemid!=null&&itemid.length()>0&&!"root".equalsIgnoreCase(itemid)){
			try {
				recordVo.setString("codeitemid", itemid);
				recordVo.setString("codesetid", "55");
				if(!dao.isExistRecordVo(recordVo))
					this.getFormHM().put("codeitemdesc", "");
				//recordVo=dao.findByPrimaryKey(recordVo);
			} catch (Exception e) {
				this.getFormHM().put("codeitemdesc", "");
			} 
			this.getFormHM().put("codeitemdesc",recordVo.getString("codeitemdesc"));
		}else
			this.getFormHM().put("codeitemdesc","");
	}
}
