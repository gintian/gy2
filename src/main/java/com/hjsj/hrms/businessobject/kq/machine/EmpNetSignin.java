package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author Owner
 * 
 */
public class EmpNetSignin {
    private Connection conn;
    private UserView   userView;
    private String     signmess   = "";
    private String     errorflag  = "";
    private String     errormess1 = "";
    private String     errormess2 = "";
    private String     errormess3 = "";
    private String     errormess4 = "";
    private String 	   errormess5 = "";

    public EmpNetSignin() {

    }

    public EmpNetSignin(Connection conn) {
        this.conn = conn;
    }

    public EmpNetSignin(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }

    public String getSignmess() {
        return this.signmess;
    }

    /**
     * 得到日期yyyy.MM.dd
     * 
     * @return
     */
    public String getWork_date() {
        return PubFunc.getStringDate("yyyy.MM.dd");
    }

    /**
     * 得到时间
     * 
     * @return HH:mm
     */
    public String getWork_tiem() {
        return PubFunc.getStringDate("HH:mm");
    }

    public boolean empNetSingin(String a0100, String nbase, String workdate, String worktime, String sp_flag, String singin_flag) throws GeneralException {
        boolean isCorrect = false;
        ContentDAO dao = new ContentDAO(this.conn);
        LazyDynaBean bean = getEmpBean(dao, a0100, nbase, workdate);
        String name = (String) bean.get("a0101");
        String mess = "";
        if ("0".equals(singin_flag)) {
            mess = "签到";
        } else {
            mess = "签退";
        }
        /*
         * if(class_id==null||class_id.length()<=0) throw
         * GeneralExceptionHandler.Handle(new
         * GeneralException(name+",没有排班不可"+mess+"！")); if(class_id.equals("0"))
         * throw GeneralExceptionHandler.Handle(new
         * GeneralException(name+",休息班次不可"+mess+"！"));
         */
        NetSignIn netSignIn = new NetSignIn(this.userView, this.conn);
        String cardno = netSignIn.getKqCard(nbase, a0100);
        if (cardno == null || cardno.length() <= 0) {
            throw GeneralExceptionHandler.Handle(new GeneralException("", name + "，没有分配考勤卡号，不能网上考勤！"));
        }
        ArrayList classList = netSignIn.getClassID(nbase, a0100, this.userView.getUserOrgId(), this.userView.getUserDeptId(), this.userView.getUserPosId(), workdate);
	    if (classList == null || classList.size() <= 0) 
	    {
		    throw GeneralExceptionHandler.Handle(new GeneralException(name + "，" + ResourceFactory.getProperty("kq.netsign.error.notarrange.in")));
	    }
        if (!netSignIn.IsExists(nbase, a0100, workdate, worktime)) {

            throw GeneralExceptionHandler.Handle(new GeneralException(name + "，规定时间间隔内不能签多次！"));

        } else if (!netSignIn.ifNetSign(nbase, a0100, workdate, worktime)) {
            isCorrect = false;
            throw GeneralExceptionHandler.Handle(new GeneralException(name + "不可以在请假时间范围内签到签退！"));

        } else if (!netSignIn.signInScope(nbase, a0100, (String) bean.get("b0110"), (String) bean.get("e0122"), (String) bean.get("e01a1"), workdate, worktime, singin_flag)) {
            throw GeneralExceptionHandler.Handle(new GeneralException(name + "，有效刷卡时间范围外，" + mess + "无效！"));

        } else {
            String class_id = netSignIn.getClass_id();
            String remess = netSignIn.signInCount(class_id, workdate, worktime, singin_flag);
            // isCorrect=onNetSign(nbase,a0100,cardno,bean,"",null,workdate,worktime,mess,sp_flag,"");
            // 增加一个参数 1 为正常，2 为补刷卡
            isCorrect = onNetSign(nbase, a0100, cardno, bean, "", null, workdate, worktime, mess, sp_flag, "", "1");
            if (isCorrect) {
                if (remess != null && remess.length() > 0) {
                    this.signmess = "" + mess + "成功," + remess + "!";
                } else {
                    this.signmess = "" + mess + "成功!";
                }
            } else {
                this.signmess = "" + mess + "失败!";
            }
        }
        return isCorrect;
    }

