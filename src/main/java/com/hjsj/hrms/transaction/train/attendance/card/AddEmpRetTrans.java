package com.hjsj.hrms.transaction.train.attendance.card;

import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * <p>
 * Title:AddEmpRetTrans.java
 * </p>
 * <p>
 * Description>:AddEmpRetTrans.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Mar 14, 2011 3:05:55 PM
 * </p>
 * <p>
 * 
 * @version: 5.0
 *           </p>
 *           <p>
 * @author: 郑文龙
 */
public class AddEmpRetTrans extends IBusiness {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException {
        String regFlag = (String) this.getFormHM().get("regFlag");// 1：签到2：签退标记
        String courseplan = (String) this.getFormHM().get("courseplan");// 培训课程编号
        if (courseplan != null && courseplan.length() > 0)
            courseplan = PubFunc.decrypt(SafeCode.decode(courseplan));

        String nowDate = (String) this.getFormHM().get("nowDate");
        String nowHours = (String) this.getFormHM().get("nowHours");
        nowHours = nowHours == null || nowHours.length() < 1 ? "00" : nowHours;
        nowHours = nowHours != null && nowHours.length() == 1 ? "0" + nowHours : nowHours;
        String nowMinutes = (String) this.getFormHM().get("nowMinutes");
        nowMinutes = nowMinutes == null || nowMinutes.length() < 1 ? "00" : nowMinutes;
        nowMinutes = nowMinutes != null && nowMinutes.length() == 1 ? "0" + nowMinutes : nowMinutes;
        String retReason = (String) this.getFormHM().get("retReason");
        retReason = retReason == null || retReason.length() < 1 ? "" : SafeCode.decode(retReason).replaceAll("'", "''");
        List usrid = (ArrayList) this.getFormHM().get("usrid");
        TrainAtteBo bo = new TrainAtteBo();
        Date date = OperateDate.strToDate(nowDate + " " + nowHours + ":" + nowMinutes + ":0" + regFlag, "yyyy-MM-dd HH:mm:ss");
        RecordVo vo = bo.getRegStateVo(regFlag, date, courseplan, this.frameconn);
        if (vo == null) {
            if ("3".equals(regFlag)) {
                throw new GeneralException(ResourceFactory.getProperty("train.b_plan.reg.errmsg3"));
            } else if ("4".equals(regFlag)) {
                throw new GeneralException(ResourceFactory.getProperty("train.b_plan.reg.errmsg4"));
            }
        }
        int card_type = vo.getInt("card_type");
        int late_for = vo.getInt("late_for");
        int leave_early = vo.getInt("leave_early");
        ArrayList empList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            for (int i = 0; i < usrid.size(); i++) {
                String[] usr = ((String) usrid.get(i)).split("\\`");
                String nbase = usr[0].trim();
                if (nbase != null && nbase.length() > 0)
                    nbase = PubFunc.decrypt(SafeCode.decode(nbase));
                String a0100 = usr[1].trim();
                if (a0100 != null && a0100.length() > 0)
                    a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
                RecordVo rvo = new RecordVo("tr_cardtime");
                rvo = bo.getVo(a0100, nbase, this.frameconn, rvo);
                rvo.setString("oper_cause", retReason);
                rvo.setString("oper_user", this.userView.getUserName());
                rvo.setDate("oper_time", new Date());
                rvo.setDate("card_time", date);
                rvo.setInt("card_type", card_type);// --------类别（1：签到/2：签退）
                rvo.setString("r4101", courseplan);
                rvo.setInt("late_for", late_for);
                rvo.setInt("leave_early", leave_early);
                if (bo.isExistsRecord(rvo, this.frameconn)) {
                    if ("1".equals(regFlag) || "3".equals(regFlag)) {
                        throw new GeneralException(rvo.getString("a0101") + "," + ResourceFactory.getProperty("train.b_plan.reg.errmsg5"));
                    } else if ("2".equals(regFlag) || "4".equals(regFlag)) {
                        throw new GeneralException(rvo.getString("a0101") + "," + ResourceFactory.getProperty("train.b_plan.reg.errmsg6"));
                    }
                }
                empList.add(rvo);
            }
            dao.addValueObject(empList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // String reg_state = bo.getMsgBy(vo.getInt("late_for"),
        // vo.getInt("leave_early"));
        // String nowTime = OperateDate.dateToStr(date, "mm:ss");
        // this.getFormHM().put("u_name", vo.getString("a0101"));
        // this.getFormHM().put("nowTime", nowTime);
        // this.getFormHM().put("reg_state", reg_state);
        this.getFormHM().put("isOk", "1");
    }
}
