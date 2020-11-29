package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

public class SaveThirdpartResumeParmTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            HashMap<String, String> parmMap = new HashMap<String, String>();
            String thirdpartName = (String) this.getFormHM().get("name");

            if ("BeiSen".equalsIgnoreCase(thirdpartName)) {
            	//地址
                String apiAddress = (String) this.getFormHM().get("apiAddress");
                //租户编号
                String tenantId = (String) this.getFormHM().get("tenantId");                
                String token = (String) this.getFormHM().get("token");
                //阶段ID或编码  和 状态ID或编码 
                String statusId = (String) this.getFormHM().get("statusId");
               
                if(StringUtils.isEmpty(apiAddress)){
                	apiAddress = "http://api.beisenapp.com/RecruitV2";        	       	
                }
                
                if(StringUtils.isEmpty(statusId)){
                	statusId = "S12:U12;";  	
                }
                               
                parmMap.put("apiurl", apiAddress);
                parmMap.put("tenantId", tenantId);
                parmMap.put("token", token);
                parmMap.put("statusId", statusId);
            }

            if ("Dayee".equalsIgnoreCase(thirdpartName)) {
                String apiurl = (String) this.getFormHM().get("apiurl");
                String corpcode = (String) this.getFormHM().get("corpcode");
                String userName = (String) this.getFormHM().get("userName");
                String passWord = (String) this.getFormHM().get("passWord");

                parmMap.put("apiUrl", apiurl);
                parmMap.put("corpCode", corpcode);
                parmMap.put("userName", userName);
                parmMap.put("passWord", passWord);
            }

            ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(
                    this.frameconn, thirdpartName, this.userView);
            base.saveResumeParam(parmMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
