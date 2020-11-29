package com.hjsj.hrms.module.kq.shiftcheck.transaction;


import com.hjsj.hrms.module.kq.shiftcheck.businessobject.ShiftCheckService;
import com.hjsj.hrms.module.kq.shiftcheck.businessobject.impl.ShiftCheckServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

/**  
 * <p>Title: ShiftCheckTrans</p>  
 * <p>Description: 考勤排班审查-导出</p>  
 * <p>Company: hjsj</p>
 * @date 2018年12月10日 上午午10:38:50
 * @author linbz  
 * @version 7.5
 */  
public class ExportShiftCheckTrans extends IBusiness {

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
            // 导出工作分析表
            if(actionType.contains(",workAnalysisTable,")) {
            	
            	String fileName = shiftCheckService.exportWorkAnalysisTable(jsonObj);
            	this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
            }
            // 导出排班表
            if(actionType.contains(",shiftTable,")) {
            	
            	String fileName = shiftCheckService.exportShiftTable(jsonObj);
            	this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
        }
    }
}
