package com.hjsj.hrms.module.system.portal.jobtitle.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.module.system.portal.jobtitle.businessobject.JobtitlePortalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 获取首页职称评审提示信息
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetJobtitlePortalMsg extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
			HashMap configMap = reviewFileBo.getResultsArchivingConfig(JobtitleUtil.ZC_REVIEWARCHIVE_STR);
			
			String fieldset = (String)configMap.get("fieldset");// 归档子集 
			String committee_result = (String)configMap.get("W0559");// 通过状态指标，如果为null算做不通过
			String apply_post = (String)configMap.get("W0515");// 申报职称指标
			String start_date = (String)configMap.get("W0309");// 开始日期
			String meeting_name = (String)configMap.get("W0303");// 会议名称
			
			JobtitlePortalBo jobtitlePortalBo = new JobtitlePortalBo(this.getFrameconn(), this.userView);// 工具类
			HashMap<String, String> map = new  HashMap<String, String>();
			if(!StringUtils.isEmpty(fieldset)){
				map = jobtitlePortalBo.getResultsArchivingInfo(fieldset, committee_result, apply_post, start_date, meeting_name);
			}
			
			this.getFormHM().put("infomap", map);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
