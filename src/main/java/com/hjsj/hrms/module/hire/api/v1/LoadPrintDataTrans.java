package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.ykcard.YkcardOutWord;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 招聘外网获取PDF登记表打印数据交易类
 */
public class LoadPrintDataTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		String tableId = (String)this.formHM.get("tableid");
		String type = (String)this.formHM.get("type");
        String filename = null;
        try {
        	if(StringUtils.isBlank(tableId)) {
        		ParameterXMLBo xmlBo = new ParameterXMLBo(this.frameconn,"1");
    			HashMap xmlMap = xmlBo.getAttributeValues();
        		String isAttach = "0";
                if(xmlMap.get("attach") != null && ((String)xmlMap.get("attach")).length() > 0) {
                    isAttach = (String)xmlMap.get("attach");
                }
    			EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.frameconn,isAttach);
    			if("printExam".equals(type)) {
    				if(xmlMap.get("admissionCard")!=null&&!"".equals((String)xmlMap.get("admissionCard"))){
    	                tableId = (String)xmlMap.get("admissionCard");
    	            }
    			}else if("showScore".equals(type)) {
    				tableId = employNetPortalBo.getScoreTabId();
    			}else {
    				tableId = employNetPortalBo.getResumeTemplateId(this.userView.getA0100());
    			}
    			if(StringUtils.equals("-1", tableId) || StringUtils.isBlank(tableId)) {
    				this.formHM.put("hasTable", "0");
    				throw GeneralExceptionHandler.Handle(new Exception("未配置登记表模板，请联系管理员！"));
    			}
    			if(StringUtils.equals("showScore", type) && !employNetPortalBo.canQueryScore(this.userView.getDbname(), this.userView.getA0100())) {
    				this.formHM.put("isPublish", "0");
    				throw GeneralExceptionHandler.Handle(new Exception("成绩还未到发布日期，请耐心等待！"));
    			}
        	}
        	
        	if(StringUtils.equals("-1", tableId)) {
        		this.formHM.put("hasTable", "0");
        	}
				
            YkcardOutWord outWord = new YkcardOutWord(this.userView, this.frameconn);
            filename = outWord.outPdfYkcard(Integer.parseInt(tableId), this.userView.getA0100(), "0", "1", this.userView.getDbname(), "selfinfo", "0", "1");
            this.formHM.put("tableId", tableId);
            this.formHM.put("fileName", PubFunc.encrypt(filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
