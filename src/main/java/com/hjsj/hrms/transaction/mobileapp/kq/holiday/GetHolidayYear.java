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
 * <p>Title: GetHolidayYear </p>
 * <p>Description:获得假期年份和休假年份 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-11-26 上午11:17:24</p>
 * @author tiany
 * @version 1.0
 */
public class GetHolidayYear {
    public void execute(UserView userView, HashMap hm, Connection conn,String transType) throws GeneralException {
        String message = "";
        String succeed ="false";
        try{
            
            String a0100 = (String)hm.get("a0100");//人员编号
            String nbase = (String)hm.get("nbase");//人员编号
            HolidayBo holidayBo = new HolidayBo(conn,userView);
            List holidayYearList=null;
            if("6".equals(transType)){
                holidayYearList= holidayBo.getMyHolidayYears(nbase, a0100);
            }else if("7".equals(transType)){
                holidayYearList= holidayBo.getMyOvertimeForLeaveYears(nbase, a0100);
            }
            succeed ="true";
            hm.put("years", holidayYearList) ;
            
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
