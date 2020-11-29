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

import java.util.Date;

/**
 * 
 * <p>
 * Title:AddEmpRegTrans.java
 * </p>
 * <p>
 * Description>:AddEmpRegTrans.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Mar 14, 2011 3:05:50 PM
 * </p>
 * <p>
 * 
 * @version: 5.0
 *           </p>
 *           <p>
 * @author: 郑文龙
 */
public class AddEmpRegTrans extends IBusiness {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException {
        String card_num = (String) this.getFormHM().get("card_num");// 考勤卡号
        String regFlag = (String) this.getFormHM().get("regFlag");// 1：签到2：签退标记
        String courseplan = (String) this.getFormHM().get("courseplan");// 培训课程编号
        String classplan = (String) this.getFormHM().get("classplan");// 培训班编号
        if (courseplan != null && courseplan.length() > 0)
            courseplan = PubFunc.decrypt(SafeCode.decode(courseplan));

        if (classplan != null && classplan.length() > 0)
            classplan = PubFunc.decrypt(SafeCode.decode(classplan));

        String into = (String) this.getFormHM().get("into");// 未排进培训班的人员刷卡时是否直接进库，1为直接入库，0为询问
        Date date = new Date();
        TrainAtteBo bo = new TrainAtteBo();
        bo.setUserView(this.userView);
        RecordVo vo = bo.getRegStateVo(regFlag, card_num, date, courseplan, this.frameconn, classplan);

        if (vo == null) {
            // if ("1".equals(regFlag)) {
            // throw new GeneralException(ResourceFactory
            // .getProperty("train.b_plan.reg.errmsg3"));
            // } else if ("2".equals(regFlag)) {
            // throw new GeneralException(ResourceFactory
            // .getProperty("train.b_plan.reg.errmsg4"));
            // }

            throw new GeneralException(ResourceFactory.getProperty("train.b_plan.reg.errmsg7"));

        }

        if ("1".equals(into) && bo.getInto()) {
            // 将人员加入到培训班
            bo.addTrainStudent(userView, this.frameconn, classplan, courseplan, card_num);
            this.getFormHM().put("isinto", "0");

        } else {
            if (bo.getInto()) {
                this.getFormHM().put("isinto", "1");
                this.getFormHM().put("a0101", vo.getString("a0101"));
                this.getFormHM().put("classname", bo.getClassName(classplan, this.frameconn));
                return;
            } else {
                this.getFormHM().put("isinto", "0");
            }
        }

        if (bo.isExistsRecord(vo, this.frameconn)) {
            this.getFormHM().put("u_name", "");
            this.getFormHM().put("nowTime", "");
            this.getFormHM().put("reg_state", "");
            if ("1".equals(regFlag) || "3".equals(regFlag)) {
                throw new GeneralException(vo.getString("a0101") + "," + ResourceFactory.getProperty("train.b_plan.reg.errmsg5"));
            } else if ("2".equals(regFlag) || "4".equals(regFlag)) {
                throw new GeneralException(vo.getString("a0101") + "," + ResourceFactory.getProperty("train.b_plan.reg.errmsg6"));
            }
        } else {
            ContentDAO dao = new ContentDAO(this.frameconn);
            dao.addValueObject(vo);
            String reg_state = bo.getMsgBy(vo.getInt("late_for"), vo.getInt("leave_early"));
            String nowTime = OperateDate.dateToStr(date, "HH:mm");
            this.getFormHM().put("u_name", vo.getString("a0101"));
            this.getFormHM().put("nowTime", nowTime);
            this.getFormHM().put("reg_state", reg_state);
        }
    }
}
