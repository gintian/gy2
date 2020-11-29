package com.hjsj.hrms.module.kq.config.parameter.transaction;


import com.hjsj.hrms.module.kq.config.parameter.businessobject.KqParameterService;
import com.hjsj.hrms.module.kq.config.parameter.businessobject.impl.KqParameterServiceImpl;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**  
 * <p>Title: KqParameterTrans</p>  
 * <p>Description: 考勤管理参数</p>  
 * <p>Company: hjsj</p>
 * @date 2018年11月16日 下午4:47:14
 * @author linbz  
 * @version 7.5
 */  
public class KqParameterTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	
        JSONObject returnJson = new JSONObject();
        returnJson.put("return_code", "success");
        try {
            String jsonStr = (String)this.getFormHM().get("jsonStr");
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            /**
             * init: 初始化
             * save: 保存数据
             */
            String actionType = (String) jsonObj.get("type");
            KqParameterService kqParameterService = new KqParameterServiceImpl(this.getUserView(), this.getFrameconn());
            // 初始化方法
            if ("init".equalsIgnoreCase(actionType)) {
            	JSONObject return_data = new JSONObject();
            	// 应急中心个性化标识
            	String hlwyjzx_flag = "hlwyjzx".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")) ? "1" : "0";
            	return_data.put("hlwyjzx_flag", hlwyjzx_flag);
            	
            	HashMap map = kqParameterService.getKqParameter();
            	return_data.put("kqParameMap", map);
            	ArrayList<HashMap<String, String>> listNbase = kqParameterService.listNbase();
                return_data.put("nbase_all", listNbase);
                ArrayList<HashMap<String, String>> listA01 = kqParameterService.listA01Str("0");
                return_data.put("listA01", listA01);
                ArrayList<HashMap<String, String>> listA01UM = kqParameterService.listA01Str("1");
                return_data.put("listA01UM", listA01UM);
                // 开始结束日期
                ArrayList<HashMap<String, String>> listDateField = kqParameterService.listDateFieldItemid("");
                return_data.put("listDateField", listDateField);
                // 获取子集列表
                ArrayList<HashMap<String, String>> listFieldSet = kqParameterService.listFieldSet("");
                return_data.put("listFieldSet", listFieldSet);
                // 获取变动子集信息
                String setid = (String)map.get("setid");
                ArrayList<HashMap<String, String>> listChangeUM = kqParameterService.listFieldItemid(setid, "0");
                return_data.put("listChangeUM", listChangeUM);
                ArrayList<HashMap<String, String>> listChangeDate = kqParameterService.listFieldItemid(setid, "1");
                return_data.put("listChangeDate", listChangeDate);
                // 获取请假子集信息
                HashMap leaveMap = (HashMap)map.get("leave_subset");
                String leave_setid = (String)leaveMap.get("setid");
                ArrayList<HashMap<String, String>> leaveListDate = kqParameterService.listFieldItemid(leave_setid, "1");
                return_data.put("leaveListDate", leaveListDate);
                ArrayList<HashMap<String, String>> leaveListStr = kqParameterService.listFieldItemid(leave_setid, "4");
                return_data.put("leaveListStr", leaveListStr);
                ArrayList<HashMap<String, String>> leaveListCode = kqParameterService.listFieldItemid(leave_setid, "3");
                return_data.put("leaveListCode", leaveListCode);
                // 获取公出子集信息
                HashMap officeleaveMap = (HashMap)map.get("officeleave_subset");
                String officeleave_setid = (String)officeleaveMap.get("setid");
                ArrayList<HashMap<String, String>> officeleaveListDate = kqParameterService.listFieldItemid(officeleave_setid, "1");
                return_data.put("officeleaveListDate", officeleaveListDate);
                ArrayList<HashMap<String, String>> officeleaveListStr = kqParameterService.listFieldItemid(officeleave_setid, "4");
                return_data.put("officeleaveListStr", officeleaveListStr);
                ArrayList<HashMap<String, String>> officeleaveListCode = kqParameterService.listFieldItemid(officeleave_setid, "3");
                return_data.put("officeleaveListCode", officeleaveListCode);
                // 获取加班子集信息
                HashMap overtimeMap = (HashMap)map.get("overtime_subset");
                String overtime_setid = (String)overtimeMap.get("setid");
                ArrayList<HashMap<String, String>> overtimeListDate = kqParameterService.listFieldItemid(overtime_setid, "1");
                return_data.put("overtimeListDate", overtimeListDate);
                ArrayList<HashMap<String, String>> overtimeListStr = kqParameterService.listFieldItemid(overtime_setid, "4");
                return_data.put("overtimeListStr", overtimeListStr);
                ArrayList<HashMap<String, String>> overtimeListCode = kqParameterService.listFieldItemid(overtime_setid, "3");
                return_data.put("overtimeListCode", overtimeListCode);
                
                returnJson.put("return_data", return_data);
            }
            // 保存
            else if("save".equalsIgnoreCase(actionType)){

                String returnStr = kqParameterService.saveKqParameter(jsonObj);
                returnJson = JSONObject.fromObject(returnStr);
            }
            // 更换变动子集
            else if("kqChangeSetid".equalsIgnoreCase(actionType)){
            	String setid = (String) jsonObj.get("setid");
            	JSONObject return_data = new JSONObject();
            	ArrayList<HashMap<String, String>> listChangeUM = kqParameterService.listFieldItemid(setid, "0");
                return_data.put("listChangeUM", listChangeUM);
                ArrayList<HashMap<String, String>> listChangeDate = kqParameterService.listFieldItemid(setid, "1");
                return_data.put("listChangeDate", listChangeDate);
                
                returnJson.put("return_data", return_data);
            }
            // 更换申请子集联动信息
            else if("kqApplySetid".equalsIgnoreCase(actionType)){
            	String setid = (String) jsonObj.get("setid");
            	JSONObject return_data = new JSONObject();
                ArrayList<HashMap<String, String>> listDate = kqParameterService.listFieldItemid(setid, "1");
                return_data.put("listDate", listDate);
                ArrayList<HashMap<String, String>> listStr = kqParameterService.listFieldItemid(setid, "4");
                return_data.put("listStr", listStr);
                ArrayList<HashMap<String, String>> listCode = kqParameterService.listFieldItemid(setid, "3");
                return_data.put("listCode", listCode);
                
                returnJson.put("return_data", return_data);
            }
            
            this.getFormHM().put("returnStr",returnJson);

        } catch (Exception e) {
            returnJson.put("return_code", "fail");
            returnJson.put("return_msg", ResourceFactory.getProperty("kq.machine.error"));
            this.getFormHM().put("returnStr",returnJson);
        }
    }
}
