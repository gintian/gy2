package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.List;
/**
 * 评审会议业务操作（删除 | 暂停 | 启动）
 * @author haosl
 *
 */
public class OptMeetingDataTrans extends IBusiness {
	
	/**
	 * opt	1:删除会议  2：暂停会议 3：启动会议  4.校验会议完整性，不通过则不能启动会议
	 */
	@Override
	public void execute() throws GeneralException {
		try {
			String opt = (String)formHM.get("opt");
			String meetingId = formHM.get("meetingid")==null?"":(String)formHM.get("meetingid");
			
			if(!StringUtils.isEmpty(meetingId))
				meetingId=PubFunc.decrypt(meetingId);
			ReviewMeetingPortalBo rmpb = new ReviewMeetingPortalBo(userView, frameconn);
			if("1".equals(opt)) {
				//删除会议
				rmpb.deleteMeeting(meetingId); 
			}else if("2".equals(opt)) {
				//暂停会议
				rmpb.stopMeeting(meetingId);
			}else if("3".equals(opt)) {
				//启动会议
				rmpb.startMeeting(meetingId);
			}else if("4".equals(opt)) {
				List<String> msgList = rmpb.isCommitteConsummate(meetingId);
				formHM.put("msgList", msgList);
			}
			
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
