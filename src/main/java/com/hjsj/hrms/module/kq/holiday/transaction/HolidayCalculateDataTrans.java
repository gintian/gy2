package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
/**
 * 假期管理公式计算
 * @Title:        HolidayCalculateDataTrans.java
 * @Description:  假期管理公式计算调用的交易类
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:09:15
 * @author        chenxg
 * @version       1.0
 */
public class HolidayCalculateDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String countStart = (String) this.getFormHM().get("countStart");
            String countEnd = (String) this.getFormHM().get("countEnd");
            String nbase = (String) this.getFormHM().get("nbase");
            String holidayYear = (String) this.getFormHM().get("holidayYear");
            String holidayType = (String) this.getFormHM().get("holidayType");
            if(StringUtils.isNotEmpty(holidayType))
                holidayType = PubFunc.decrypt(holidayType);
            
            String fieldData = (String) this.getFormHM().get("fieldDatas");
            if (fieldData == null || fieldData.length() <= 0)
                fieldData = "q1703";
            
            // 上年结余截止日期
            String balanceEndDate = (String) this.getFormHM().get("balanceEndDate");
            HolidayBo bo = new HolidayBo(this.getFrameconn(), this.userView);
            // 是否计算上年年假结余 1为计算，0为不计算
            String balanceValue = (String) this.getFormHM().get("balanceValue");
            if (!"1".equals(balanceValue))
                balanceValue = "0";
            
            if ("1".equals(balanceValue)) {
                // 验证可休天数和结余长度
                String msg = bo.checklength();
                if(StringUtils.isNotEmpty(msg)) {
                	this.getFormHM().put("msg", msg);
                	return;
                }
            }
            
            String clearZone = (String) this.getFormHM().get("clearZone");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fieldData", fieldData);
            map.put("nbase", nbase);
            map.put("holidayType", holidayType);
            map.put("holidayYear", holidayYear);
            map.put("countStart", countStart);
            map.put("countEnd", countEnd);
            map.put("balanceValue", balanceValue);
            map.put("balanceEndDate", balanceEndDate);
            map.put("clearZone", clearZone);
            bo.calCulateHoliday(map);
        } catch (Exception e) {
            this.getFormHM().put("msg", e.getMessage());
            e.printStackTrace();
        }
        
        this.getFormHM().put("msg", "0");
    }

}
