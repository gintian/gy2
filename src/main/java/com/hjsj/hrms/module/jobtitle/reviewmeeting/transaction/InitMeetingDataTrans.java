package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingPortalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 查询评审会议列表
 * 
 * @author haosl
 */
public class InitMeetingDataTrans extends IBusiness {

	/**
	 *1、scheme（String[]） 查询方案参数：
	 * 		1) 为空数组时初始化操作：
	 * 			默认加载当前年度的本单位的评审会议，下属单位的评审会议不可见。
	 * 		2)不为空 方案查询：
	 * 			例 :scheme=[‘in’,’2018’,’UM`0002’] 
	 *				Scheme[0]：01|05|09|06 //会议状态01=起草;05=执行中 06=结束 09=暂停
	 *				Scheme[1]：年度（2018）
	 *				Scheme[2]：为空本单位 不为空查看下属单位（具体机构号：UN|UM`0002）
	 */
	@Override
	public void execute() throws GeneralException {
		
		try {
			HashMap formMap = this.getFormHM();
			
			String[] scheme = formMap.get("scheme")==null?new String[3]:((String)formMap.get("scheme")).split(",");
			
			int pageNum = Integer.parseInt((String)this.formHM.get("page"));
			int pageSize = Integer.parseInt((String)this.formHM.get("limit"));
			ReviewMeetingPortalBo rmpbo = new ReviewMeetingPortalBo(userView, frameconn);
			ArrayList<LazyDynaBean> meetings = rmpbo.schemeMeettingsBySchmeme(scheme,pageSize,pageNum);
			int count = rmpbo.getCountNum(scheme);
			
			formMap.put("meetings", meetings);
			formMap.put("totalCount", count);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
