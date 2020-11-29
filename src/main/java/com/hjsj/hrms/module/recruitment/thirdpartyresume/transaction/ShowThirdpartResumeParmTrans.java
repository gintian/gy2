package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowThirdpartResumeParmTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            ThirdPartyResumeSourceFactory ThirdPartyResumeInfo = new ThirdPartyResumeSourceFactory();
            ArrayList<HashMap<String, String>> ThirdPartyInfo = ThirdPartyResumeInfo.getThirdPartyResumeSources();
            String tabId = "";
            StringBuffer tabs = new StringBuffer("[");
            for(int i = 0; i < ThirdPartyInfo.size(); i ++){
                HashMap<String, String> infoMap = ThirdPartyInfo.get(i);
                String valid = infoMap.get("valid");
                if("0".equalsIgnoreCase(valid))
                    continue;
                
                String name = infoMap.get("name");
                String fullname = infoMap.get("fullname");
                if(StringUtils.isEmpty(tabId))
                    tabId = name;
                
                tabs.append("{");
                tabs.append("title:'" + fullname + "',");
                tabs.append("icon:'../../../../module/recruitment/thirdpartyresume/images/" + name.toLowerCase() + ".png',");
                tabs.append("id:'" + name + "',");
                tabs.append("titleRotation:'left',");
                tabs.append("layout:{align: 'middle',type: 'vbox'},");
                tabs.append("},");
            }
            
            if(tabs.toString().endsWith(","))
                tabs.setLength(tabs.length() - 1);
            
            tabs.append("]");
            
            this.getFormHM().put("tabs", tabs.toString());
            this.getFormHM().put("tabId", tabId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
