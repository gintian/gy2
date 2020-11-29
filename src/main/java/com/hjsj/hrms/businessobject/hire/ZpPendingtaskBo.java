package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 招聘需求模块：更新查询待办任务表
 * 
 * @author chenxg
 * 
 */
public class ZpPendingtaskBo {
    private Connection conn = null;
    private UserView userview = null;

    public ZpPendingtaskBo(Connection conn, UserView userview) {
        this.userview = userview;
        this.conn = conn;
    }

    /**
     * 招聘需求报批时更新待办任务表
     * 
     * @param str
     *            当前用户的直接领导
     * @param type
     *            审批关系中参与者的类型
     */
    public void updatePendingTask(String str, String type) {
        if (str == null || str.length() < 1) {
            return;
        }

        RowSet rs = null;
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
            String date = df.format(calendar.getTime());

            IDGenerator idg = new IDGenerator(2, this.conn);
            String pending_id = idg.getId("pengdingTask.pengding_id");

            String sender = this.userview.getUserName();
            String receiver = "";

            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer();
            if ("1".equals(type)) {
                String nbase = str.substring(0, 3);
                String a0100 = str.substring(3);
                AttestationUtils utils = new AttestationUtils();
                LazyDynaBean abean = utils.getUserNamePassField();
                String username_field = (String) abean.get("name");
                sql.append("select " + username_field + " username");
                sql.append(" from " + nbase + "A01");
                sql.append(" where a0100='" + a0100 + "'");
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    receiver = rs.getString("username");
                }

            } else {
                receiver = str;
            }

            String flag = checkPendingTask(sender, receiver);

            if ("0".equalsIgnoreCase(flag)) {// 在待办任务表中新增待办数据
                RecordVo vo = new RecordVo("t_hr_pendingtask");
                vo.setString("pending_id", pending_id);
                vo.setDate("create_time", date);
                vo.setDate("lasttime", date);
                vo.setString("sender", sender);
                vo.setString("pending_type", "32");
                vo.setString("pending_title", ResourceFactory.getProperty("hire.apply.approve"));
                vo.setString("pending_url", "/hire/demandPlan/positionDemand/positionDemandTree.do?br_query2=query&returnflag=8");
                vo.setString("pending_status", "0");
                vo.setString("pending_level", "1");
                vo.setString("receiver", receiver);
                dao.addValueObject(vo);
            } else if ("2".equalsIgnoreCase(flag)) {// 在待办任务表中存在对应的待办数据但状态不是待办
                sql.delete(0, sql.length());
                sql.append("update t_hr_pendingtask set Pending_status='0',");
                sql.append(" sender='" + sender + "',");
                sql.append(" Lasttime=" + Sql_switcher.charToDate("'" + date + "'"));
                sql.append(" where Pending_type='32'");
                sql.append(" and Receiver='" + receiver + "'");
                sql.append(" and (Pending_status='3' or Pending_status='0')");
                dao.update(sql.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rsclosed(rs);
        }

    }

    /**
     * 检查是否存在招聘需求审批的待办任务
     * 
     * @param Receiver
     *            接收者帐号
     * @return flag=0：没有；1：存在且状态为待办；2：存在但状态为已阅
     */
    private String checkPendingTask(String sender, String receiver) {
        String flag = "0";
        String Pendingstatus = "";

        StringBuffer sql = new StringBuffer();
        sql.append("select Pending_status,Sender");
        sql.append(" from t_hr_pendingtask");
        sql.append(" where Pending_type='32'");
        sql.append(" and Receiver='" + receiver + "'");
        sql.append(" and (Pending_status='0' or Pending_status='3')");

        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                Pendingstatus = rs.getString("Pending_status");
            }

