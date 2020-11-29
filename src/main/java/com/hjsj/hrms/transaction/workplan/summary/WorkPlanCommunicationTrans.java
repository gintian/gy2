package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanCommunicationBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 查询 沟通信息 
 * 
 * 简要说明类的作用
 * 其它补充说明
 * <p>Title: WorkPlanCommunicationTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-7-31 下午04:39:08</p>
 * @author guoby
 * @version 1.0
 */
public class WorkPlanCommunicationTrans extends IBusiness{

	public void execute() throws GeneralException {
		WorkPlanCommunicationBo communicationBo = new WorkPlanCommunicationBo(this.frameconn,this.userView);
		
		try {
			String type = (String) this.getFormHM().get("type");
			String objectId = WorkPlanUtil.decryption((String) this.getFormHM().get("objectId"));
			String messageId = WorkPlanUtil.decryption((String) this.getFormHM().get("msgId"));
			
			// 删除
			if(!(messageId == null || "".equals(messageId))){
				
				boolean  deleteResult = false;
				deleteResult = communicationBo.delMessage(messageId);
				this.formHM.put("deleteResult", deleteResult+"");
				
			}else{// 查询
				
				ArrayList resultList = communicationBo.queryAllMessage(type, objectId);
				
				this.formHM.put("list", resultList);
				this.formHM.put("bhr", String.valueOf(this.userView.getA0100().length()<1));
				if(resultList.size()==0)
					return;
				
				ArrayList rsUpLoadFileList = new ArrayList();
				for (int i = 0; i < resultList.size(); i++) {// 490 50041
					ArrayList list = (ArrayList) resultList.get(i);
					String msgId = (String) list.get(3);
					msgId = WorkPlanUtil.decryption(msgId);
					
					ArrayList arrList = communicationBo.queryAllUpLoadFile(msgId);
					if (arrList.size() == 0)
						continue;


					rsUpLoadFileList.add(arrList);
				}
				this.formHM.put("fileList", rsUpLoadFileList);
			}
			
		} catch (Exception e) {
			 e.printStackTrace();
	         throw GeneralExceptionHandler.Handle(e);
		}
	}

}
