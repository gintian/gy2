package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * 职称评审_上会材料_获取数据源
 * @createtime August 24, 2017 9:07:55 PM
 * @author chent
 */
@SuppressWarnings("serial")
public class GetDataTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			
			ReviewFileBo bo = null;// 工具类
			String type = (String) this.getFormHM().get("type");
			ArrayList<HashMap> data = new ArrayList<HashMap>();//数据源
			
			if("1".equals(type)) {// 获取会议数据源
				bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				ArrayList schemeType = (ArrayList)this.getFormHM().get("schemeType");
				data = bo.getMeetingdata(schemeType);
				this.getFormHM().put("meetingdata", data);
				
			} else if("2".equals(type)) {// 给“taskid+时间戳”加密
				String taskid = PubFunc.decrypt((String) this.getFormHM().get("taskid"));
				this.getFormHM().put("taskid_validate", ReviewFileBo.createTaskidValidCode(taskid));
				
			} else if("3".equals(type)) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
