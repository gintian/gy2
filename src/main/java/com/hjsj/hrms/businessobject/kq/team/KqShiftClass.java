package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class KqShiftClass {
    private Connection conn;
    private UserView   userView;
    private float      tiems   = 0;
    private float      days    = 0;
    private String     where_c;
    private ArrayList  db_list = new ArrayList();
    private String     a0100;
    private String     nbase;
    // 是否是自助用户的排班
    private boolean    self    = false;

    public String getWhere_c() {
        return where_c;
    }

    public void setWhere_c(String where_c) {
        this.where_c = where_c;
    }

    public KqShiftClass() {
    }

    public KqShiftClass(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
     * 通过周期班次得到对应基本班次的序号
     * @param shift_id
     * @return
     */
    public ArrayList getClassIdFromShiftId(String shift_id) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("select " + KqClassArrayConstant.kq_shift_class_classID);
        sql.append(" from " + KqClassArrayConstant.kq_shift_class_table + "");
        sql.append(" where " + KqClassArrayConstant.kq_shift_class_shiftID + "='" + shift_id + "'");
        sql.append(" order by " + KqClassArrayConstant.kq_shift_class_seq);
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                list.add(rs.getString(KqClassArrayConstant.kq_shift_class_classID));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    /**
     * 增加周期班次
     * @param shift_name
     * @throws GeneralException
     */
    public void addKqShift(String shift_name) throws GeneralException {
        if (shift_name == null || shift_name.length() <= 0) {
            return;
        }
        
        ArrayList list = new ArrayList();
        IDGenerator idg = new IDGenerator(2, this.conn);
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            String shift_id = idg.getId(KqClassArrayConstant.kq_shift_table + "." + KqClassArrayConstant.kq_shift_ID).toUpperCase();
            String sql = "insert into " + KqClassArrayConstant.kq_shift_table + " (" + KqClassArrayConstant.kq_shift_ID + ", " + KqClassArrayConstant.kq_shift_name + ") values (?,?)";
            list.add(shift_id);
            list.add(shift_name);
            dao.insert(sql, list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 修改周期班名称
     * @param shift_name
     * @param shift_id
     * @throws GeneralException
     */
    public void updateKqShiftName(String shift_name, String shift_id) throws GeneralException {
        if (shift_name == null || shift_name.length() <= 0) {
            return;
        }
        if (shift_id == null || shift_id.length() <= 0) {
            return;
        }
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        sql.append("update " + KqClassArrayConstant.kq_shift_table + " set");
        sql.append(" " + KqClassArrayConstant.kq_shift_name + "='" + shift_name + "'");
        sql.append(" where " + KqClassArrayConstant.kq_shift_ID + "='" + shift_id + "'");
        try {
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改周期班次的信息
     * @param shift_name
     * @param week_flag
     * @param feast_flag
     * @param shift_id
     * @throws GeneralException
     */
    public void updateKqOneShift(String shift_name, String week_flag, String feast_flag, String shift_id) throws GeneralException {
        if (shift_id == null || shift_id.length() <= 0) {
            return;
        }
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        sql.append("update " + KqClassArrayConstant.kq_shift_table + " set");
        sql.append(" " + KqClassArrayConstant.kq_shift_feast_flag + "='" + feast_flag + "',");
        sql.append("" + KqClassArrayConstant.kq_shift_week_flag + "='" + week_flag + "',");
        sql.append(" where " + KqClassArrayConstant.kq_shift_ID + "='" + shift_id + "'");
        try {
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteKqShiftName(String shift_id) throws GeneralException {

        if (shift_id == null || shift_id.length() <= 0) {
            return;
        }
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        sql.append("delete from " + KqClassArrayConstant.kq_shift_table + "");
        sql.append(" where " + KqClassArrayConstant.kq_shift_ID + "='" + shift_id + "'");
        ArrayList list = new ArrayList();
        try {
            dao.delete(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap getCodeItem(String a_code) {
        HashMap hashmap = new HashMap();
        if (a_code != null && a_code.length() > 0) {
            hashmap.put("codesetid", a_code.substring(0, 2));
            hashmap.put("codeitemid", a_code.substring(3));
        }
        return hashmap;
    }

    public String returnShiftHtml(ArrayList datelist, String a_code, String nbase) throws GeneralException {
        StringBuffer html = new StringBuffer();
        html.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'>");
        html.append("<thead> <tr> ");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.sunday") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.monday") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.tuesday") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.wednesday") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.thursday") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.firday") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.Saturday") + "</td>");
        html.append(" </tr></thead> ");
        html.append(getDateHteml(datelist, a_code, nbase));
        String sql = "select item_unit from kq_item where item_id like '27'";
        String state = "0";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                CodeItem codeValue = AdminCode.getCode("28", rs.getString("item_unit"));
            	String codename = codeValue.getCodename();
            	if ("天".equals(codename)) {
            		state = "1";
            	}
            }
        } catch (Exception e) {
            throw new GeneralException("系统代码项28计量单位为空，请添加后再操作！");
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        String timeUnit = "";
        float workTimeLen = 0;
        if ("0".equals(state)) {
            timeUnit = KqConstant.Unit.HOUR_DESC;
            workTimeLen = this.tiems;
        } else {
            timeUnit = KqConstant.Unit.DAY_DESC;
            workTimeLen = this.days;
        }
        html.append("<tr>");
        html.append("<td align='center' nowrap bgcolor='#CCCCFF' class='RecordRow common_background_color'> 合计（"+timeUnit+"）</td>");
        html.append("<td colspan='6' align='left' nowrap bgcolor='#FFFF99' class='RecordRow'>");
        html.append("&nbsp;" + workTimeLen + "</td></tr>");
        html.append("</table>");
        return html.toString();
    }

    /**
     * 日历
     * @param datelist
     * @param a_code
     * @param nbase
     * @return
     */
    private String getDateHteml(ArrayList datelist, String a_code, String nbase) throws GeneralException {
        try {
            StringBuffer html = new StringBuffer();
            int theRows = datelist.size() / 7;
            int mod = datelist.size() % 7;
            if (mod > 0) {
                theRows = theRows + 1;
            }
            String fristday = datelist.get(0).toString();
            String end_day = datelist.get(datelist.size() - 1).toString();
            String flag = "1";
            if ("UN".equals(a_code) && (where_c == null || where_c.length() <= 0)) {
                flag = "0";
            } else {
                flag = "1";
            }
            ArrayList recordlist = getRecord(datelist.size(), fristday, end_day, a_code, nbase,"","","");

            Date date = DateUtils.getDate(fristday, "yyyy.MM.dd");
            String FirstDay = KqUtilsClass.getWeekName(date);
            if (datelist.size() == 28) {
                if (!FirstDay.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.sunday"))) {
                    theRows = theRows + 1;
                }
            }
            String rest = KQRestOper.getRestStrTurn(FirstDay);
            if (rest.indexOf("7") != -1) {
                rest = "0,";
            }
            if (rest != null && rest.length() > 1) {
                rest = rest.substring(0, 1);
            }
            int theFirstDay = Integer.parseInt(rest);
            int theMonthLen = theFirstDay + datelist.size();
            if (7 - theFirstDay < mod) {
                theRows = theRows + 1;
            }
            int n = 0;
            int day = 0;
            String day_str = "";
            String nbase1 = PubFunc.encrypt(nbase);
            String a_code1 = PubFunc.encrypt(a_code);
            for (int i = 0; i < theRows; i++) {
                html.append("<tr>");
                for (int j = 0; j < 7; j++) {
                    n++;
                    if (n > theFirstDay && n <= theMonthLen) {
                        day = n - theFirstDay - 1;
                        day_str = datelist.get(day).toString();
                        String tsd_str = getTdStr(recordlist, day_str, flag);
                        String onDblClick = "";
                        if(userView.hasTheFunction("270700") || userView.hasTheFunction("0C3500"))
                        {
                            onDblClick="onDblClick=\"javascript:editClass('"+nbase1+"','"+a_code1+"','"+day_str+"','1')\"";
                        }                        
                        html.append(getOneTd(tsd_str, onDblClick));
                    } else {
                        html.append(getOneTd("&nbsp;", ""));
                    }
                }
                html.append("</tr>");
            }
            return html.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

    }

    /**
     * 得到纪录班次时间内的表格内容
     * @param day_str
     * @param recordlist
     * @param flag
     * @return
     * @throws GeneralException
     */

    private String getTdStr(ArrayList recordlist, String day_str, String flag) throws GeneralException {
        StringBuffer str_html = new StringBuffer();
        if (!"3".equals(flag)) {
            str_html.append(day_str + "<br>");
        }
        String a0100 = "";
        String nbase = "";
        if (!"0".equals(flag)) {
            for (int i = 0; i < recordlist.size(); i++) {
                HashMap hashmap = (HashMap) recordlist.get(i);
                String q03z0 = hashmap.get("q03z0").toString();
                if (day_str.equals(q03z0.trim())) {
                    String n_a0100 = (String) hashmap.get("a0100");
                    String n_nbase = (String) hashmap.get("nbase").toString();
                    if (a0100 != null && a0100.length() > 0) {
                        if (!a0100.equals(n_a0100)) {
                            ;
                        }
                        break;
                    } else {
                        a0100 = n_a0100;
                    }
                    if (nbase != null && nbase.length() > 0 && !"all".equalsIgnoreCase(nbase)) {
                        if (!nbase.equals(n_nbase)) {
                            ;
                        }
                        break;
                    } else {
                        nbase = n_nbase;
                    }
                    String name = (String) hashmap.get("name");
                    String onduty_1 = (String) hashmap.get("onduty_1");
                    String offduty_1 = (String) hashmap.get("offduty_1");
                    String onduty_2 = (String) hashmap.get("onduty_2");
                    String offduty_2 = (String) hashmap.get("offduty_2");
                    String onduty_3 = (String) hashmap.get("onduty_3");
                    String offduty_3 = (String) hashmap.get("offduty_3");
                    str_html.append(name);
                    boolean isXX = false;
                    if (onduty_1 != null && onduty_1.length() > 0 && offduty_1 != null && offduty_1.length() > 0) {
                        str_html.append("<br>"+onduty_1+"~"+offduty_1);
                        getWork_Time(onduty_1, offduty_1);
                        isXX = true;
                    }
                    if (onduty_2 != null && onduty_2.length() > 0 && offduty_2 != null && offduty_2.length() > 0) {
                        str_html.append("<br>"+onduty_2+"~"+offduty_2);
                        getWork_Time(onduty_2, offduty_2);
                        isXX = true;
                    }
                    if (onduty_3 != null && onduty_3.length() > 0 && offduty_3 != null && offduty_3.length() > 0) {
                        str_html.append("<br>"+onduty_3+"~"+offduty_3);
                        getWork_Time(onduty_3, offduty_3);
                        isXX = true;
                    }
                    /*if(onduty_4!=null&&onduty_4.length()>4&&offduty_4!=null&&offduty_4.length()>4)
                    {
                        str_html.append("<br>"+onduty_4+"~"+offduty_4);
                        getWork_Time(onduty_4,offduty_4);
                    }*/
                    if (isXX) {
                        this.days++;
                    }
                    break;
                }
            }
        } else {
            str_html.append("<br><br>");
        }
        return str_html.toString();
    }

    /**
     * 一个表格
     * @param str
     * @return
     */
    private String getOneTd(String str, String onDblClick_str) {
        StringBuffer str_html = new StringBuffer();
        String name = str;
//        if (str != null && str.indexOf("<br>") != -1) {
//            int index = str.indexOf("<br>") + 4;
//            name = str.substring(index);
//        }
        if (name.endsWith("<br>休息") || "休息".equals(name)) {
            if("sign".equals(onDblClick_str))
            {               
                str_html.append("<td align='center' bgcolor='#99FF99' class='RecordRow' style='border-left:none;' onClick=\"javascript:tr_onclick(this,'#99FF99')\" " + onDblClick_str + " nowrap>");
            }else
            {
                str_html.append("<td align='center' bgcolor='#99FF99' class='RecordRow' onClick=\"javascript:tr_onclick(this,'#99FF99')\" " + onDblClick_str + " nowrap>");
            }
            str_html.append(str);
            str_html.append("</td>");
        } else {
            if("sign".equals(onDblClick_str))
            {               
                str_html.append("<td align='center' class='RecordRow common_border_color'  style='height:70;border-left:none;word-wrap:break-all;' onClick=\"javascript:tr_onclick(this,'')\" " + onDblClick_str + " nowrap>");
            }else
            {
                str_html.append("<td align='center' class='RecordRow common_border_color' style='height:70' onClick=\"javascript:tr_onclick(this,'')\" " + onDblClick_str + " nowrap>");
            }
            str_html.append(str);
            str_html.append("</td>");
        }
        return str_html.toString();
    }

    /**
     * 得到员工排班的一个期间的的对应纪录
     * @param day_str
     * @param a_code
     * @param nbase
     * @return
     * @throws GeneralException
     */
    public ArrayList getRecord(int days, String start_day, String end_day, String a_code, String nbase,String  start_date,String name,String selectShowBar) throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer day_where = new StringBuffer();
        day_where.append(" and " + KqClassArrayConstant.kq_employ_shift_q03z0 + ">='" + start_day + "'");
        day_where.append(" and " + KqClassArrayConstant.kq_employ_shift_q03z0 + "<='" + end_day + "'");
        if(StringUtils.isNotEmpty(start_date)&&"1".equals(selectShowBar)&& StringUtils.isNotBlank(name) &&days==7){
            start_date = start_date.replace("-",".");
            day_where.append(" and a0100 in (select a0100 from kq_employ_shift LEFT JOIN kq_class ON kq_employ_shift.class_id=kq_class.class_id ");
            day_where.append("where q03z0='"+start_date+"' and nbase='" + nbase + "' and ");
            day_where.append( "name in ("+name+") )");
           
        }
//        if(StringUtils.isNotEmpty(str))
        String sql = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        HashMap hashmap = null;
        try {
            if (a_code.indexOf("EP") != -1) {

                sql = getJoinKqClass(nbase, a_code, day_where.toString());
                rs = dao.search(sql, 0, 31);
                int i = 0;
                while (rs.next()) {
                    if (i >= days) {
                        break;
                    } else {
                        i++;
                    }
                    hashmap = new HashMap();
                    hashmap.put("q03z0", rs.getString("q03z0") != null && rs.getString("q03z0").length() > 0 ? rs.getString("q03z0") : "");
                    hashmap.put("a0100", rs.getString("a0100") != null && rs.getString("a0100").length() > 0 ? rs.getString("a0100") : "");
                    hashmap.put("nbase", rs.getString("nbase") != null && rs.getString("nbase").length() > 0 ? rs.getString("nbase") : "");
                    hashmap.put("name", rs.getString("name") != null && rs.getString("name").length() > 0 ? rs.getString("name") : "");
                    hashmap.put("onduty_1", rs.getString("onduty_1") != null && rs.getString("onduty_1").length() > 0 ? rs.getString("onduty_1") : "");
                    hashmap.put("offduty_1", rs.getString("offduty_1") != null && rs.getString("offduty_1").length() > 0 ? rs.getString("offduty_1") : "");
                    hashmap.put("onduty_2", rs.getString("onduty_2") != null && rs.getString("onduty_2").length() > 0 ? rs.getString("onduty_2") : "");
                    hashmap.put("offduty_2", rs.getString("offduty_2") != null && rs.getString("offduty_2").length() > 0 ? rs.getString("offduty_2") : "");
                    hashmap.put("onduty_3", rs.getString("onduty_3") != null && rs.getString("onduty_3").length() > 0 ? rs.getString("onduty_3") : "");
                    hashmap.put("offduty_3", rs.getString("offduty_3") != null && rs.getString("offduty_3").length() > 0 ? rs.getString("offduty_3") : "");
                    hashmap.put("onduty_4", rs.getString("onduty_4") != null && rs.getString("onduty_4").length() > 0 ? rs.getString("onduty_4") : "");
                    hashmap.put("offduty_4", rs.getString("offduty_4") != null && rs.getString("offduty_4").length() > 0 ? rs.getString("offduty_4") : "");
                    this.setA0100((String) hashmap.get("a0100"));
                    this.setNbase((String) hashmap.get("nbase"));
                    list.add(hashmap);
                }
            } else if(this.where_c!=null && this.where_c.toLowerCase().indexOf("a0101")>0) {
                sql = getJoinKqClass(nbase, a_code, day_where.toString());
                rs = dao.search(sql, 0, 31);
                int i = 0;
                while (rs.next()) {
                    if (i >= days) {
                        break;
                    } else {
                        i++;
                    }
                    hashmap = new HashMap();
                    hashmap.put("q03z0", rs.getString("q03z0") != null && rs.getString("q03z0").length() > 0 ? rs.getString("q03z0") : "");
                    hashmap.put("a0100", rs.getString("a0100") != null && rs.getString("a0100").length() > 0 ? rs.getString("a0100") : "");
                    hashmap.put("nbase", rs.getString("nbase") != null && rs.getString("nbase").length() > 0 ? rs.getString("nbase") : "");
                    hashmap.put("name", rs.getString("name") != null && rs.getString("name").length() > 0 ? rs.getString("name") : "");
                    hashmap.put("onduty_1", rs.getString("onduty_1") != null && rs.getString("onduty_1").length() > 0 ? rs.getString("onduty_1") : "");
                    hashmap.put("offduty_1", rs.getString("offduty_1") != null && rs.getString("offduty_1").length() > 0 ? rs.getString("offduty_1") : "");
                    hashmap.put("onduty_2", rs.getString("onduty_2") != null && rs.getString("onduty_2").length() > 0 ? rs.getString("onduty_2") : "");
                    hashmap.put("offduty_2", rs.getString("offduty_2") != null && rs.getString("offduty_2").length() > 0 ? rs.getString("offduty_2") : "");
                    hashmap.put("onduty_3", rs.getString("onduty_3") != null && rs.getString("onduty_3").length() > 0 ? rs.getString("onduty_3") : "");
                    hashmap.put("offduty_3", rs.getString("offduty_3") != null && rs.getString("offduty_3").length() > 0 ? rs.getString("offduty_3") : "");
                    hashmap.put("onduty_4", rs.getString("onduty_4") != null && rs.getString("onduty_4").length() > 0 ? rs.getString("onduty_4") : "");
                    hashmap.put("offduty_4", rs.getString("offduty_4") != null && rs.getString("offduty_4").length() > 0 ? rs.getString("offduty_4") : "");
                    this.setA0100((String) hashmap.get("a0100"));
                    this.setNbase((String) hashmap.get("nbase"));
                    list.add(hashmap);
                }
            } else {
                //kq_org_dept_shift
                sql = getJoinKqOrgDeptClass(a_code, day_where.toString());
                //System.out.println(sql);
                rs = dao.search(sql, 0, 31);
                int i = 0;
                while (rs.next()) {
                    if (i >= days) {
                        break;
                    } else {
                        i++;
                    }
                    hashmap = new HashMap();
                    hashmap.put("q03z0", rs.getString("q03z0") != null && rs.getString("q03z0").length() > 0 ? rs.getString("q03z0") : "");
                    hashmap.put("a0100", rs.getString("org_dept_id") != null && rs.getString("org_dept_id").length() > 0 ? rs.getString("org_dept_id") : "");
                    hashmap.put("nbase", "");
                    hashmap.put("name", rs.getString("name") != null && rs.getString("name").length() > 0 ? rs.getString("name") : "");
                    hashmap.put("onduty_1", rs.getString("onduty_1") != null && rs.getString("onduty_1").length() > 0 ? rs.getString("onduty_1") : "");
                    hashmap.put("offduty_1", rs.getString("offduty_1") != null && rs.getString("offduty_1").length() > 0 ? rs.getString("offduty_1") : "");
                    hashmap.put("onduty_2", rs.getString("onduty_2") != null && rs.getString("onduty_2").length() > 0 ? rs.getString("onduty_2") : "");
                    hashmap.put("offduty_2", rs.getString("offduty_2") != null && rs.getString("offduty_2").length() > 0 ? rs.getString("offduty_2") : "");
                    hashmap.put("onduty_3", rs.getString("onduty_3") != null && rs.getString("onduty_3").length() > 0 ? rs.getString("onduty_3") : "");
                    hashmap.put("offduty_3", rs.getString("offduty_3") != null && rs.getString("offduty_3").length() > 0 ? rs.getString("offduty_3") : "");
                    hashmap.put("onduty_4", rs.getString("onduty_4") != null && rs.getString("onduty_4").length() > 0 ? rs.getString("onduty_4") : "");
                    hashmap.put("offduty_4", rs.getString("offduty_4") != null && rs.getString("offduty_4").length() > 0 ? rs.getString("offduty_4") : "");
                    list.add(hashmap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    public String getCodeItemWhere(String a_code, String nbase) {
        String where = "";
        String org_str = "";
        //判断用户是否是考勤员角色，如果是考勤员角色就走考勤员权限
        String dbname = userView.getDbname();
        String a0100 = userView.getA0100();
        String ischecker = "0";
        ischecker = RegisterInitInfoData.ischecker(dbname, a0100, conn);
        if (!"UN".equals(a_code)) {
            ischecker = "0";
        }
        if (a_code != null && a_code.length() > 0) {
            String codesetid = a_code.substring(0, 2);
            if ("UN".equalsIgnoreCase(codesetid)) {
                org_str = "b0110";
            } else if ("UM".equalsIgnoreCase(codesetid)) {
                org_str = "e0122";
            } else if ("@K".equalsIgnoreCase(codesetid)) {
                org_str = "e01a1";
            } else if ("GP".equalsIgnoreCase(codesetid)) {
                org_str = "a0100";
                if ("GP".equals(a_code)) {
                    where = org_str + " in (select a0100 from kq_group_emp ";
                    if (nbase != null && !"all".equalsIgnoreCase(nbase)) {
                        where = where + " where nbase='" + nbase + "'";
                    }
                    where = where + ")";
                    return where;
                }
            } else if ("EP".equalsIgnoreCase(codesetid)) {
                org_str = "a0100";
            }
            if (a_code.length() >= 3) {
                String codeitemid = a_code.substring(2);
                if (codeitemid != null && codeitemid.length() > 0) {
                    if (!"GP".equalsIgnoreCase(codesetid)) {
                        if ("0".equals(ischecker)) {
                            where = org_str + " like '" + codeitemid + "%'";
                        } else {
                            where = org_str + " like '%'";
                        }
                    } else {

                        if (codeitemid == null || codeitemid.length() <= 0) {
                            String a0100s = getEmploys_Group(codeitemid);
                            if (a0100s == null || a0100s.length() <= 0) {
                                a0100s = "''";
                            }
                            where = org_str + " in (" + a0100s + ")";
                        } else {
                            if (nbase != null && !"all".equalsIgnoreCase(nbase)) {
                                where = org_str + " in (select a0100 from kq_group_emp where nbase='" + nbase + "'  and group_id='" + codeitemid + "')";
                            } else {
                                where = org_str + " in (select a0100 from kq_group_emp where group_id='" + codeitemid + "')";
                            }
                        }

                    }

                }
            }
        }
        return where;
    }

    /**
     * 带机构的
     * @param a_code
     * @param nbase
     * @return
     */
    public String getCodeOrgItemWhere(String a_code) {
        String where = "";
        String codesetid_v = "";
        if (a_code != null && a_code.length() > 0) {
            String codesetid = a_code.substring(0, 2);
            if ("UN".equalsIgnoreCase(codesetid)) {
                codesetid_v = "UN";
            } else if ("UM".equalsIgnoreCase(codesetid)) {
                codesetid_v = "UM";
            } else if ("@K".equalsIgnoreCase(codesetid)) {
                codesetid_v = "@K";
            } else if ("GP".equalsIgnoreCase(codesetid)) {
                codesetid_v = "@G";

            } else if ("EP".equalsIgnoreCase(codesetid)) {
                //org_str="a0100";              
            }
            if (a_code.length() >= 3) {
                String codeitemid = a_code.substring(2);
                if (codeitemid != null && codeitemid.length() > 0) {
                    if ("EP".equalsIgnoreCase(codesetid)) {
                        where = "a0100 = '" + codeitemid + "'";
                    } else {
                        where = "upper(codesetid)='" + codesetid_v.toUpperCase() + "' and org_dept_id='" + codeitemid + "'";
                    }

                }
            } else if ("GP".equals(a_code)) {
                where = "upper(codesetid)='" + codesetid_v.toUpperCase() + "' and org_dept_id=''";//点击所有班组,不显示排班
            }
        }
        return where;
    }

    /**
     * 组合排班表和班次表
     * @param nbase
     * @param a_code
     * @return
     * @throws GeneralException
     */
    private String getJoinKqClass(String nbase, String a_code, String day_where) throws GeneralException {
        String code_where = getCodeItemWhere(a_code, nbase);
        String ltable = KqClassArrayConstant.kq_employ_shift_table;//目标表
        String rtable = KqClassConstant.kq_class_table;//源表      
        String lfield = ltable + "." + KqClassArrayConstant.kq_employ_shift_classid;//源表的过滤条件  
        String rfield = rtable + "." + KqClassConstant.kq_class_id;
        /*SELECT Q03Z0,nbase,A0100,A0101,a.class_id,name,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3
           FROM kq_employ_shift a LEFT JOIN kq_class b 
           ON a.class_id=b.class_id
           WHERE a.Q03Z0>=:From AND a.Q03Z0<=:To*/
        String join_str = Sql_switcher.left_join(ltable, rtable, lfield, rfield);
        StringBuffer sql = new StringBuffer();
        sql.append("select nbase,a0100," + ltable + "." + KqClassArrayConstant.kq_employ_shift_q03z0 + " as q03z0,");
        sql.append(rtable + "." + KqClassConstant.kq_class_name + " as name,");
        sql.append("onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,onduty_4,offduty_4");
        sql.append(" from " + ltable + " " + join_str);
        sql.append(" where 1=1 ");
        if (nbase != null && nbase.length() > 0 && !"all".equalsIgnoreCase(nbase)) {
            sql.append(" and nbase='" + nbase + "'");
        }
        if (day_where != null && day_where.length() > 0) {
            sql.append(" " + day_where);
        }
        
        if (code_where != null && code_where.length() > 0) {
            sql.append(" and " + code_where);
        }
        //31963	完善校验，查询条件|33287由于选择全部人员库与单个人员库时 拼接的SQL略有不同，故校验失败，无法添加where_c条件语句
        if (StringUtils.isNotEmpty(this.where_c) 
                && (this.where_c.indexOf("A0100 IN (")!=-1 || this.where_c.indexOf("EXISTS") != -1)) {
            sql.append(where_c);
        }
        
        if (!this.userView.isSuper_admin() && !self) {
            if (nbase != null && nbase.length() > 0 ) {
                String aNbase = "Usr";
                if (!"all".equalsIgnoreCase(nbase)) {
                    aNbase = nbase;
                }
                
                String whereIN = RegisterInitInfoData.getWhereINSql(userView, aNbase);
                
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    sql.append(" and  EXISTS(select a0100 " + whereIN + " and " + aNbase + "A01.a0100=" + ltable + ".a0100)");
                } else {
                    sql.append(" and  EXISTS(select a0100 " + whereIN + " where " + aNbase + "A01.a0100=" + ltable + ".a0100)");
                }
            }
        }
        sql.append(" order by nbase,a0100," + KqClassArrayConstant.kq_employ_shift_q03z0);
        return sql.toString();
    }

    /**
     * 组合部门班次排班表和班次表
     * @param nbase
     * @param a_code
     * @return
     * @throws GeneralException
     */
    private String getJoinKqOrgDeptClass(String a_code, String day_where) throws GeneralException {
        String code_where = getCodeOrgItemWhere(a_code);
        String ltable = KqClassArrayConstant.kq_org_dept_shift_table;//目标表
        String rtable = KqClassConstant.kq_class_table;//源表      
        String lfield = ltable + "." + KqClassArrayConstant.kq_employ_shift_classid;//源表的过滤条件  
        String rfield = rtable + "." + KqClassConstant.kq_class_id;
        /*SELECT Q03Z0,nbase,A0100,A0101,a.class_id,name,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3
           FROM kq_employ_shift a LEFT JOIN kq_class b 
           ON a.class_id=b.class_id
           WHERE a.Q03Z0>=:From AND a.Q03Z0<=:To*/
        String join_str = Sql_switcher.left_join(ltable, rtable, lfield, rfield);
        StringBuffer sql = new StringBuffer();
        sql.append("select org_dept_id," + ltable + "." + KqClassArrayConstant.kq_employ_shift_q03z0 + " as q03z0,");
        sql.append(rtable + "." + KqClassConstant.kq_class_name + " as name,");
        sql.append("onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,onduty_4,offduty_4");
        sql.append(" from " + ltable + " " + join_str);
        sql.append(" where 1=1 ");
        if (day_where != null && day_where.length() > 0) {
            sql.append(" " + day_where);
        }
        if (code_where != null && code_where.length() > 0) {
            sql.append(" and " + code_where);
        }
        sql.append(" order by " + KqClassArrayConstant.kq_employ_shift_q03z0);
        
        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(conn);
         
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                return sql.toString();
            }
            
            if (a_code == null || a_code.length() <= 2) {
                return sql.toString();
            }
            
            String codesetid = a_code.substring(0, 2);
            String codeitemid = a_code.substring(2);
                
            if ("EP".equalsIgnoreCase(codesetid) || "GP".equalsIgnoreCase(codesetid)) {
                return sql.toString();
            }
            
            String sqlOrg = "select parentid from organization"
                          + " where codesetid = '" + codesetid + "' and codeitemid = '" + codeitemid + "'";
            rs = dao.search(sqlOrg.toString());
            if(!rs.next()) {
                return sql.toString();
            }
            
            String parentid = rs.getString(1);
            sqlOrg = "select codesetid from organization where codeitemid = '" + parentid + "'";
            rs = dao.search(sqlOrg.toString());
            if (rs.next()) 
            {
                String parentCodesetid = rs.getString(1);
                if (!"UN".equalsIgnoreCase(parentCodesetid + parentid) && !((codesetid + codeitemid).equals(parentCodesetid + parentid))) 
                {
                    return getJoinKqOrgDeptClass(parentCodesetid + parentid,day_where);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return sql.toString();
    }

    /**
     * 计算时间
     * @param sb_time
     * @param xb_time
     * @return
     * @throws GeneralException 
     */
    public float getWork_Time(String sb_time, String xb_time) throws GeneralException {
        float work_tiem = 0;
        Date sb_T = DateUtils.getDate(sb_time, "HH:mm");
        Date xb_T = DateUtils.getDate(xb_time, "HH:mm");
        work_tiem = KQRestOper.toHourFormMinute(sb_T, xb_T);
        if (work_tiem < 0) {
            sb_T = DateUtils.getDate("2007.03.08 " + sb_time, "yyyy.MM.dd HH:mm");
            xb_T = DateUtils.getDate("2007.03.09 " + xb_time, "yyyy.MM.dd HH:mm");
            work_tiem = KQRestOper.toHourFormMinute(sb_T, xb_T);
        }
        this.tiems = this.tiems + work_tiem;
        return work_tiem;
    }

    /**
     * 得到一个组的一个员工编号
     * @param parentid
     * @return
     */
    private String getEmploys_Group(String group_id) {
        StringBuffer strsql = new StringBuffer();

        strsql.append("select a0100,nbase ,a0101 from kq_group_emp");
        strsql.append(" where group_id='" + group_id + "'");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rset = null;
        StringBuffer a0100s = new StringBuffer();
        try {
            rset = dao.search(strsql.toString());
            while (rset.next()) {
                a0100s.append("'" + rset.getString("a0100") + "',");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rset);
        }
        if (a0100s != null && a0100s.length() > 0) {
            a0100s.setLength(a0100s.length() - 1);
        }

        return a0100s.toString();
    }

    /**
     * 按纪录现实
     * @param datelist
     * @param a_code
     * @param db_list
     * @param curpage
     * @param pagesize
     * @return
     * @throws GeneralException
     */
    public String returnRecordHtml(ArrayList datelist, String a_code, ArrayList db_list, 
            int curpage, int pagesize, String kqTypeWhr,String  start_date,String name,String selectShowBar) throws GeneralException {
        StringBuffer html = new StringBuffer();
        String width = "10%";
        if(datelist.size()>7) {
            width = "70";
        }
        html.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td>");
        html.append("<div class='fixedDiv common_border_color' style='border:1px solid;'>");

        html.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' style='border-collapse: collapse;word-wrap:break-word'");
        html.append("<thead> <tr> ");
        html.append("<td align='center' class='TableRow' style='border-top:none;border-left:none;' nowrap width='"+width+"'> &nbsp;姓名&nbsp; </td>");
        html.append("<td align='center' class='TableRow' style='border-top:none;border-left:none;word-wrap:break-all;' nowrap width='"+width+"'> &nbsp;单位&nbsp; </td>");
        html.append("<td align='center' class='TableRow' style='border-top:none;border-left:none;word-wrap:break-all;' nowrap width='"+width+"'> &nbsp;部门&nbsp; </td>");
        String aDate = "";
        for (int i = 0; i < datelist.size(); i++) {
            aDate = datelist.get(i).toString();
            html.append("<td align='center' class='TableRow' style='border-top:none;'  width='"+width+"' onClick=\"javascript:tr_onclick(this,'')\" nowrap> &nbsp;");
            html.append(aDate+"<br/>&nbsp;");
            html.append(KqUtilsClass.getWeekName(DateUtils.getDate(aDate, "yyyy.MM.dd")));
            html.append("&nbsp; </td>");
        }
        html.append(" </tr></thead> ");
        html.append(getRecordDateHtml(datelist, a_code, db_list, curpage, pagesize, kqTypeWhr, start_date, name, selectShowBar));

        html.append("</table>");
        return html.toString();
    }

    /**
     * 主体
     * @param datelist
     * @param a_code
     * @param db_list
     * @param curpage
     * @param pagesize
     * @return
     * @throws GeneralException
     */
    private String getRecordDateHtml(ArrayList datelist, String a_code, ArrayList db_list, 
            int curpage, int pagesize, String kqTypeWhr,String  start_date,String name,String selectShowBar) throws GeneralException {
        StringBuffer html = new StringBuffer();

        String fristday = datelist.get(0).toString();
        String end_day = datelist.get(datelist.size() - 1).toString();
        int allrows = getRecordCount(db_list, a_code, fristday, end_day, kqTypeWhr,start_date,name,selectShowBar,datelist.size());
        int sum_page = (allrows - 1) / pagesize + 1;
        curpage = getCurpage(curpage, pagesize, sum_page);
        ArrayList a0100List = getA0100Record(fristday, end_day, a_code, db_list, pagesize, curpage, kqTypeWhr, start_date,name,selectShowBar,datelist.size());
        for (int i = 0; i < a0100List.size(); i++) {
            html.append("<tr>");
            HashMap hash = (HashMap) a0100List.get(i);
            String a0100 = (String) hash.get("a0100");
            String nbase = (String) hash.get("nbase");
            String a0101 = (String) hash.get("a0101");
            String unitName = (String) hash.get("unitName");
            String departName = (String) hash.get("departName");
            html.append(getOneTd(a0101, "sign"));
            html.append(getOneTd(unitName, "sign"));
            html.append(getOneTd(departName, "sign"));
            ArrayList recordlist = getRecord(datelist.size(), fristday, end_day, "EP" + a0100, nbase, start_date, name, selectShowBar);
            String nbase1 = PubFunc.encrypt(nbase);
            String a01001 = PubFunc.encrypt("EP"+a0100);
            for (int j = 0; j < datelist.size(); j++) {
                String day_str = datelist.get(j).toString();
                String tsd_str = getTdStr(recordlist, day_str, "3");
                String onDblClick = "";
                if(userView.hasTheFunction("270700") || userView.hasTheFunction("0C3500"))
                {
                    onDblClick = "onDblClick=\"javascript:editClass('" + nbase1 + "','" + a01001 + "','" + day_str  + "','1')\"";

                }  
                html.append(getOneTd(tsd_str, onDblClick));
            }
            html.append("</tr>");
        }
        html.append("</table></div>");
        html.append("</td></tr><tr><td><table width='100%' border='0' cellspacing='0' cellpadding='0'>");
        if (a0100List != null && a0100List.size() > 0) {
            html.append("<tr>");
            html.append("<td colspan='31' align='left' nowrap class='TableRow' style='border-top:none'>");
            if("1".equals(selectShowBar)) {
                html.append("<select name='curpage' size='1' onchange='javascript:change(5)'>");
            } else {
                html.append("<select name='curpage' size='1' onchange='javascript:change()'>");
            }
            for (int i = 1; i <= sum_page; i++) {
                if (i == curpage) {
                    html.append("<option value='" + i + "' selected='selected'>第" + i + "页</option>");
                } else {
                    html.append("<option value='" + i + "'>第" + i + "页</option>");
                }
            }
            html.append("</select>");
            html.append("</td></tr>");
        }
        html.append("</table></td></tr>");
        return html.toString();
    }

    /**
     * 当前页的纪录
     * @param start_day
     * @param end_day
     * @param a_code
     * @param db_list
     * @param pagesize
     * @param curpage
     * @return
     * @throws GeneralException
     */
    private ArrayList getA0100Record(String start_day, String end_day, String a_code, ArrayList db_list, 
            int pagesize, int curpage, String kqTypeWhr ,String  start_date,String name,String selectShowBar, int dateSize) throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer day_where = new StringBuffer();
        day_where.append(" and " + KqClassArrayConstant.kq_employ_shift_q03z0 + ">='" + start_day + "'");
        day_where.append(" and " + KqClassArrayConstant.kq_employ_shift_q03z0 + "<='" + end_day + "'");
        if(StringUtils.isNotEmpty(start_date)&&"1".equals(selectShowBar)&& StringUtils.isNotBlank(name) &&dateSize==7){
            start_date = start_date.replace("-",".");
            day_where.append(" and a0100 in (select a0100 from kq_employ_shift LEFT JOIN kq_class ON kq_employ_shift.class_id=kq_class.class_id ");
            day_where.append("where q03z0='"+start_date+"' and ");
            day_where.append( "name in ( "+name+" ))");
        }
        String ltable = KqClassArrayConstant.kq_employ_shift_table;//目标表          
        StringBuffer sql = new StringBuffer();
        //29614 这里this.where_c是传的需要查询的值
    	KqParameter para = new KqParameter(this.userView, "", this.conn);
        HashMap map = para.getKqParamterMap();
        String gnoField = (String) map.get("g_no");//工号指标
        String cardnoField= (String) map.get("cardno");//卡号指标
        StringBuffer selWhr = new StringBuffer();
        selWhr.append(" where ");
    	selWhr.append(" A0101 like '%" + this.where_c + "%'");//按姓名查询
    	selWhr.append(" or " + gnoField + " like '%" + this.where_c + "%'");//按工号查询
    	selWhr.append(" or " + cardnoField + " like '%" + this.where_c + "%'");//按考勤卡号查询
        
        for (int i = 0; i < db_list.size(); i++) {
            String nbase = (String) db_list.get(i);
            String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
            if (!whereA0100In.toUpperCase().contains(" WHERE ")) {
                whereA0100In = whereA0100In + " WHERE 1=1 ";
            }
            
            String code_where = getCodeItemWhere(a_code, (String) db_list.get(i));
            //linbz 26547 记录排序
            sql.append("select b.a0000,b.a0100,b.a0101,'" + nbase + "' nbase,r1.codeitemdesc unitName,r2.codeitemdesc departName from (");
            
//            sql.append("select distinct a0100,a0101,'" + nbase + "' nbase,r1.codeitemdesc unitName,r2.codeitemdesc departName from " + ltable 
//                    + "  left join organization r1 on r1.codeitemid = B0110 left join organization r2 on r2.codeitemid = E0122");
            
            sql.append("select distinct a0100 ");
            sql.append(" from ").append(ltable);
            
            sql.append(" where nbase='" + nbase + "'");
            if (day_where != null && day_where.length() > 0) {
                sql.append(" " + day_where);
            }
            
            if (code_where != null && code_where.length() > 0) {
                sql.append(" and " + code_where);
            }
            
            sql.append(" and   a0100 in (select a0100 " + whereA0100In + kqTypeWhr + ") ");
            //29614  记录这里this.where_c是传的需要查询的值
            StringBuffer whereC = new StringBuffer();
            if (StringUtils.isNotEmpty(this.where_c)){
            	whereC.append(" and (");
        		whereC.append(" A0100 IN (select A0100 from " + nbase + "A01");
        		whereC.append(selWhr.toString());
        		whereC.append(")");
        		whereC.append(")");
        		sql.append(whereC.toString());
            }
            	
            sql.append(") a ");
            
            sql.append(" right join ");
            sql.append(" (select a0100,a0000,a0101,b0110,e0122 " + whereA0100In + kqTypeWhr);
            if (code_where != null && code_where.length() > 0) {
                sql.append(" and " + code_where);
            }
            //29614
            if (StringUtils.isNotEmpty(this.where_c)){
            	sql.append(whereC.toString());
            }
            sql.append(" ) b ");
            sql.append(" on a.a0100=b.a0100 ");
            sql.append(" left join organization r1 on r1.codeitemid = b.B0110 ");
            sql.append(" left join organization r2 on r2.codeitemid = b.E0122 ");
            sql.append(" where  a.a0100 is not null ");
            
            sql.append(" union ");
        }
        if (sql != null && sql.toString().length() > 0) {
            sql.setLength(sql.length() - 7);
        }
        
        
        StringBuffer sqlAll = new StringBuffer();
        sqlAll.append(" select * from ( ");
        sqlAll.append(sql.toString());
        sqlAll.append(" ) x ");
        sqlAll.append(" order by nbase,a0000,a0100 ");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        HashMap hashmap = null;
        ArrayList keylist = new ArrayList();
        //keylist.add("q03z0");
        keylist.add("a0100");
        keylist.add("nbase");
        try {
            rs = dao.search(sqlAll.toString(), pagesize, curpage);
            while (rs.next()) {
                hashmap = new HashMap();

                hashmap.put("a0100", rs.getString("a0100") != null && rs.getString("a0100").length() > 0 ? rs.getString("a0100") : "");
                hashmap.put("a0101", rs.getString("a0101") != null && rs.getString("a0101").length() > 0 ? rs.getString("a0101") : "");
                hashmap.put("nbase", rs.getString("nbase") != null && rs.getString("nbase").length() > 0 ? rs.getString("nbase") : "");
                hashmap.put("unitName", rs.getString("unitName") != null && rs.getString("unitName").length() > 0 ? rs.getString("unitName") : "");
                hashmap.put("departName", rs.getString("departName") != null && rs.getString("departName").length() > 0 ? rs.getString("departName") : "");
                list.add(hashmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    /**
     * 总数
     * @param db_list
     * @param a_code
     * @param start_day
     * @param end_day
     * @return
     * @throws GeneralException
     */
    private int getRecordCount(ArrayList db_list, String a_code, String start_day, String end_day, String kqTypeWhr,String start_date,String name,String selectShowBar,int dateSize) throws GeneralException {
        int countI = 0;
        ContentDAO dao = new ContentDAO(this.conn);
        String count = "";
        RowSet rowSet = null;
        try {
        	//29614 记录这里this.where_c是传的需要查询的值
        	KqParameter para = new KqParameter(this.userView, "", this.conn);
            HashMap map = para.getKqParamterMap();
            String gnoField = (String) map.get("g_no");//工号指标
            String cardnoField= (String) map.get("cardno");//卡号指标
            StringBuffer selWhr = new StringBuffer();
            selWhr.append(" where ");
        	selWhr.append(" A0101 like '%" + this.where_c + "%'");//按姓名查询
        	selWhr.append(" or " + gnoField + " like '%" + this.where_c + "%'");//按工号查询
        	selWhr.append(" or " + cardnoField + " like '%" + this.where_c + "%'");//按考勤卡号查询
        	
            for (int i = 0; i < db_list.size(); i++) {
                String dbase = db_list.get(i).toString();
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase);
                if (!whereA0100In.toUpperCase().contains(" WHERE ")) {
                    whereA0100In = whereA0100In + " WHERE 1=1 ";
                }
                    
                String code_where = getCodeItemWhere(a_code, dbase);
                StringBuffer sqlstr = new StringBuffer();
                sqlstr.append("select count(a0100) a from ( ");
                sqlstr.append("select a0100,a0101,'" + dbase + "' nbase from " + KqClassArrayConstant.kq_employ_shift_table);
                sqlstr.append(" where nbase='" + dbase + "' ");
                sqlstr.append(" and " + KqClassArrayConstant.kq_employ_shift_q03z0 + ">='" + start_day + "'");
                sqlstr.append(" and " + KqClassArrayConstant.kq_employ_shift_q03z0 + "<='" + end_day + "'");
                if(StringUtils.isNotEmpty(start_date)&&"1".equals(selectShowBar)&& StringUtils.isNotBlank(name) &&dateSize==7){
                    start_date = start_date.replace("-",".");
                    sqlstr.append(" and a0100 in (select a0100 from kq_employ_shift LEFT JOIN kq_class ON kq_employ_shift.class_id=kq_class.class_id ");
                    sqlstr.append("where q03z0='"+start_date+"' and ");
                    sqlstr.append( "name in ( "+name+" ))");
                }
                if (code_where != null && code_where.length() > 0) {
                    sqlstr.append(" and " + code_where);
                }
                //29614 
                if (StringUtils.isNotEmpty(this.where_c)){
                	sqlstr.append(" and (");
                	sqlstr.append(" A0100 IN (select A0100 from " + dbase + "A01");
                	sqlstr.append(selWhr.toString());
                	sqlstr.append(")");
                	sqlstr.append(")");
                }
                sqlstr.append(" and  a0100 in(select a0100 " + whereA0100In + kqTypeWhr + ") ");
                sqlstr.append(" group by a0100,a0101");
                sqlstr.append(") aaaa");
                //System.out.println(sqlstr.toString());
                rowSet = dao.search(sqlstr.toString());
                if (rowSet.next()) {
                    count = rowSet.getString("a");
                    countI = Integer.parseInt(count) + countI;
                }
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return countI;
    }

    /**
     * 判断页码
     * 
     * */
    public int getCurpage(int curpage, int pagesize, int sum_page) {
        if (curpage <= 0) {
            curpage = 1;
        } else if (curpage > sum_page) {
            curpage = sum_page;
        }
        return curpage;
    }

    /**
     * 取得排班界面输入查询sql条件
     * @param select_flag
     * @param name 姓名或工号或卡号
     * @param nbase
     * @return
     * @throws GeneralException
     */
    public String getSelWhere(String select_flag, String name, String nbase) throws GeneralException {
        StringBuffer where_c = new StringBuffer();
        if (name == null || name.length() <= 0 || "null".equals(name)) {
            return "";
        }
        
        if (select_flag != null && "1".equals(select_flag)) {
            name = PubFunc.getStr(name);
            
        	//获取工号、考勤卡号指标方法：
        	KqParameter para = new KqParameter(this.userView, "", this.conn);
            HashMap hashmap = para.getKqParamterMap();
            //工号指标
            String gnoField = (String) hashmap.get("g_no");
            //卡号指标
            String cardnoField= (String) hashmap.get("cardno");
            
        	StringBuffer selWhr = new StringBuffer();
        	selWhr.append(" where ");
        	//按姓名查询
        	selWhr.append(" (A0101 like '%" + name + "%'");
        	//按工号查询
        	selWhr.append(" or " + gnoField + " like '%" + name + "%'");
        	//按考勤卡号查询
        	selWhr.append(" or " + cardnoField + " like '%" + name + "%')");
        	
            if(nbase==null || "all".equalsIgnoreCase(nbase) || "".equalsIgnoreCase(nbase)){
            	if(this.db_list == null || this.db_list.size()==0) {
            		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
            		this.db_list = kqUtilsClass.getKqPreList();
            	}
            	
            	if(this.db_list == null || this.db_list.size()==0) {
                    return "";
                }
            	
            	where_c.append(" and (");
            	for (int i = 0; i < this.db_list.size(); i++) {
            		nbase = (String)db_list.get(i);
            		if(i>0) {
                        where_c.append(" OR ");
                    }
            		where_c.append(" EXISTS(SELECT 1 FROM ").append(nbase).append("A01");
            		where_c.append(selWhr.toString());
            		where_c.append(" and ").append(nbase).append("A01.a0100=kq_employ_shift.a0100");
            		where_c.append(" and kq_employ_shift.nbase='").append(nbase).append("'");
            		where_c.append(")");
            	}
            	where_c.append(")");
           }else{
               where_c.append(" and EXISTS(SELECT 1 FROM ").append(nbase).append("A01");
               where_c.append(selWhr.toString());
               where_c.append(" and ").append(nbase).append("A01.a0100=kq_employ_shift.a0100");
               where_c.append(" and kq_employ_shift.nbase='").append(nbase).append("'");
           	   where_c.append(")");
           }
        }
        
        return where_c.toString();
    }
    /**
     * 通过快速查询条件获取人员编号和库前缀
     * @param selectName	条件
     * @param nbaseList		库集合
     * @param code
     * @param kind
     * @param kqTypeWhr		考勤类型条件
     * @return
     * @throws GeneralException
     */
    public ArrayList getUserRecord(String selectName, ArrayList nbaseList, String code
    		, String kind, String kqTypeWhr)  throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        String where = "";
        
        //获取工号、考勤卡号指标方法：
    	KqParameter para = new KqParameter(this.userView, "", this.conn);
        HashMap hashmap = para.getKqParamterMap();
        //工号指标
        String gnoField = (String) hashmap.get("g_no");
        //卡号指标
        String cardnoField= (String) hashmap.get("cardno");

        if ("2".equals(kind)) {
            where = " b0110 like '" + code + "%'";
        } else if ("1".equals(kind)) {
            where = " e0122 like '" + code + "%'";
        } else {
            where = " e01a1 like '" + code + "%'";
        }

        for (Iterator it = nbaseList.iterator(); it.hasNext();) {
            String nbase = (String) it.next();
            if (sql.length() > 0) {
                sql.append(" UNION ALL ");
            }
            sql.append("SELECT A0100,'").append(nbase).append("' nbase,A0101,").append(gnoField).append(",").append(cardnoField).append(" FROM ").append(nbase).append("A01");
            sql.append(" WHERE (A0101 LIKE '%").append(selectName).append("%'");//按姓名查询
            sql.append(" or ").append(gnoField).append(" LIKE '%").append(selectName).append("%'");//按工号查询
            sql.append(" or ").append(cardnoField).append(" LIKE '%").append(selectName).append("%') ");//按考勤卡号查询
            sql.append(kqTypeWhr);
            sql.append(" AND ").append(where);
            sql.append(" AND A0100 IN (SELECT A0100 ").append(RegisterInitInfoData.getWhereINSql(userView, nbase)).append(")");
//            sql.append(" order by a0000,b0110,e0122,e01a1 ");
        }
        RowSet rs = null;
        try {
        	ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String a0100 = rs.getString("a0100");
                String nbase = rs.getString("nbase");
                String a0101 = rs.getString("a0101");
                String gnoFieldV = rs.getString(gnoField);
                String cardnoFieldV = rs.getString(cardnoField);
                //29316 查询时优先查与该值相等的条件
                if(StringUtils.isNotEmpty(selectName) && (selectName.equalsIgnoreCase(a0101) || selectName.equalsIgnoreCase(gnoFieldV) || selectName.equalsIgnoreCase(cardnoFieldV))){
                	list.add(0, a0100 + "'" + nbase);
                	break;
                }
                list.add(a0100 + "'" + nbase);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return list;
    }
    
    public ArrayList getDb_list() {
        return db_list;
    }

    public void setDb_list(ArrayList db_list) {
        this.db_list = db_list;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public String getA0100() {
        return a0100;
    }

    public void setA0100(String a0100) {
        this.a0100 = a0100;
    }

    public String getNbase() {
        return nbase;
    }

    public void setNbase(String nbase) {
        this.nbase = nbase;
    }
}
