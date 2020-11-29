package com.hjsj.hrms.transaction.mobileapp.kq.checkin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>Title: CheckIn </p>
 * <p>Description: 移动考勤签到</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-10-30 上午10:01:38</p>
 * @author tiany
 * @version 1.0
 */
public class CheckIn {
    public void execute(UserView userView ,HashMap hm ,Connection conn) throws GeneralException{
        String message = "";
        String succeed ="false";
        //签到地址名称
        String mylocation=(String)  hm.get("mylocation");
        //签到说明
        String checkInDesc=(String)  hm.get("checkInDesc");
        //签到点经度和纬度
        String longitude=(String)hm.get("longitude");
        String latitude=(String)hm.get("latitude") ;
        String isCommon=(String)hm.get("isCommon") ;
        String phoneBindFlag=(String)hm.get("phoneBindFlag") ;
        //xuj update 2015-3-6 移动打卡走类考勤机方式不做排班等验证
        String ifCheckKQClass = SystemConfig.getPropertyValue("ifCheckKQClass");
        if("1".equals(ifCheckKQClass)){
        	message = check(userView, conn);//检查签到条件是否符合 排班，假期，打卡时间范围，检查
        }
        if(message.length()==0){
        	 CheckInMainBo MCABo = new CheckInMainBo(userView, conn);
             message= MCABo.CheckInSave(mylocation, checkInDesc, longitude, latitude,isCommon,phoneBindFlag);  
             if(message.length()==0){
                 succeed ="true";
             }
        }
       
        String transType = (String)hm.get("transType");
        hm.put("transType", transType);
        hm.put("succeed", succeed);
        hm.put("message", message);
    }
    /**
     * 排班，假期，打卡时间范围，检查
     * @param userView
     * @param conn
     * @return
     * @throws GeneralException
     */
 private String check(UserView userView, Connection conn) throws GeneralException {
	 NetSignIn netSignIn = new NetSignIn(userView,conn);
	   
	   String nbase = userView.getDbname();
	   String a0100 = userView.getA0100();
	   String work_date = netSignIn.getWork_date();
	   String work_tiem = PubFunc.getStringDate("HH:mm");
	   // 规定时间间隔内不能签多次
	   if(!netSignIn.IsExists(nbase,a0100,work_date,work_tiem))
	   {
		   return ResourceFactory.getProperty("kq.netsign.error.notrepeatsign");					   
	   }	
	   // 是否刷卡
	   if(!netSignIn.ifNetSign(nbase,a0100,work_date,work_tiem))
	   {
	 	   return ResourceFactory.getProperty("kq.netsign.error.notsigninleavetime");
	   }
	   // 得到适合的班次
	   ArrayList classList = netSignIn.getClassID(nbase, a0100, userView.getUserOrgId(), userView.getUserDeptId(), userView.getUserPosId(), work_date);
	   if (classList == null || classList.size() <= 0) 
	   {
		   return ResourceFactory.getProperty("kq.netsign.error.notarrange.in");
	   }
	   // 签到范围
	   if(!netSignIn.signInScope(nbase,a0100,userView.getUserOrgId(),userView.getUserDeptId(),userView.getUserPosId(),work_date,work_tiem,"0"))//0签到
	   {
		   return ResourceFactory.getProperty("kq.netsign.error.notinvalidtime.in");					 
	   }
		
	   return "";
	}
/**
 * @throws GeneralException 
  * 
  * @param conn 
 * @param hm 
 * @param userView 
 * @Title: getCheckInfo   
  * @Description: //获取签到检查信息（签到前查询签到点和签到说明的库内长度,签到点和范围控制，手机绑定标示等)
  * @param  
  * @return void    
 * @throws SQLException 
  * @throws
  */
    public void getCheckInfo(UserView userView, HashMap hm, Connection conn) throws GeneralException, SQLException {
        Map messageMap = new HashMap();
        String transType = (String)hm.get("transType");
        String succeed ="false";
        CheckInMainBo CIMBO = new CheckInMainBo(userView, conn);
        messageMap= CIMBO.getCheckInfo();  
        hm.putAll(messageMap);
        succeed ="true";
        hm.put("succeed", succeed);
        hm.put("transType", transType);
    }
    
    /**
     * 
     * @Title: getCheckOutInfo   
     * @Description:    
     * @param userView
     * @param hm
     * @param conn
     * @throws GeneralException
     * @throws SQLException 
     * @return void
     */
    public void getCheckOutInfo(UserView userView, HashMap hm, Connection conn) throws GeneralException, SQLException {
        CheckInMainBo CIMBO = new CheckInMainBo(userView, conn);
        // 定位的位置
        String location = (String) hm.get("location");
        if(location != null && location.length() > 0)
        {
        	List checkOutInfo = CIMBO.getCheckOutInfo(location);  
            hm.put("checkOutInfo", checkOutInfo);
        }
        hm.put("succeed", "true");
    }
    
}
