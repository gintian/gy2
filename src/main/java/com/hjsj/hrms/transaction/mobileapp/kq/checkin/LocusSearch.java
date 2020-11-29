package com.hjsj.hrms.transaction.mobileapp.kq.checkin;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>Title: MobileCheckInSearchTrans </p>
 * <p>Description: 移动平台轨迹查询（交易编码9102002102）</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-10-26 上午09:42:53</p>
 * @author tiany
 * @version 1.0
 */
public class LocusSearch {
    
    public void execute(UserView userView,HashMap hm ,Connection conn) throws GeneralException {
        String message = "";
        String succeed ="false";
        try{
            String startDate = (String)hm.get("startDate");
            String endDate = (String)hm.get("endDate");
            String a0100 = (String)hm.get("a0100");//人员编号
            String nbase = (String)hm.get("nbase");//人员编号
            String pageIndex = (String)hm.get("pageIndex");
            String pageSize = (String)hm.get("pageSize");
            pageIndex=pageIndex==null?"1":pageIndex;
            pageSize=pageSize==null?"10":pageSize;
            CheckInMainBo checkInBo = new CheckInMainBo(userView,conn);
            List locusList= checkInBo.searchLocus(a0100, nbase, startDate, endDate,pageIndex,pageSize);
            if(locusList!=null){
                hm.put("locusList", locusList);
                succeed ="true";
            }else{
                succeed ="false";
                message=ResourceFactory.getProperty("current.user.dbpre.no") ;
            }
           
        }catch (Exception e) {
            succeed ="false";
            message=ResourceFactory.getProperty("kq.netsign.in.fail");
            throw GeneralExceptionHandler.Handle(e);   
        } finally{
            hm.put("succeed", succeed);
            hm.put("message", message);
        }
    }

}
