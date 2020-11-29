package com.hjsj.hrms.transaction.mobileapp.kq.holiday;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>Title: MyHoliday </p>
 * <p>Description:考勤我的假期信息查询 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-11-15 上午10:39:48</p>
 * @author tiany
 * @version 1.0
 */
public class MyHoliday {

    public void execute(UserView userView, HashMap hm, Connection conn) throws GeneralException {
        String message = "";
        String succeed ="false";
        try{
            String year = (String)hm.get("year");
            String a0100 = (String)hm.get("a0100");//人员编号
            String nbase = (String)hm.get("nbase");//人员编号
            //String unit_id = (String)hm.get("unit_id");//人员单位  xuj update 2015.07.22 考勤假期支持集团化不是根据人员操作单位取  
            String unit_id = this.getUserManagePrivOrgIdWithPre(conn, userView);
            HolidayBo holidayBo = new HolidayBo(conn,userView);
            List holidayTypes = new ArrayList(); 
            List holidayTypesData= holidayBo.searchMyHoliday(year,a0100,nbase,unit_id,holidayTypes);
            
            hm.put("holidayTypes", holidayTypes);
            hm.put("holidayTypesData", holidayTypesData);
            succeed ="true";
            
            
        }catch ( Exception e) {
            message=ResourceFactory.getProperty("mobileapp.kq.error.holidaySearchError");
            hm.put("message", message);
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally{
            hm.put("succeed", succeed);
        }
    }

    /**
     * 获取考勤集团化权限类，考虑到ManagePrivCode在考勤一开始就存在，故直接调用，不在重新在mobileapp下构建，移动服务兼容老包不会存在问题
     * @param conn
     * @param userView
     * @return
     */
    private String getUserManagePrivOrgIdWithPre(Connection conn, UserView userView) {
        String orgId;
        if (userView.isSuper_admin()) {
            orgId = "UN";
        } else {
            ManagePrivCode managePrivCode = new ManagePrivCode(userView, conn);
            orgId = "UN" + managePrivCode.getPrivOrgId();
        }

        return orgId;
    }
}
