package com.hjsj.hrms.transaction.kq.register.history.statfx;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.statfx.RegisterStatBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 点击次数查询出人员属性
 * 
 * @author Owner
 * 
 */
public class SeeNameDataTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String b0110 = (String) hm.get("b01101"); // 功能号
            String itemid = (String) hm.get("itemid"); // 指标号
            String code11 = (String) hm.get("codetj");
            String registertime = (String) this.getFormHM().get("registertime"); // 开始时间
            String jsdatetime = (String) this.getFormHM().get("jsdatetime"); // 结束时间
            String codesetid = "UN";
            if (!userView.isSuper_admin()) // 判断是否是超级
            {
                // if("UM".equals(userView.getManagePrivCode()))
                // //取得操作用户的管理范围代码类 UN或者UM
                if ("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
                    codesetid = "UM";
            }

            String cur_course = "";
            String sanshu = "";
            /** 得到当前考勤期间 **/
            if ((registertime != null && !"".equals(registertime)) || (jsdatetime != null && !"".equals(jsdatetime))) {
                cur_course = registertime;
            } else {
                /** 得到当前考勤期间 **/
                ArrayList list = RegisterDate.getKqDayList(this.getFrameconn());
                if (list != null && list.size() > 0) {
                    // 当前考勤期间开始时间
                    cur_course = list.get(0).toString(); 
                }
            }

            String cur_co = cur_course.substring(0, 4);
            /** 标题头 **/
            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            ArrayList kqq03list = RegisterStatBo.setnamelist(itemid, this.getFrameconn(), fielditemlist);

            /** 时间范围 **/
            cur_course = sevtime(b0110, cur_co);
            sanshu = sanshu(b0110, cur_co, itemid, codesetid); // 数值

            //zxj 20160504 增加人员权限
            String privWhr = RegisterInitInfoData.getKqEmpPrivWhr(this.frameconn, this.userView, "Q05");
            String codesetidvalue = judgeunandum(b0110);
            ArrayList sqllist = RegisterStatBo.Sevsetname(cur_course, b0110, itemid, "Q05", kqq03list, cur_co, codesetidvalue, privWhr);
            
            this.getFormHM().put("sqlstrs", sqllist.get(0).toString());
            this.getFormHM().put("strwheres", sqllist.get(1).toString());
            this.getFormHM().put("orderbys", sqllist.get(2).toString());
            this.getFormHM().put("columnss", sqllist.get(3).toString());
            this.getFormHM().put("sanshu", sanshu);
            this.getFormHM().put("kqnamelsit", kqq03list);
            this.getFormHM().put("backy", code11);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 得到实践范围
     * 
     * @param b0110
     * @param cur_course
     * @return
     */
    private String sevtime(String b0110, String cur_course) {
        String time = "";
        cur_course = cur_course + "-PT";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowSet = null;
        try {
            StringBuffer sql = new StringBuffer();
            b0110 = b0110 != null ? b0110 : "";
            if ("".equals(b0110)) {
                sql.setLength(0);
                sql.append("select min(b0110) as b0110 from Q09");
                rowSet = dao.search(sql.toString());
                while (rowSet.next()) {
                    b0110 = rowSet.getString("b0110");
                }
            }
            sql.setLength(0);
            sql.append("select scope from Q09 where q03z0='" + cur_course + "' and b0110='" + b0110 + "'");
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                time = rowSet.getString("scope");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return time;
    }

    /**
     * 
     * @param b0110
     *            权限
     * @param cur_course
     * @param itemid
     * @param dbase
     *            codesetid NU UM
     * @return
     */
    private String sanshu(String b0110, String cur_course, String itemid, String codesetid) {
        String time = "";
        cur_course = cur_course + "-PT";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowSet = null;
        String codesetids = judgeunandum(b0110);
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(*) as a0100 from ( select  nbase,a0100 from Q05 where ");
            sql.append("q03z0 like '" + cur_course + "%'");
            if ("UN".equalsIgnoreCase(codesetids)) {
                sql.append(" and b0110 like '" + b0110 + "%' and " + itemid + ">0)a");
            } else {
                sql.append(" and e0122 like '" + b0110 + "%' and " + itemid + ">0)a");
            }

            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                time = rowSet.getString("a0100");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return time;
    }

    private String judgeunandum(String b0110) {
        String codesetid = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowSet = null;
        try {
            String sql = "select codesetid from Organization where codeitemid= '" + b0110 + "'";
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                codesetid = rowSet.getString("codesetid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return codesetid;
    }
}
