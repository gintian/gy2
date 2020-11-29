package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @ClassName: SaveResumeImportSchemeTrans 
 * @Description: TODO保存对应指标集到数据库xml
 * @author zhangcq
 * @date 2016-06-13 上午11:34:22 
 *
 */
public class SaveResumeImportSchemeTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		 String thirdpartName = (String) this.getFormHM().get("name");
		//这个参数是从jspform下取得值所以过滤处理一下
		String dbname= (String) this.getFormHM().get("userbase");
		dbname=PubFunc.hireKeyWord_filter(dbname);
		String itemID = (String) hm.get("itemID");
		itemID = PubFunc.hireKeyWord_filter(itemID);
		String secitemID = (String) hm.get("secitemID");
		secitemID = PubFunc.hireKeyWord_filter(secitemID);
		String imptype = (String) hm.get("mode");  //更新方式
		imptype = PubFunc.hireKeyWord_filter(imptype);
		String synchronousFlag = (String) hm.get("synchronousFlag");  //更新方式
		synchronousFlag = PubFunc.hireKeyWord_filter(synchronousFlag);
		
		HashMap resumeMap = new HashMap();
		resumeMap.put("dbname", dbname);
		resumeMap.put("identifyfld", itemID);
		resumeMap.put("sencondfld", secitemID);
		resumeMap.put("imptype", imptype); 
		resumeMap.put("synchronousFlag", synchronousFlag); 
		ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdpartName, this.userView);
		base.saveResumeParam(resumeMap);
		ArrayList<LazyDynaBean> list =new ArrayList();
		String codearray =  (String) hm.get("codearray");
		JSONArray jsonArr = JSONArray.fromObject(codearray);
		for(int z = 0; z < jsonArr.size(); z++){
			LazyDynaBean contentBean = new LazyDynaBean();
		    JSONObject json = jsonArr.getJSONObject(z);
		    String selected = json.get("selected").toString();
		    String resumeset = json.get("resumeset").toString();
		    String resumesetId = json.get("resumesetid").toString();
		    selected = selected == null || "#".equals(selected) ? "" : selected;
		    resumesetId = resumesetId == null ? "" : resumesetId;
			contentBean.set("resumeset", resumeset);
			contentBean.set("ehrset", selected);
			contentBean.set("resumesetId", resumesetId);
			list.add(contentBean);
		}    
		base.saveResumefieldsetParam(list);
	}

}
