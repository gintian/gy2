package com.hjsj.hrms.module.kq.config.calendar.businessobject.impl;

import com.hjsj.hrms.module.kq.config.calendar.businessobject.KqCalenderService;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class KqCalenderServiceImpl implements KqCalenderService {

        private UserView userView;
        private Connection conn;

        public KqCalenderServiceImpl(UserView userView, Connection conn) {
            this.userView = userView;
            this.conn = conn;
        }
    @Override
    public boolean checkHoliday(String id,String date, String kqYear) {
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer strSql = new StringBuffer();
        RowSet rs = null;
        strSql.append("select * from kq_feast where feast_dates like '%"+date+"%'");
        if (StringUtils.isNotEmpty(id)) {
            strSql.append(" and feast_id not in('" + id + "')");
        }
        try {
            rs= dao.search(strSql.toString());
            while (rs.next()) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            
        }
        return true;
    }
    @Override
    public boolean saveHolidayForList(ArrayList<HashMap<String, String>> holidayList) {
        ArrayList<ArrayList<Object>> insertParamList = new ArrayList<ArrayList<Object>>();
        ContentDAO dao = new ContentDAO(this.conn);
        IDGenerator idg = new IDGenerator(2, this.conn);
        try {
        String feast_id = idg.getId("kq_feast.feast_id");
        int feastId=Integer.valueOf(feast_id);
        for (HashMap<String, String> holiday : holidayList) {
            ArrayList<Object> insertValueList = new ArrayList<Object>();
            insertValueList.add(holiday.get("feastName"));
            insertValueList.add(holiday.get("feastDate"));
            insertValueList.add(feastId);
            insertParamList.add(insertValueList);
            feastId++;
        }
        String sql="insert into kq_feast ( feast_name,feast_dates,feast_id) values(?,?,?)";
            dao.batchInsert(sql, insertParamList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Override
    public boolean saveTurnRestForList(ArrayList<HashMap<String, String>> turnRestList) {
        ArrayList<ArrayList<Object>> insertParamList = new ArrayList<ArrayList<Object>>();
        ContentDAO dao = new ContentDAO(this.conn);
        IDGenerator idg = new IDGenerator(2, this.conn);
        try {
            String turn_id = idg.getId("kq_turn_rest.turn_id");
            int turnId=Integer.valueOf(turn_id);
            for (HashMap<String, String> turnRest : turnRestList) {
                ArrayList<Object> insertValueList = new ArrayList<Object>();
                String weekDay=turnRest.get("weekDay");
                String turnDay=turnRest.get("turnDay");
                weekDay= weekDay.replaceAll("\\.", "-");
                turnDay= turnDay.replaceAll("\\.", "-");
                insertValueList.add(turnId);
                 if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
                     insertValueList.add(DateUtils.getTimestamp(weekDay, "yyyy-MM-dd"));
                     insertValueList.add(DateUtils.getTimestamp(turnDay, "yyyy-MM-dd"));
                 }else {
                     insertValueList.add(turnRest.get("weekDay"));
                     insertValueList.add(turnRest.get("turnDay"));
                 }
                insertValueList.add("UN");
                insertParamList.add(insertValueList);
                turnId++;
            }
            String sql="insert into kq_turn_rest (turn_id,week_date,turn_date,B0110) values(?,?,?,?)";
            dao.batchInsert(sql, insertParamList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateHolidayForList(ArrayList<HashMap<String, String>> holidayList) {
        ArrayList<ArrayList<Object>> updateParamList = new ArrayList<ArrayList<Object>>();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            for (HashMap<String, String> holiday : holidayList) {
                ArrayList<Object> updateValueList = new ArrayList<Object>();
                updateValueList.add(holiday.get("feastName"));
                updateValueList.add(holiday.get("feastDate"));
                updateValueList.add(holiday.get("id"));
                updateParamList.add(updateValueList);
            }
            String sql="update kq_feast set  feast_name = ?,feast_dates =? where feast_id=?";
            dao.batchUpdate(sql, updateParamList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean saveRestWeek(String week) {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList nameList = new ArrayList();
        StringBuffer ssql = new StringBuffer();
        if (week.length()==0) {
            return true;
        }
        try {
            ssql.append("delete  from kq_restofweek  where b0110 ='UN'");
            dao.delete(ssql.toString(), nameList);
            ssql.delete(0, ssql.length());
            ssql.append("insert into kq_restofweek (b0110,rest_weeks)values(?,?)");
            nameList.add("UN");
            nameList.add(week);
            dao.insert(ssql.toString(), nameList);
            return true;
        } catch (Exception exx) {
           exx.printStackTrace();
           return false;
        }
    }

    @Override
    public boolean deleteHolidayForList(ArrayList<HashMap<String, String>> holidayList) {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList<ArrayList<Object>> deleteParamList = new ArrayList<ArrayList<Object>>();
        try {
            for (HashMap<String, String> holiday : holidayList) {
                ArrayList<Object> deleteValueList = new ArrayList<Object>();
                deleteValueList.add(holiday.get("id"));
                deleteParamList.add(deleteValueList);
            }
            String sql = "delete from kq_feast where feast_id =?";
            dao.batchUpdate(sql, deleteParamList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteTurnRestForList(ArrayList<HashMap<String, String>> turnRestList) {
        ArrayList<ArrayList<Object>> deleteParamList = new ArrayList<ArrayList<Object>>();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            for (HashMap<String, String> turnRest : turnRestList) {
                ArrayList<Object> deleteValueList = new ArrayList<Object>();
                Integer id = Integer.valueOf(turnRest.get("id"));
                deleteValueList.add(id);
                deleteParamList.add(deleteValueList);
            }
            String sql="delete from kq_turn_rest where turn_id = ? and b0110='UN'";
            dao.batchUpdate(sql, deleteParamList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateTurnRestForList(ArrayList<HashMap<String, String>> turnRestList) {
        ArrayList<ArrayList<Object>> updateParamList = new ArrayList<ArrayList<Object>>();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            for (HashMap<String, String> turnRest : turnRestList) {
                ArrayList<Object> updateValueList = new ArrayList<Object>();
                 if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
                     updateValueList.add(DateUtils.getTimestamp(turnRest.get("weekDay"), "yyyy-MM-dd"));
                     updateValueList.add(DateUtils.getTimestamp(turnRest.get("turnDay"), "yyyy-MM-dd"));
                 }else {
                     updateValueList.add(turnRest.get("weekDay"));
                     updateValueList.add(turnRest.get("turnDay"));
                 }
                updateValueList.add(turnRest.get("id"));
                updateParamList.add(updateValueList);
            }
            String sql="update kq_turn_rest set week_date=?,turn_date=?  where turn_id=?";
            dao.batchInsert(sql, updateParamList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