            if ("0".equalsIgnoreCase(Pendingstatus)) {
                if (rs.getString("Sender").equalsIgnoreCase(sender)) {
                    flag = "1";
                } else {
                    flag = "2";
                }
            } else if ("3".equalsIgnoreCase(Pendingstatus)) {
                flag = "2";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            rsclosed(rs);
        }
        return flag;

    }

    /**
     * 更新待办任务表的数据
     * 
     * @param flag
     *            =0：浏览审核页面；1：执行审批/驳回操作 ；2/3:关联人员的业务用户更新人员的我的任务
     */
    public void updatingPendingTask(String flag) {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
            String date = df.format(calendar.getTime());

            String receiver = this.userview.getUserName();
            String a01username = geta01Username(receiver);
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer();

            if ("0".equalsIgnoreCase(flag) || "3".equalsIgnoreCase(flag)) {
                sql.append("update t_hr_pendingtask set Pending_status='3'");
            } else if ("1".equalsIgnoreCase(flag) || "2".equalsIgnoreCase(flag)) {
                sql.append("update t_hr_pendingtask set Pending_status='1'");
            }

            sql.append(" where Pending_type='32'");

            if ("0".equalsIgnoreCase(flag) || "1".equalsIgnoreCase(flag)) {
                sql.append(" and Receiver='" + receiver + "'");
            } else if ("2".equalsIgnoreCase(flag) || "3".equalsIgnoreCase(flag)) {
                sql.append(" and Receiver='" + a01username + "'");
            }

            sql.append(" and " + Sql_switcher.dateToChar("Lasttime", "yyyy-MM-dd") + "='" + date + "'");

            if ("0".equalsIgnoreCase(flag) || "3".equalsIgnoreCase(flag)) {
                sql.append("  and pending_status='0'");
            } else if ("1".equalsIgnoreCase(flag) || "2".equalsIgnoreCase(flag)) {
                sql.append(" and (pending_status='0' or pending_status='3')");
            }

            dao.update(sql.toString());

            if ("0".equalsIgnoreCase(flag) && userview.getStatus() != 4 && a01username != null && a01username.length() > 0) {
                updatingPendingTask("3");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查招聘已报批的需求是否全部处理若全部处理则更新待办表数据
     */
    public void checkZpappr() {
        boolean flag = false;
        ContentDAO dao = new ContentDAO(this.conn);
        String currappuser = "";
        RowSet rs = null;
        try {
            int status = this.userview.getStatus();
            if (status == 4) {
                currappuser = this.userview.getDbname() + this.userview.getA0100();
            } else {
                currappuser = this.userview.getUserName();
            }

            rs = dao.search("select 1 from z03 where (z0319='02' or z0319='07') and currappuser='" + currappuser + "'");
            if (rs.next()) {
                flag = true;
            }

            if (!flag) {
                updatingPendingTask("1");
            } else if (flag) {
                updatingPendingTask("0");
            }

            if (status != 4) {
                flag = false;
                String a01username = geta01Username(currappuser);
                rs = dao.search("select 1 from z03 where (z0319='02' or z0319='07') and currappuser='" + a01username + "'");
                if (rs.next()) {
                    flag = true;
                }

                if (!flag) {
                    updatingPendingTask("2");
                } else if (flag) {
                    updatingPendingTask("3");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            rsclosed(rs);
        }

    }

    /**
     * 我的任务显示待办任务
     * 
     * @return
     */
    public ArrayList getZpapprDta() {
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            String receiver = this.userview.getUserName();
            String username = geta01Username(receiver);

            StringBuffer sql = new StringBuffer();
            sql.append("select Pending_title,Pending_url,Pending_status,pending_id");
            sql.append(" from t_hr_pendingtask");
            sql.append(" where Pending_type='32'");
            sql.append(" and (pending_status='0' or pending_status='3')");

            if (username != null && username.length() > 0) {
                sql.append(" and (Receiver='" + receiver + "' or Receiver='" + username + "')");
            } else {
                sql.append(" and Receiver='" + receiver + "'");
            }

            rs = dao.search(sql.toString());
            while (rs.next()) {
                CommonData cData = new CommonData();
                String Pending_status = "";
                if ("0".equalsIgnoreCase(rs.getString("Pending_status"))) {
                    Pending_status = "待办";
                } else if ("3".equalsIgnoreCase(rs.getString("Pending_status"))) {
                    Pending_status = "已阅";
                }
                cData.setDataName(rs.getString("Pending_title") + "(" + Pending_status + ")");
                cData.setDataValue(rs.getString("Pending_url") + "&amp;pendingId=" + rs.getString("pending_id"));
                list.add(cData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            rsclosed(rs);
        }
        return list;

    }

    /**
     * 检查业务用户关联的人员的登录帐号
     * 
     * @param receiver
     * @return
     */
    private String geta01Username(String receiver) {
        String usernaem = "";
        String a0100 = "";
        String nbase = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search("select a0100,nbase from operuser where username='" + receiver + "'");
            if (rs.next()) {
                a0100 = rs.getString("a0100");
                nbase = rs.getString("nbase");
            }

            if (a0100 != null && a0100.length() > 0 && nbase != null && nbase.length() > 0) {
                AttestationUtils utils = new AttestationUtils();
                LazyDynaBean abean = utils.getUserNamePassField();
                String username_field = (String) abean.get("name");
                StringBuffer sql = new StringBuffer();
                sql.append("select " + username_field + " username");
                sql.append(" from " + nbase + "A01");
                sql.append(" where a0100='" + a0100 + "'");
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    usernaem = rs.getString("username");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rsclosed(rs);
        }

        return usernaem;

    }

    /**
     * 关闭rowset
     * 
     * @param rs
     */
    private void rsclosed(RowSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
