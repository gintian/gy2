package com.hjsj.hrms.module.kq.config.calendar.transaction;

import com.hjsj.hrms.module.kq.config.calendar.businessobject.impl.KqCalenderServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 保存工作日历参数设置
 */
public class KqSaveCalendarTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        JSONObject returnJson = new JSONObject();
        String return_code = "success";
        String return_msg = "success";
        try {
            String data = (String)this.getFormHM().get("data");
            JSONObject jsonObj = JSONObject.fromObject(data);
            String week=(String) jsonObj.get("week");
            JSONArray holiday = JSONArray.fromObject(jsonObj.get("holiday"));
            JSONArray turnRest = JSONArray.fromObject(jsonObj.get("turn_rest"));
            KqCalenderServiceImpl kqCalenderService=new KqCalenderServiceImpl(this.getUserView(), this.getFrameconn());
            //公休日
            kqCalenderService.saveRestWeek(week);
            //节假日
            ArrayList<HashMap<String, String>> addHoliday =new ArrayList<HashMap<String,String>>();
            ArrayList<HashMap<String, String>> updateHoliday =new ArrayList<HashMap<String,String>>();
            ArrayList<HashMap<String, String>> delHoliday =new ArrayList<HashMap<String,String>>();
            HashMap<String, String> map=null;
            for (int i = 0; i < holiday.size(); i++) {
                JSONObject jsObject = holiday.getJSONObject(i);
                map=new HashMap<String, String>();
                String state=jsObject.getString("state");
                map.put("feastName", jsObject.getString("name"));
                map.put("id", jsObject.getString("id"));
                map.put("feastDate", jsObject.getString("dates"));
              //节假日 state:0-不变，1-新增，2-修改，3-删除
                if ("0".equals(state)) {
                    continue;
                }else if ("1".equals(state)) {
                    addHoliday.add(map);
                }else if ("2".equals(state)) {
                    updateHoliday.add(map);
                }else if ("3".equals(state)) {
                    delHoliday.add(map);
                }
            }
            //公休日倒休
            ArrayList<HashMap<String, String>> addTurnRest =new ArrayList<HashMap<String,String>>();
            ArrayList<HashMap<String, String>> updateTurnRest =new ArrayList<HashMap<String,String>>();
            ArrayList<HashMap<String, String>> delTurnRest =new ArrayList<HashMap<String,String>>();
            for (int i = 0; i < turnRest.size(); i++) {
                JSONObject jsObject = turnRest.getJSONObject(i);
                map=new HashMap<String, String>();
                String state=jsObject.getString("state");
                map.put("weekDay", jsObject.getString("week_date"));
                map.put("id", jsObject.getString("id"));
                map.put("turnDay", jsObject.getString("turn_date"));
              //公休日倒休 state:0-不变，1-新增，2-修改，3-删除
                if ("0".equals(state)) {
                    continue;
                }else if ("1".equals(state)) {
                    addTurnRest.add(map);
                }else if ("2".equals(state)) {
                    updateTurnRest.add(map);
                }else if ("3".equals(state)) {
                    delTurnRest.add(map);
                }
            }
            if (kqCalenderService.saveRestWeek(week)
                    &&kqCalenderService.saveHolidayForList(addHoliday)
                    &&kqCalenderService.updateHolidayForList(updateHoliday)
                    &&kqCalenderService.saveTurnRestForList(addTurnRest)
                    &&kqCalenderService.updateTurnRestForList(updateTurnRest)
                    &&kqCalenderService.deleteTurnRestForList(delTurnRest)
                    &&kqCalenderService.deleteHolidayForList(delHoliday)) {
            }else {
                return_code = "fail";
                return_msg = "fail";
            }
        } catch (Exception e) {
            return_code = "fail";
            return_msg = "fail";
            e.printStackTrace();
        }
        returnJson.put("return_code", return_code);
        returnJson.put("return_msg", return_msg);
        this.formHM.put("returnStr", returnJson.toString());
        
    }

}
