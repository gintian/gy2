package com.hjsj.hrms.module.analyse.transcation;

import com.hjsj.hrms.module.analyse.bussinessobject.IAnalyseService;
import com.hjsj.hrms.module.analyse.bussinessobject.impl.IAnalyseServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈薪酬分析首界面〉
 *
 * @author caoqy
 * @Date 2019-12-18
 * @since 1.0.0
 */
public class AnalyseMainTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map dataMap = new HashMap();
        IAnalyseService iAnalyseService = new IAnalyseServiceImpl(this.getFrameconn(), this.getUserView());
        try {
            String menuid = (String) this.getFormHM().get("menuid");
            List analyseList = iAnalyseService.getAnalyseMainData(menuid);
            dataMap.put("analyseList", analyseList);
        } catch (Exception e) {
            return_code = "fail";
            return_msg = e.getMessage();
            e.printStackTrace();
        } finally {
            this.getFormHM().put("return_code", return_code);
            this.getFormHM().put("return_msg", return_msg);
            this.getFormHM().put("return_data", dataMap);
        }
    }
}
