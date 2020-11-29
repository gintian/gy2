package com.hjsj.hrms.module.template.templatetoolbar.apply.transaction;

import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;

public class CheckSplitByOthersTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
	        String taskId = frontProperty.getTaskId();            
	        String allNum = (String)this.getFormHM().get("allNum");
	        String checkSpllitInfo = "";
	        //检查单据是否被他人拆解
	        if (StringUtils.isNotBlank(taskId)&&!"0".equals(taskId)&&StringUtils.isNotBlank(allNum)){
	        	checkSpllitInfo = this.isSplitByOthers(taskId,allNum);
	        }  
	        if(StringUtils.isNotBlank(checkSpllitInfo)) { 
	        	throw GeneralExceptionHandler.Handle(new Exception(checkSpllitInfo));   
	        }
		}catch (Exception e) {
			e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
     * 检查单据是否被他人拆解
     * @param taskId
     * @param selectNum 
     * @return
     */
	private String isSplitByOthers(String taskId, String allNum) {
		String info = "";
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());   
			String[] taskarr = taskId.split(",");
			int num = 0;
			for(int i=0;i<taskarr.length;i++) {
				String task_id = taskarr[i];
				String sql = "select count(seqnum) from t_wf_task_objlink where task_id="+task_id;
				rset = dao.search(sql);
				if(rset.next()) {
					num+=rset.getInt(1);
				}
			}
			if(num!=0&&num<Integer.parseInt(allNum)) {
	        	info="单据已被其他审批人拆分审批，请重新进入单据审批。";
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return info;
	}
}
