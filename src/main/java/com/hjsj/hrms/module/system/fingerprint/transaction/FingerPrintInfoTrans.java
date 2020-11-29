package com.hjsj.hrms.module.system.fingerprint.transaction;

import com.hjsj.hrms.module.system.fingerprint.businessobject.FingerPrintInfoService;
import com.hjsj.hrms.module.system.fingerprint.businessobject.impl.FingerPrintInfoServiceImpl;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FingerPrintInfoTrans extends IBusiness {
    private Logger log = LoggerFactory.getLogger(FingerPrintInfoTrans.class);

    @Override
    public void execute() throws GeneralException {

        TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
        String tabId = frontProperty.getTabId();
        FingerPrintInfoService fingerprintinfoservice = new FingerPrintInfoServiceImpl(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
        String method = (String) this.formHM.get("method");
        boolean flag = false;
        String errorMsg = "";
        try {
            if ("initData".equalsIgnoreCase(method)) {//初始化
                Map dataMap = fingerprintinfoservice.initData();
                String jobNumber = (String) dataMap.get("jobNumber");
                String name = (String) dataMap.get("name");
                this.getFormHM().clear();
                this.getFormHM().put("jobNumber", jobNumber);
                this.getFormHM().put("name", name);
                flag = true;
            } else if ("faceSave".equalsIgnoreCase(method)) {//人脸存储
                String jobNumber = (String) this.getFormHM().get("jobNumber");
                String faceData = (String) this.getFormHM().get("faceData");
                flag = fingerprintinfoservice.saveFaceData(faceData, jobNumber);
            } else if ("FingerSave".equalsIgnoreCase(method)) {//指纹存储
                String jobNumber = (String) this.getFormHM().get("jobNumber");
                Map fingerDataMap = PubFunc.DynaBean2Map((MorphDynaBean) this.getFormHM().get("fingerModel"));
                flag = fingerprintinfoservice.saveFingerData(fingerDataMap, jobNumber);
                Map checkMap = calculateFinger(fingerDataMap);
                this.getFormHM().put("checkMap", checkMap);
            } else if ("FingerCheck".equalsIgnoreCase(method)) {//单个指纹复核
                String jobNumber = (String) this.getFormHM().get("jobNumber");
                String featureFinger = (String) this.getFormHM().get("featureFinger");
                String finger = (String) this.getFormHM().get("finger");
                flag = fingerprintinfoservice.checkFingerData(jobNumber,featureFinger,finger);
            }else if("FingerReview".equalsIgnoreCase(method)){//审核接口
                String jobNumber = (String) this.getFormHM().get("jobNumber");
                Map fingerDataMap = PubFunc.DynaBean2Map((MorphDynaBean) this.getFormHM().get("fingerModel"));
                flag = fingerprintinfoservice.revieFingerData(fingerDataMap, jobNumber);
            }else if("FingerReset".equalsIgnoreCase(method)){//重置指纹
                String jobNumber = (String) this.getFormHM().get("jobNumber");
                flag = fingerprintinfoservice.resetFingerData(jobNumber);
            }
        } catch (Exception e) {
            flag = false;
            errorMsg = e.getMessage();
            e.printStackTrace();
        }
        JSONObject returnStr = new JSONObject();
        //是否保存成功
        returnStr.put("return_code", flag == true ? "success" : "fail");
        returnStr.put("return_msg", errorMsg);
        this.getFormHM().put("returnStr", returnStr);
        this.getFormHM().put("result", flag);
    }

    private Map calculateFinger(Map fingerDataMap) {
        String fingerStr = "finger0,finger1,finger2,finger3,finger4,finger5,finger6,finger7,finger8,finger9";
        Map map = new HashMap();
        List checkFingerList = new ArrayList();
        List noCheckFingerList = new ArrayList();
        for (int i = 0; i < fingerStr.split(",").length; i++) {
            String key = fingerStr.split(",")[i];
            if (fingerDataMap.containsKey(key)) {
                checkFingerList.add(key);
            } else {
                noCheckFingerList.add(key);
            }
        }
        map.put("checkFinger", checkFingerList);
        map.put("noCheckFinger", noCheckFingerList);
        return map;
    }
}
