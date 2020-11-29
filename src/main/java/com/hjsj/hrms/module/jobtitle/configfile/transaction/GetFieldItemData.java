package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * 职称评审_配置_评审结果归档方案
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 */
@SuppressWarnings("serial")
public class GetFieldItemData extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			
			ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
			String type = (String) this.getFormHM().get("type");
			ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();//数据源
			
			if("1".equals(type)) {// 获取归档子集数据源
				data = reviewFileBo.getArchivedata();
				this.getFormHM().put("archivedata", data);
				
			} else if("2".equals(type)) {//获取目的指标数据源
				String fieldsetid = "";//haosl 20160919 修改 归档配置，点击配置按钮报错
				Object object = this.getFormHM().get("fieldsetid");
				if(object!=null){
					fieldsetid = object.toString();
				}
				if(!StringUtils.isEmpty(fieldsetid)){
					data = reviewFileBo.getObjectivedata(fieldsetid);
				}
				this.getFormHM().put("objectivedata", data);
			} else if("3".equals(type)) {//获取配置信息
				HashMap configMap = new HashMap();
				configMap = reviewFileBo.getResultsArchivingConfig(JobtitleUtil.ZC_REVIEWARCHIVE_STR);
				this.getFormHM().put("configmap", configMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
