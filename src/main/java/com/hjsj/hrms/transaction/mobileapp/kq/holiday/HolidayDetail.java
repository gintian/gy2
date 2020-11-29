package com.hjsj.hrms.transaction.mobileapp.kq.holiday;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>Title: HolidayDetail </p>
 * <p>Description: 移动考勤假期-休假明细</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-11-28 上午11:24:20</p>
 * @author tiany
 * @version 1.0
 */
public class HolidayDetail {

    public void execute(UserView userView, HashMap hm, Connection conn) throws GeneralException {
        String message = "";
        String succeed ="false";
        try{
            String year = (String)hm.get("year");
            String a0100 = (String)hm.get("a0100");//人员编号
            String nbase = (String)hm.get("nbase");//人员编号
            String unit_id = (String)hm.get("unit_id");//人员单位
            String pageIndex = (String)hm.get("pageIndex");
            String pageSize = (String)hm.get("pageSize");
            pageIndex=pageIndex==null?"1":pageIndex;
            pageSize=pageSize==null?"10":pageSize;
            HolidayBo holidayBo = new HolidayBo(conn,userView);
            List holidayDetailList= holidayBo.searchHolidayDetail(year,a0100,nbase,unit_id,pageIndex,pageSize);
            
            hm.put("holidayDetailList", holidayDetailList);
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

}
