package com.hjsj.hrms.module.kq.shiftcheck.transaction;


import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftService;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl.ShiftServiceImpl;
import com.hjsj.hrms.module.kq.shiftcheck.businessobject.ShiftCheckService;
import com.hjsj.hrms.module.kq.shiftcheck.businessobject.impl.ShiftCheckServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**  
 * <p>Title: ShiftCheckTrans</p>  
 * <p>Description: 考勤排班审查</p>  
 * <p>Company: hjsj</p>
 * @date 2018年12月3日 下午1:38:50
 * @author linbz  
 * @version 7.5
 */  
public class ShiftCheckTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	
        try {
            String jsonStr = (String)this.getFormHM().get("jsonStr");
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            /**
             * init: 初始化
             */
            String actionType = (String) jsonObj.get("type");
            actionType = "," + actionType + ",";
            ShiftCheckService shiftCheckService = new ShiftCheckServiceImpl(this.userView, this.frameconn);
            // 图表数据
            if (actionType.contains(",all,") || actionType.contains(",shiftchart,")) {
            	
            	ArrayList orgOndutylist = shiftCheckService.listOrgOndutyCount(jsonObj);
                this.getFormHM().put("orgOndutylist",orgOndutylist);
                this.getFormHM().put("type", "shiftchart");
            }
            // 列表数据
            if(actionType.contains(",all,") || actionType.contains(",shiftlist,")) {
            	//每次从菜单进入页面时都先把过滤条件清掉
            	String firstFlag = (String) jsonObj.get("firstFlag");
            	if("1".equals(firstFlag)) {
            		this.userView.getHm().remove("shiftWhere");
            	}
            	
            	HashMap dataMap = shiftCheckService.getShiftCheckData(jsonObj);
                this.getFormHM().put("columnJson", (String)dataMap.get("columnJson"));
                this.getFormHM().put("column", (String)dataMap.get("column"));
                this.getFormHM().put("weekList", (ArrayList)dataMap.get("weekList"));
                this.getFormHM().put("dateJson", (String)dataMap.get("dateJson"));
                this.getFormHM().put("year", (String)dataMap.get("year"));
                this.getFormHM().put("month", (String)dataMap.get("month"));
                this.getFormHM().put("weekIndex", (String)dataMap.get("weekIndex"));
                // 44980 更改获取排班 审查SQL方式
//                this.getFormHM().put("shiftCheckSql", (String)dataMap.get("shiftCheckSql"));
                this.getFormHM().put("pageRows", (String)dataMap.get("pageRows"));
                this.getFormHM().put("type", "shiftlist");
            }
            
            if(actionType.contains(",changeSubmoudleId,")) {
            	ShiftService shift = new ShiftServiceImpl(this.userView, this.frameconn);
            	shift.changeSubmoudleId("shiftCheck");
            }
            // 功能授权
            JSONObject priv = new JSONObject();
    		priv.put("workAnalysisTablepriv", this.userView.hasTheFunction("272020301")?"1":"0");// 导出工作分析表
    		priv.put("shiftTablepriv", this.userView.hasTheFunction("272020302")?"1":"0");// 导出排班表
    		this.getFormHM().put("privs", priv);
            
        } catch (Exception e) {
        	e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
        }
    }
}
