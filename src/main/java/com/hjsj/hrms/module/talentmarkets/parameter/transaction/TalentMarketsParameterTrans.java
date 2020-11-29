package com.hjsj.hrms.module.talentmarkets.parameter.transaction;

import com.hjsj.hrms.module.talentmarkets.parameter.businessobject.TalentMarketsParameterService;
import com.hjsj.hrms.module.talentmarkets.parameter.businessobject.impl.TalentMarketsParameterServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Titile TalentMarketsParameterTrans
 * @Description 人才市场参数配置 加载和保存功能
 * @Company hjsj
 * @Create time: 2019年8月8日下午6:13:14
 * @author wangd
 * @version 1.0
 *
 */
public class TalentMarketsParameterTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
	    //默认返回成功，只有出现异常时才返回失败
	    String return_code = "success";
	    //默认返回的错误信息为空即可
	    String return_msg = StringUtils.EMPTY;
	    Map<String,Object> returnMap = new HashMap<String, Object>();
		TalentMarketsParameterService talentMarketsParameterService = new TalentMarketsParameterServiceImpl(this.frameconn);// 各种方法
		String fieldType = (String) this.formHM.get("fieldType");
		String tabId = (String) this.formHM.get("tabid");
		try {
			String type = (String) this.formHM.get("type");
			if ("save".equalsIgnoreCase(type)) {
			    //保存
				MorphDynaBean morphDynaBean = (MorphDynaBean) this.formHM.get("jsonStr");
				JSONObject jsonObject = JSONObject.fromObject(morphDynaBean);
				String str = jsonObject.toString();
                return_msg=talentMarketsParameterService.saveSettings(str);
                returnMap.put("return_code", return_code);
                returnMap.put("return_msg", return_msg);
                this.formHM.put("returnStr", returnMap);
			} else if("search".equalsIgnoreCase(type)) {
			    //查询 search
				JSONObject jsonObj=talentMarketsParameterService.loadSettings();
                returnMap.put("return_code",return_code);
                returnMap.put("return_msg", return_msg);
                returnMap.put("return_data", jsonObj);
				this.formHM.put("returnStr", returnMap);
			}else if("checkConfigurable".equalsIgnoreCase(type)){
				/*String postTabid = (String) this.formHM.get("postTabid");
				String applyTabid = (String) this.formHM.get("applyTabid");
				String hireTabid = (String) this.formHM.get("hireTabid");*/
				//检验是否能够配置
				HashMap checkData = talentMarketsParameterService.checkConfigurable();
				this.formHM.put("checkData", checkData);
			}else if("indicatorCorr".equalsIgnoreCase(type)){
				//获取指标对应数据
				if(StringUtils.isNotEmpty(fieldType)){
					String changeType = (String) this.formHM.get("changeType");
					String lengthLimit = (String) this.formHM.get("lengthLimit");
					String searchType = (String) this.formHM.get("searchType");
					ArrayList templateItems = talentMarketsParameterService.loadTemplateItems(fieldType,tabId,changeType,lengthLimit,searchType);
					this.formHM.put("templateItems", templateItems);
				}
			}
		} catch (Exception e) {
            return_code = "fail";
            return_msg = e.getMessage();
            returnMap.put("return_code",return_code);
            returnMap.put("return_msg",return_msg);
            this.formHM.put("returnStr", returnMap);
            e.printStackTrace();
		}
	}


}
