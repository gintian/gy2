package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
/**
 * 查询第三方参数
 * @Title:        SearchThirdpartResumeParamTrans.java
 * @Description:  查询第三方参数
 * @Company:      hjsj     
 * @Create time:  2016-6-7 下午04:44:32
 * @author        chenxg
 * @version       1.0
 */
public class SearchThirdpartResumeParamTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        String thirdpartName = (String) this.getFormHM().get("name");
        try{
            LazyDynaBean paramBean = new LazyDynaBean();
            ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdpartName, this.userView);
            HashMap paramMap = base.getResumeParam();
            paramBean = (LazyDynaBean) paramMap.get("thirdPartyParm");

            if("BeiSen".equalsIgnoreCase(thirdpartName)) {
                String apiAddress = "";
                String tenantId = "";
                String token = "";
                String statusId = "";
                if(paramBean != null) {
                	//地址
                    apiAddress = (String) paramBean.get("apiurl");
                    //租户编号
                    tenantId = (String) paramBean.get("tenantId");                 
                    token = (String) paramBean.get("token");
                    //阶段ID或编码 和 状态ID或编码
                    statusId = (String) paramBean.get("statusId");
                 
                    if(StringUtils.isEmpty(apiAddress)){
                    	apiAddress = "http://api.beisenapp.com/RecruitV2";        	       	
                    }
                    
                    if(StringUtils.isEmpty(statusId)){
                    	statusId = "S12:U12;";  	
                    }
    
                }
                
                String defineShow = "false";
                if (userView.isSuper_admin() || userView.hasTheFunction("2606701")) 
                    defineShow = "true";
                
                String importShow = "false";
                if (userView.isSuper_admin() || userView.hasTheFunction("2606702")) 
                    importShow = "true";
                
                this.getFormHM().put("defineShow", defineShow);
                this.getFormHM().put("importShow", importShow);
                this.getFormHM().put("apiAddress", apiAddress);
                this.getFormHM().put("tenantId", tenantId);
                this.getFormHM().put("token", token);
                this.getFormHM().put("statusId", statusId);
            }
            
            if("DaYee".equalsIgnoreCase(thirdpartName)) {
                String apiUrl = "";
                String corpCode = "";
                String userName = "";
                String passWord = "";
                if(paramBean != null) {
                    apiUrl = (String) paramBean.get("apiUrl");
                    corpCode = (String) paramBean.get("corpCode");
                    userName = (String) paramBean.get("userName");
                    passWord = (String) paramBean.get("passWord");                
    
                }
                
                this.getFormHM().put("apiUrl", apiUrl);
                this.getFormHM().put("corpCode", corpCode);
                this.getFormHM().put("userName", userName);
                this.getFormHM().put("passWord", passWord);
            }
            
            this.getFormHM().put("tabId", thirdpartName);
        }catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
