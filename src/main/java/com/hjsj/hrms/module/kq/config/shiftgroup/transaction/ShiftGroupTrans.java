package com.hjsj.hrms.module.kq.config.shiftgroup.transaction;

import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftGroupService;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl.ShiftGroupServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

/**  
 * <p>Title: ShiftGroupTrans</p>  
 * <p>Description: 班组详细信息交易类</p>  
 * <p>Company: hjsj</p>
 * @date 2018年10月28日 下午1:43:27
 * @author linbz  
 * @version 7.5
 */  
public class ShiftGroupTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
        try 
        {
        	String jsonStr = (String)this.formHM.get("jsonStr");
    		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        	//"type":"detail" , //detail: 班组信息；save: 新增、修改班组信息；delete: 删除班组
        	String type = jsonObj.getString("type");
        	
        	ShiftGroupService shiftGroupService = new ShiftGroupServiceImpl(this.userView,this.frameconn);
        	String returnStr = "";
        	if("detail".equals(type)) {
        		String groupId = jsonObj.getString("group_id");
        		returnStr = shiftGroupService.getShiftGroup(groupId);
        	}else if("save".equals(type)) {
        		returnStr = shiftGroupService.saveShiftGroup(jsonObj);
        	}else if("delete".equals(type)) {
        		String groupId = jsonObj.getString("group_id");
        		returnStr = shiftGroupService.delShiftGroup(groupId);
        	}
        	
        	this.getFormHM().put("returnStr", returnStr);
        	
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }


}