    /**
     * 得到考勤班次
     * 
     * @param dao
     * @param a0100
     * @param nbase
     * @param workdate
     * @return
     */
    public LazyDynaBean getEmpBean(ContentDAO dao, String a0100, String nbase, String workdate) {
        StringBuffer sql = new StringBuffer();
        sql.append("select b0110,e0122,e01a1,a0101 from " + nbase + "A01 where ");
        sql.append(" a0100='" + a0100 + "'");
        RowSet rs = null;
        LazyDynaBean bean = new LazyDynaBean();
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                bean.set("b0110", rs.getString("b0110") != null && rs.getString("b0110").length() > 0 ? rs.getString("b0110") : "");
                bean.set("e0122", rs.getString("e0122") != null && rs.getString("e0122").length() > 0 ? rs.getString("e0122") : "");
                bean.set("e01a1", rs.getString("e01a1") != null && rs.getString("e01a1").length() > 0 ? rs.getString("e01a1") : "");
                bean.set("a0101", rs.getString("a0101") != null && rs.getString("a0101").length() > 0 ? rs.getString("a0101") : "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return bean;
    }

    /**
     * 得到某班次的刷卡时间
     * 
     * @param class_id
     * @return
     */
    private HashMap getDutyTime(String class_id) {
        StringBuffer sql = new StringBuffer();
        sql.append("select " + kqClassShiftColumns());
        sql.append(" from kq_class where class_id='" + class_id + "'");
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap map = new HashMap();
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                for (int i = 0; i < 4; i++) {
                    String onduty_card = rs.getString("onduty_card_" + (i + 1));
                    String onduty = rs.getString("onduty_" + (i + 1));
                    String offduty_card = rs.getString("offduty_card_" + (i + 1));
                    String offduty = rs.getString("offduty_" + (i + 1));
                    map.put("onduty_card_" + (i + 1), onduty_card);
                    map.put("onduty_" + (i + 1), onduty);
                    map.put("offduty_card_" + (i + 1), offduty_card);
                    map.put("offduty_" + (i + 1), offduty);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return map;
    }

    private String kqClassShiftColumns() {
        StringBuffer columns = new StringBuffer();
        columns.append("class_id,onduty_card_1,offduty_card_1,onduty_card_2,offduty_card_2,");
        columns.append("onduty_card_3,offduty_card_3,onduty_card_4,offduty_card_4,");
        columns.append("onduty_start_1,onduty_1,onduty_flextime_1,be_late_for_1,absent_work_1,onduty_end_1,");
        columns.append("rest_start_1,rest_end_1,offduty_start_1,leave_early_absent_1,leave_early_1,");
        columns.append("offduty_1,offduty_flextime_1,offduty_end_1,");
        // 2
        columns.append("onduty_start_2,onduty_2,onduty_flextime_2,be_late_for_2,absent_work_2,onduty_end_2,");
        columns.append("rest_start_2,rest_end_2,offduty_start_2,leave_early_absent_2,leave_early_2,");
        columns.append("offduty_2,offduty_flextime_2,offduty_end_2,");
        // 3
        columns.append("onduty_start_3,onduty_3,onduty_flextime_3,be_late_for_3,absent_work_3,onduty_end_3,");
        columns.append("rest_start_3,rest_end_3,offduty_start_3,leave_early_absent_3,leave_early_3,");
        columns.append("offduty_3,offduty_flextime_3,offduty_end_3,");
        // 4
        columns.append("onduty_start_4,onduty_4,onduty_flextime_4,be_late_for_4,absent_work_4,onduty_end_4,");
        columns.append("rest_start_4,rest_end_4,offduty_start_4,leave_early_absent_4,leave_early_4,");
        columns.append("offduty_4,offduty_flextime_4,offduty_end_4,");
        // other
        columns.append("night_shift_start,night_shift_end,zeroflag,domain_count,work_hours,zero_absent,one_absent");
        return columns.toString();
    }

    public boolean onNetSign(String nbase, String a0100, String cardno, LazyDynaBean bean, String oper_cause, Date oper_time, String work_date, String work_tiem, String location, String sp_flag, String ip_addr, String datafromp) throws GeneralException {
        boolean isCorrect = true;
        StringBuffer sql = new StringBuffer();
        sql.append("insert into kq_originality_data(a0100,nbase,card_no,work_date,work_time,a0101,b0110,e0122,e01a1,location");
        sql.append(",inout_flag,oper_cause,oper_user,oper_time,oper_mach,sp_flag,datafrom)");
        sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        ArrayList list = new ArrayList();
        list.add(a0100);
        list.add(nbase);
        list.add(cardno);
        list.add(work_date);
        list.add(work_tiem);
        list.add(bean.get("a0101"));
        list.add(bean.get("b0110"));
        list.add(bean.get("e0122"));
        list.add(bean.get("e01a1"));
        list.add(location);
        list.add("0");
        list.add(oper_cause);
        list.add(this.userView.getUserFullName());
        if (oper_time == null) {
            list.add(null);
        } else {
            list.add(DateUtils.getTimestamp(DateUtils.format(oper_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
        }
        list.add(ip_addr);
        list.add(sp_flag);
        // list.add("1"); 网上签到点击签到 kq_originality_data 刷卡数据表 中 datafrom
        // 应该为0；1表示补刷卡数据
        if ("2".equalsIgnoreCase(datafromp)) {
            // 补签
            list.add("1");
        } else {
            // 正常
            list.add("0");
        }
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            dao.insert(sql.toString(), list);
        } catch (Exception e) {
            isCorrect = false;
            e.printStackTrace();
        }
        return isCorrect;
    }

    public ArrayList onNetSignList(String nbase, String a0100, String cardno, LazyDynaBean bean, String oper_cause, Date oper_time, String work_date, String work_tiem, String location, String sp_flag, String ip_addr) throws GeneralException {

        ArrayList list = new ArrayList();
        list.add(a0100);
        list.add(nbase);
        list.add(cardno);
        list.add(work_date);
        list.add(work_tiem);
        list.add(bean.get("a0101"));
        list.add(bean.get("b0110"));
        list.add(bean.get("e0122"));
        list.add(bean.get("e01a1"));
        list.add(location);
        list.add("0");
        list.add(oper_cause);
        list.add(this.userView.getUserFullName());
        if (oper_time == null) {
            list.add(null);
        } else {
            list.add(DateUtils.getTimestamp(DateUtils.format(oper_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
        }
        list.add(ip_addr);
        list.add(sp_flag);
        // list.add("1"); //1就是补签
        list.add("0"); // 改为：这批量签到不是补签
        return list;
    }

    /*************** 批量签到 ****************/

    /**
     * 批量签到
     * 
     * @param a0100list
     * @param nbase
     * @param workdate
     * @param sp_flag
     * @return
     * @throws GeneralException
     */
    public boolean bacthEmpNetSingin(ArrayList a0100list, String nbase, String workdate, String work_time, String sp_flag, String singin_flag) throws GeneralException {
        boolean isCorrect = true;
        ContentDAO dao = new ContentDAO(this.conn);
        String a0100 = null;
        String nbased = null;
        ArrayList datalist = new ArrayList();
        
        for (int i = 0; i < a0100list.size(); i++) {
            ArrayList listd = (ArrayList) a0100list.get(i);
            for (int k = 0; k < 1; k++) {
                a0100 = (String) listd.get(0);
                a0100 = a0100.trim(); // 取消空格
                if (a0100 == null || "".equals(a0100)) {
                    continue;
                }
                
                nbased = (String) listd.get(1);
                nbased = nbased.trim();
                if (nbased == null || "".equals(nbased)) {
                    continue;
                }
                
                oneEmpNetSinginList(a0100, nbased, workdate, work_time, sp_flag, datalist, singin_flag);
            }
        }
        
        StringBuffer sql = new StringBuffer();
        sql.append("insert into kq_originality_data(a0100,nbase,card_no,work_date,work_time,a0101,b0110,e0122,e01a1,location");
        sql.append(",inout_flag,oper_cause,oper_user,oper_time,oper_mach,sp_flag,datafrom)");
        sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        try {
            dao.batchInsert(sql.toString(), datalist);

            if (this.errorflag != null && this.errorflag.length() > 0) {
                this.signmess = "以下人员操作失败：\\n\\n";
                
                if (this.errormess1 != null && this.errormess1.length() > 0) {
                    this.signmess = this.signmess + "没有卡号：" + this.errormess1.substring(0, this.errormess1.length() - 1) + "！\\n\\n";
                }
                
                if (this.errormess2 != null && this.errormess2.length() > 0) {
                    this.signmess = this.signmess + "规定时间间隔内不能签多次：" + this.errormess2.substring(0, this.errormess2.length() - 1) + "！\\n\\n";
                }

                if (this.errormess3 != null && this.errormess3.length() > 0) {
                    this.signmess = this.signmess + "有效刷卡时间范围外操作失败：" + this.errormess3.substring(0, this.errormess3.length() - 1) + "！\\n\\n";
                }

                if (this.errormess4 != null && this.errormess4.length() > 0) {
                    this.signmess = this.signmess + "不可以在请假时间范围内签到签退：" + this.errormess4.substring(0, this.errormess4.length() - 1) + "！\\n";
                }
                
                if (errormess5 != null && errormess5.length() > 0) {
                    signmess += "没有排班：" + errormess5.substring(0, errormess5.length() - 1) + "！\\n\\n";
                }
            } else {
                this.signmess = "操作成功！";
            }
        } catch (Exception e) {
            isCorrect = false;
            e.printStackTrace();
            this.signmess = "操作失败！";
        }

        return isCorrect;
    }

    private ArrayList oneEmpNetSinginList(String a0100, String nbase, String workdate, String work_time, String sp_flag, ArrayList list, String singin_flag) throws GeneralException {

        ContentDAO dao = new ContentDAO(this.conn);
        LazyDynaBean bean = getEmpBean(dao, a0100, nbase, workdate);

        String name = (String) bean.get("a0101");
        String sp_mess = "";
        if ("0".equals(singin_flag)) {
            sp_mess = "签到";
        } else {
            sp_mess = "签退";
        }

        NetSignIn netSignIn = new NetSignIn(this.userView, this.conn);

        String cardno = netSignIn.getKqCard(nbase, a0100);
        if (cardno == null || cardno.length() <= 0) {
            this.errorflag = "1";// 卡号为空
            this.errormess1 = this.errormess1 + (name + "，");
        }
        ArrayList classList = netSignIn.getClassID(nbase, a0100, (String) bean.get("b0110"), (String) bean.get("e0122"), (String) bean.get("e01a1"), workdate);
        if (classList == null || classList.size() <= 0) 
        {
        	errorflag = "5";
        	errormess5 += name + "，";
        }else if (!netSignIn.IsExists(nbase, a0100, workdate, work_time)) {
            this.errorflag = "2";// 规定时间间隔内不能签多次
            this.errormess2 = this.errormess2 + (name + "，");
        } else if (!netSignIn.signInScope(nbase, a0100, (String) bean.get("b0110"), (String) bean.get("e0122"), (String) bean.get("e01a1"), workdate, work_time, singin_flag)) {
            this.errorflag = "3";// 有效刷卡时间范围外操作失败
            this.errormess3 = this.errormess3 + (name + "，");
        } else if (!netSignIn.ifNetSign(nbase, a0100, workdate, work_time)) {
            this.errorflag = "4";// 不可以在请假时间范围内签到签退！
            this.errormess4 = this.errormess4 + (name + "，");
        } else {
            ArrayList onlist = onNetSignList(nbase, a0100, cardno, bean, "", null, workdate, work_time, sp_mess, sp_flag, "");
            list.add(onlist);
        }

        return list;
    }

    public boolean oneLoadNetSigninTrans(String a0100, String nbase, String workdate, String singin_flag, String sdao_count_field) throws GeneralException {
        boolean isCorrect = true;
        String sql = "select 1 from q03 where a0100='" + a0100 + "' and nbase='" + nbase + "' and q03z0='" + workdate + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        LazyDynaBean bean = getEmpBean(dao, a0100, nbase, workdate);
        String name = (String) bean.get("a0101");
        String sp_mess = "";
        String sdao = "";
        if ("0".equals(singin_flag)) {
            sp_mess = "上岛签到";
            sdao = "1";
        } else {
            sp_mess = "上岛签退";
            sdao = "0";
        }
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (!rs.next()) {
                isCorrect = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        if (!isCorrect) {
            throw GeneralExceptionHandler.Handle(new GeneralException(name + "，没有生成日明细数据，" + sp_mess + "无效！"));
        }
        sql = "update q03 set " + sdao_count_field + "='" + sdao + "' where a0100='" + a0100 + "' and nbase='" + nbase + "' and q03z0='" + workdate + "'";
        try {
            dao.update(sql);

        } catch (SQLException e) {
            isCorrect = false;
            e.printStackTrace();
        }
        return isCorrect;
    }

    public boolean batchLoadNetSignin(ArrayList a0100list, String nbase, String workdate, String singin_flag, String sdao_count_field) throws GeneralException {
        boolean isCorrect = true;
        ContentDAO dao = new ContentDAO(this.conn);
        String sp_mess = "";
        String sdao = "";
        RowSet rs = null;
        if ("0".equals(singin_flag)) {
            sp_mess = "上岛签到";
            sdao = "1";
        } else {
            sp_mess = "上岛签退";
            sdao = "0";
        }
        ArrayList list = new ArrayList();
        String a0100 = "";
        String nbased = "";
        for (int i = 0; i < a0100list.size(); i++) {
            ArrayList listd = (ArrayList) a0100list.get(i);
            for (int k = 0; k < 1; k++) {
                a0100 = (String) listd.get(0);
                a0100 = a0100.trim(); // 取消空格
                nbased = (String) listd.get(1);
                nbased = nbased.trim();
                if (a0100 == null || "".equals(a0100)) {
                    continue;
                }
                if (nbased == null || "".equals(nbased)) {
                    continue;
                }
                // 根据首钢更改 只有员工休息的才能签上岛签到 classid=0 是休息 批量上岛签到
                StringBuffer sql1 = new StringBuffer();
                String classid = "";
                sql1.append("select class_id from kq_employ_shift where ");
                sql1.append(" a0100='" + a0100 + "' and nbase='" + nbased + "' and q03z0='" + workdate + "'");
                try {
                    rs = dao.search(sql1.toString());
                    if (rs.next()) {
                        classid = rs.getString("class_id");
                    }
                    if ("0".equalsIgnoreCase(classid) || "".equals(classid)) {
                        ArrayList onlist = loadSigninEmp(dao, a0100, nbased, workdate);
                        if (onlist != null && onlist.size() > 0) {
                            list.add(onlist);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // ArrayList onlist=loadSigninEmp(dao,a0100,nbased,workdate);
                // if(onlist!=null&&onlist.size()>0)
                // list.add(onlist);
            }
            // a0100=(String)a0100list.get(i);
            // a0100= a0100.trim(); //取消空格
            // if(a0100==null||a0100.equals(""))
            // continue;
            // ArrayList onlist=loadSigninEmp(dao,a0100,nbase,workdate);
            // if(onlist!=null&&onlist.size()>0)
            // list.add(onlist);
        }
        // String
        // sql="update q03 set "+sdao_count_field+"='"+sdao+"' where a0100=? and nbase='"+nbase+"' and q03z0='"+workdate+"'";
        String sql = "update q03 set " + sdao_count_field + "='" + sdao + "' where a0100=? and nbase=? and q03z0='" + workdate + "'";
        if (list == null || list.size() <= 0) {
            isCorrect = false;
        }
        try {
            dao.batchUpdate(sql, list);

        } catch (SQLException e) {
            isCorrect = false;
            e.printStackTrace();
        }
        return isCorrect;
    }

    private ArrayList loadSigninEmp(ContentDAO dao, String a0100, String nbase, String workdate) {
        ArrayList list = new ArrayList();
        String sql = "select 1 from q03 where a0100='" + a0100 + "' and nbase='" + nbase + "' and q03z0='" + workdate + "'";
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                list.add(a0100);
                list.add(nbase);
            } else {
                LazyDynaBean bean = getEmpBean(dao, a0100, nbase, workdate);
                String name = (String) bean.get("a0101");
                this.signmess = this.signmess + "" + name + ",";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    /**
     * 得到考勤班次
     * 
     * @return
     * @throws GeneralException
     */
    public ArrayList getClassList() throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select name,class_id");
        sql.append(" from kq_class");
        sql.append(" order by displayorder");
        ContentDAO dao = new ContentDAO(this.conn);
        CommonData vo = null;
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                vo = new CommonData();
                vo.setDataName(rs.getString("name"));
                vo.setDataValue(rs.getString("class_id"));
                list.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);

        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    public ArrayList getSigninList(String sdao_count_field) {
        ArrayList list = new ArrayList();
        CommonData vo = null;
        vo = new CommonData("oned", "已签到");
        list.add(vo);
        vo = new CommonData("unon", "未签到");
        list.add(vo);
        vo = new CommonData("offed", "已签退");
        list.add(vo);
        vo = new CommonData("unoff", "未签退");
        list.add(vo);
        vo = new CommonData("mend", "补签");
        list.add(vo);
        if (sdao_count_field != null && sdao_count_field.length() > 0) {
            vo = new CommonData("loadon", "已上岛");
            list.add(vo);
            vo = new CommonData("loadoff", "未上岛");
            list.add(vo);
        }

        return list;
    }

    public HashMap getOnOffTime(String classid) {
        HashMap map = new HashMap();
        NetSignIn netSignIn = new NetSignIn();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        String columns = netSignIn.kqClassShiftColumns();
        sql.delete(0, sql.length());
        sql.append("select " + columns + " from kq_class where class_id='" + classid + "'");
        StringBuffer buf = new StringBuffer();
        buf.append("");
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            String on_start_time = "";
            String on_end_time = "";
            String off_start_time = "";
            String off_end_time = "";
            if (rs.next()) {

                buf.append(rs.getString("name"));
                buf.append("");
                buf.append(rs.getString("onduty_1") != null && rs.getString("onduty_1").length() > 0 ? "&nbsp;&nbsp;" + rs.getString("onduty_1") : "");
                String off = netSignIn.getOffduty(rs);
                buf.append(off != null && off.length() > 0 ? "~" + off : "");
                on_start_time = rs.getString("onduty_start_1");
                on_end_time = rs.getString("onduty_end_1");
                if (rs.getString("offduty_start_3") != null && rs.getString("offduty_start_3").length() > 0 && rs.getString("offduty_end_3") != null && rs.getString("offduty_end_3").length() > 0) {
                    off_start_time = rs.getString("offduty_start_3");
                    off_end_time = rs.getString("offduty_end_3");
                } else if (rs.getString("offduty_start_2") != null && rs.getString("offduty_start_2").length() > 0 && rs.getString("offduty_end_2") != null && rs.getString("offduty_end_2").length() > 0) {
                    off_start_time = rs.getString("offduty_start_2");
                    off_end_time = rs.getString("offduty_end_2");
                } else if (rs.getString("offduty_start_1") != null && rs.getString("offduty_start_1").length() > 0 && rs.getString("offduty_end_1") != null && rs.getString("offduty_end_1").length() > 0) {
                    off_start_time = rs.getString("offduty_start_1");
                    off_end_time = rs.getString("offduty_end_1");
                }
                map.put("on_start_time", on_start_time);
                map.put("on_end_time", on_end_time);
                map.put("off_start_time", off_start_time);
                map.put("off_end_time", off_end_time);
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return map;

    }

    public String getErrormess1() {
        return errormess1;
    }

    public void setErrormess1(String errormess1) {
        this.errormess1 = errormess1;
    }

    public String getErrormess2() {
        return errormess2;
    }

    public void setErrormess2(String errormess2) {
        this.errormess2 = errormess2;
    }

    public String getErrormess3() {
        return errormess3;
    }

    public void setErrormess3(String errormess3) {
        this.errormess3 = errormess3;
    }

    public String getErrormess4() {
        return errormess4;
    }

    public void setErrormess4(String errormess4) {
        this.errormess4 = errormess4;
    }
}
