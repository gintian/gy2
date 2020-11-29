package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * 防止导入简历时，后台运行时间过长导致页面链接失效
 * @Title:        PreventInvalidTrans.java
 * @Description:  防止页面链接失效
 * @Company:      hjsj     
 * @Create time:  2016-7-7 下午12:03:28
 * @author        chenxg
 * @version       1.0
 */
public class PreventInvalidTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	try {
    	    String deleteInfor = (String) this.getFormHM().get("deleteInfor");
    	    if("1".equalsIgnoreCase(deleteInfor))
    	        this.userView.getHm().put("thirdPartyShowInfor","");
    	    
    	    String showinfor = (String) this.userView.getHm().get("thirdPartyShowInfor");
    	    this.getFormHM().put("showInfor", StringUtils.isEmpty(showinfor) ? "" : showinfor);
    	} catch (Exception e) {
    	    e.printStackTrace();
        }
    }

}
