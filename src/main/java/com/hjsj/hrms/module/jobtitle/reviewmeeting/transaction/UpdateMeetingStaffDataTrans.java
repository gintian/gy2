package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

/**
 * <p>Title:UpdateMeetingStaffDataTrans </p>
 * <p>Description: 根据会议编号更新评审会议-参会人数列</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class UpdateMeetingStaffDataTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		try {
			StringBuffer sql = new StringBuffer();
			String w0301 = (String)this.getFormHM().get("w0301");//会议编号
			int typeCommittee = (Integer)this.getFormHM().get("typeCommittee");
			if(StringUtils.isNotEmpty(w0301)){
				w0301 = PubFunc.decrypt(w0301);
				ContentDAO dao = new ContentDAO(this.frameconn);
				String column = ""; 
				if(typeCommittee==1)
					column = "W0315";
				else if(typeCommittee==4)
					column = "W0323";
				sql.append("update W03 set "+column+" = (");
				sql.append(" select COUNT(*) from zc_expert_user where W0301 = '"+ w0301 +"' and type="+typeCommittee+" and W0501='xxxxxx'");
				sql.append(" ) where W0301 = '"+ w0301 +"'");
				dao.update(sql.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
