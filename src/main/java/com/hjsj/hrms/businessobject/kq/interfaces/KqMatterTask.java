package com.hjsj.hrms.businessobject.kq.interfaces;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>
 * Title:KqAppList
 * </p>
 * <p>
 * Description:考勤待办任务类（刷卡、请假、加班、公出等审批待办）
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2013-08-16
 * </p>
 * 
 * @author zxj
 * @version 1.0
 * 
 */
public class KqMatterTask {

    private Connection   conn;
    private UserView     userView;
    private KqUtilsClass kqUtilsClass;

    public KqMatterTask(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        Init();
    }

    private void Init() {
        kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
    }

    /**
     * 待批刷卡数据(从当前考勤期间开始之日起、审批人为当前用户的记录)
     * 
     * @author zxj
     * @param list
     * @return
     */
    public ArrayList getKqCardTask(ArrayList list) {
        //没有刷卡审批权限，直接退出
        if (!this.userView.hasTheFunction("0C3709") && !this.userView.hasTheFunction("270609")) {
            return list;
        }
        
        DbWizard dbw = new DbWizard(this.conn);
        if (!dbw.isExistField("kq_originality_data", "curr_user", false)) {
            return list;
        }

        String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
      

        RowSet rSet = null;
        try {
            ArrayList kq_db_list = kqUtilsClass.getKqPreList();
            if (kq_db_list == null || kq_db_list.size() <= 0) {
                return list;
            }

            String start_date = getCurDurationStartDate();
            if ("".equals(start_date)) {
                return list;
            }

            String end_date = PubFunc.getStringDate("yyyy.MM.dd");

            StringBuffer sql = new StringBuffer();
            String sql_where = "";
            sql.append("SELECT COUNT(1) as cardnum FROM (");
            String start_time = "00:00";
            String end_time = "23:59";
            String where_c = " and sp_flag = '02'";

            KqCardData kqCardData = new KqCardData(this.userView, this.conn);
            sql_where = kqCardData.getSQL1(kq_db_list, a_code, start_date, end_date, start_time, end_time, where_c, "");
            sql.append(sql_where + ") aaa");

            ContentDAO dao = new ContentDAO(this.conn);

            rSet = dao.search(sql.toString());
            if (rSet.next()) {
                String cardNum = rSet.getString("cardnum");
                if (!"0".equals(cardNum)) {
                    CommonData cData = new CommonData();
                    cData.setDataName("需审批刷卡数据 " + cardNum + " 条");
                    cData.setDataValue("/kq/machine/search_card.do?b_query=link&view=1&action=search_card_data.do&target=mil_body&viewPost=kq&privtype=kq&returnvalue=zizhu");
                    cData.put("date", start_date);
                    list.add(cData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeRowSet(rSet);
        }

        return list;
    }
    /**
     * 刷卡待办更多
     */
    public ArrayList getKqCardTaskMore(ArrayList list) {

        //没有刷卡审批权限，直接退出
        if (!this.userView.hasTheFunction("0C3709") && !this.userView.hasTheFunction("270609")) {
            return list;
        }
        
        DbWizard dbw = new DbWizard(this.conn);
        if (!dbw.isExistField("kq_originality_data", "curr_user", false)) {
            return list;
        }

        String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
      

        RowSet rSet = null;
        try {
            ArrayList kq_db_list = kqUtilsClass.getKqPreList();
            if (kq_db_list == null || kq_db_list.size() <= 0) {
                return list;
            }

            String start_date = getCurDurationStartDate();
            if ("".equals(start_date)) {
                return list;
            }

            String end_date = PubFunc.getStringDate("yyyy.MM.dd");

            StringBuffer sql = new StringBuffer();
            String sql_where = "";
            sql.append("SELECT COUNT(1) as cardnum FROM (");
            String start_time = "00:00";
            String end_time = "23:59";
            String where_c = " and sp_flag = '02'";

            KqCardData kqCardData = new KqCardData(this.userView, this.conn);
            sql_where = kqCardData.getSQL1(kq_db_list, a_code, start_date, end_date, start_time, end_time, where_c, "");
            sql.append(sql_where + ") aaa");

            ContentDAO dao = new ContentDAO(this.conn);

            rSet = dao.search(sql.toString());
            if (rSet.next()) {
                String cardNum = rSet.getString("cardnum");
                if (!"0".equals(cardNum)) {
                    CommonData cData = new CommonData();
                    cData.setDataName("需审批刷卡数据 " + cardNum + " 条");
                    cData.setDataValue("/kq/machine/search_card.do?b_query=link&view=2&action=search_card_data.do&target=mil_body&viewPost=kq&privtype=kq&returnvalue=zizhu");
                    list.add(cData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeRowSet(rSet);
        }

        return list;
    
    }
    /**
     * 待批加班申请(从当前考勤期间开始之日起且必须具有业务平台或部门考勤加班审批权限)
     * 
     * @author zxj
     * @param list
     * @return
     */
    public ArrayList getKqOvertimeTask(ArrayList list) {

        //没有加班待办参数或加班审批权限时，直接退出
        if (!"1".equals(SystemConfig.getPropertyValue("overtime_task_display"))
                || (!this.userView.hasTheFunction("270102") && !this.userView.hasTheFunction("27010d")
                && !this.userView.hasTheFunction("0C3412") && !this.userView.hasTheFunction("0C341d"))) {
            return list;
        }
            
        RowSet rSet = null;
        try {
            StringBuffer whereINStr = new StringBuffer();
            whereINStr.append(" 1=1 ");

            String kind = "";
            String privcode = RegisterInitInfoData.getKqPrivCode(userView);
            String codevalue = RegisterInitInfoData.getKqPrivCodeValue(userView);
            if (!"".equals(codevalue)) {
                if ("UM".equalsIgnoreCase(privcode)) {
                    whereINStr.append(" and e0122 like '" + codevalue + "%'");
                    kind = "0";
                } else if ("@K".equalsIgnoreCase(privcode)) {
                    whereINStr.append(" and e01a1 like '" + codevalue + "%'");
                    kind = "1";
                } else if ("UN".equalsIgnoreCase(privcode)) {
                    whereINStr.append(" and b0110 like '" + codevalue + "%'");
                    kind = "2";
                }
            }

            //取当前期间开始日期
            String start_date = kqUtilsClass.getSafeCode(getCurDurationStartDate());
            if ("".equals(start_date)) {
                return list;
            }

            String end_date = "2049.12.31";
            
            //取考勤人员库
            ArrayList kq_dbase_list = kqUtilsClass.setKqPerList(codevalue, kind);
            if (null == kq_dbase_list || kq_dbase_list.size() == 0) {
                return list;
            }

            whereINStr.append(" AND (");
            for(int i=0; i<kq_dbase_list.size(); i++)
            {
                whereINStr.append("nbase='" + kq_dbase_list.get(i).toString() + "'");
                if(i != kq_dbase_list.size()-1) {
                    whereINStr.append(" OR ");
                }
            }
            whereINStr.append(")");
            
            SearchAllApp searchAllApp = new SearchAllApp(this.conn, this.userView);
            String cond0 = searchAllApp.getWhere2("Q11", start_date, end_date, "all", "02", "1", "0");
            if (null != cond0 && cond0.length() > 0) {
                whereINStr.append(" AND " + cond0);
            }
            
            cond0 = searchAllApp.getPrivWhere(kind, codevalue, kq_dbase_list, "Q11");
            if(null != cond0 && cond0.length()>0)
            {
                whereINStr.append(" and a0100 in (");
                whereINStr.append(cond0);
                whereINStr.append(")");
            }
            
            String sql = "SELECT COUNT(*) Q11COUNT FROM Q11 WHERE " + whereINStr.toString();
            ContentDAO dao = new ContentDAO(this.conn);
            rSet = dao.search(sql);
            if (rSet.next()) {
                String q11count = rSet.getString("Q11COUNT");
                if (!"0".equals(q11count)) {
                    CommonData cData = new CommonData();
                    cData.setDataName("需审批加班申请数据 " + q11count + " 条");
                    
                    //有业务平台权限加班审批权限的，优先进业务平台，否则进部门考勤
                    if (this.userView.hasTheFunction("270102") || this.userView.hasTheFunction("27010d")) {
                        cData.setDataValue("/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q11&sp_flag=02&viewPost=kq&returnvalue=");
                    } else {
                        cData.setDataValue("/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q11&sp_flag=02&viewPost=kq&privtype=kq&returnvalue=zizhu");
                    }
                    cData.put("date", start_date);
                    list.add(cData);
                }
            }  
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeRowSet(rSet);
        }

        return list;
    }

    private String getCurDurationStartDate() {

        ArrayList kq_list = RegisterDate.getKqDayList(this.conn);
        if (kq_list.size() <= 0) {
            return "";
        }

        return kq_list.get(0).toString();
    }
    
    private void closeRowSet(RowSet rSet) {
        if (null != rSet) {
            try {
                rSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
    }
}
