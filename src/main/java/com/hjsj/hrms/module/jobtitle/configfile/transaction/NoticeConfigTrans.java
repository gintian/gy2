package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 职称-配置-公示材料配置
 * @createtime Mar 24, 2017 9:07:55 PM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class NoticeConfigTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		String type = (String) this.getFormHM().get("type");// 1：获取模板信息  2：获取模板的页签信息 3:获取配置信息 4：保存配置信息
		
		JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(), this.getUserView());
		ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
		try {
			if("1".equals(type)){//get tab
				String _key = (String) this.getFormHM().get("key");// 6：材料审查模板
				
				DomXml domXml = new DomXml();
				HashMap map = domXml.getJobtitleTemplates(this.getFrameconn());
				
				ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
				
				Iterator it = map.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					String key = (String)entry.getKey();
					if(_key.equals(key)){
						String value = (String)entry.getValue();
						String[] templateIdArr = value.split(",");
						
						for(String id : templateIdArr){
							if(StringUtils.isEmpty(id)){
								continue ;
							}
							String templateName = "";
							
							HashMap data = new HashMap();
							data.put("templateId", PubFunc.encrypt(id));
							data.put("templateName", reviewFileBo.getTabNameByTabId(id));
							dataList.add(data);
						}
					} else {
						continue ;
					}
				}
				
				
				this.getFormHM().put("data", dataList);
				this.getFormHM().put("support_subcomittee", jobtitleConfigBo.getParamConfig("college_eval"));
				
			} else if("2".equals(type)){//get page
				String tabid = (String) this.getFormHM().get("tabid");
				tabid = PubFunc.decrypt(tabid);
				
				ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
				dataList = jobtitleConfigBo.getTemplatePage(tabid);
				this.getFormHM().put("data", dataList);
				
			} else if("3".equals(type)){//get config
				HashMap map = jobtitleConfigBo.getJobtitleNoticeConfig();
				this.getFormHM().put("configmap", map);
				
			} else if("4".equals(type)){//save config
				
				ArrayList<MorphDynaBean> configArr = (ArrayList)this.getFormHM().get("configArr");
				String msg = jobtitleConfigBo.saveJobtitleNoticeConfig(configArr);
				this.getFormHM().put("msg", msg);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
