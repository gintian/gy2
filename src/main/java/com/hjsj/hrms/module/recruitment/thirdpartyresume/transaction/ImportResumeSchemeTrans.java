package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @ClassName:
 * @Description: 导入简历
 * @author zhangcq
 * @date 2016-06-16 上午11:34:22
 * 
 */
public class ImportResumeSchemeTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        StringBuffer showInfor = new StringBuffer();
        HashMap hm = (HashMap) this.getFormHM();
        String thirdPartyName = (String) hm.get("tabName");
        HashMap<String, String> map = new HashMap<String, String>();
        
        if("BeiSen".equalsIgnoreCase(thirdPartyName)){
            String startDate = (String) hm.get("startDate");
            String endDate = (String) hm.get("endDate");
            map.put("startDate", startDate);
            map.put("endDate", endDate);
        } else if ("CaiZhi".equalsIgnoreCase(thirdPartyName)) {
            String filePath = (String) hm.get("path");
            String fileName = (String) hm.get("filename");
            map.put("filePath", filePath);
            map.put("fileName", fileName);
        }
        
        
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
        String blacklist_field = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST, "field");
        map.put("blacklist_field", blacklist_field);
        ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdPartyName, this.userView);
        ArrayList<String> msgList = base.getResumeFromThirdParty(map);
        if(msgList != null && msgList.size() > 0) {
            this.formHM.put("msg", msgList.get(0));
            return;
        } else 
            this.formHM.put("msg", "ok");
        
        // 导入简历提示信息
        String showInforLog = base.getShowInforLog();
        //解析不正确的文件
        String FlistLog = base.getFlistLog();
        //以下记录人员库中已存在
        String PlistLog = base.getPlistLog();
        //以下记录在黑名单库中存在
        String blackLog = base.getBlacklistLog();
        
        int m = 0;
        showInfor.append(showInforLog + "<br>");
        
        if(StringUtils.isNotEmpty(blackLog)){
            m = m + 1;
            showInfor.append(m + "、以下记录在黑名单库中存在,不能导入:<br>");
            showInfor.append(blackLog + "<br>");
        }
        
        if(StringUtils.isNotEmpty(PlistLog)){
            m = m + 1;
            showInfor.append(m + "、以下记录人员库中已存在,不能导入:<br>");
            showInfor.append(PlistLog + "<br>");
        }
        
        if(StringUtils.isNotEmpty(FlistLog)){
            m = m + 1;
            showInfor.append(m + "、以下人员的简历解析不对或缺失关键信息,不能导入:<br>");
            showInfor.append(FlistLog + "<br>");
        }
        
        String msg = base.getMsg();
        if(StringUtils.isNotEmpty(msg))
            this.userView.getHm().put("thirdPartyShowInfor", SafeCode.encode(msg));
        else
            this.userView.getHm().put("thirdPartyShowInfor", SafeCode.encode(showInfor.toString()));
            
    }
}
