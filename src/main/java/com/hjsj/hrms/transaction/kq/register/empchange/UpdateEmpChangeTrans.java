package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.empchange.KqEmpChangeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 把从kq_employ_change表中得到的考勤增减员工信息,处理
 */
public class UpdateEmpChangeTrans extends IBusiness {
    
    public void execute() throws GeneralException {
        try {
            ArrayList emplist = (ArrayList) this.getFormHM().get("selectedlist");
            String changestatus = (String) this.getFormHM().get("changestatus");
            String start_date = (String) this.getFormHM().get("start_date");
            String end_date = (String) this.getFormHM().get("end_date");
            if (start_date == null || start_date.length() <= 0 || end_date == null || end_date.length() <= 0) {
                ArrayList kq_daylist = RegisterDate.getKqDurationList(this.frameconn);
                start_date = kq_daylist.get(0).toString();
                end_date = kq_daylist.get(kq_daylist.size() - 1).toString();
            }
            String TabName = (String) this.getFormHM().get("TabName");
            String code = RegisterInitInfoData.getKqPrivCodeValue(userView);
            
            KqEmpChangeBo empChangeBo = new KqEmpChangeBo(this.frameconn, this.userView);

            if (emplist != null && emplist.size() > 0) {
                if ("0".equals(changestatus)) { //减少人员
                    empChangeBo.change_leave(emplist, start_date, end_date);
                } else if ("1".equals(changestatus)) { //新增人员
                    empChangeBo.change_Add(emplist, start_date, end_date);
                    KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
                    ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
                    kqUtilsClass.leadingInItemToQ03(kq_dbase_list, start_date, end_date,"Q03","");//加入导入项

                    // 处理当月走的情况
                    ArrayList list = empChangeBo.getLeaveList(start_date, end_date, emplist);
                    if (list.size() > 0) {
                        empChangeBo.change_leave(list, start_date, end_date);
                    }
                } else if ("2".equals(changestatus)) { //基本信息变动（单位、部门、岗位变动）
                    String duration = RegisterDate.getDurationFromDate(start_date, this.getFrameconn());
                    empChangeBo.change_base(emplist, start_date, end_date, TabName, duration);

                } else if ("3".equals(changestatus)) { //异常数据
                    //status = 3;
                    empChangeBo.handleUnusual(emplist, start_date, end_date);
                }

                //加入导入项
                KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
                ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
                kqUtilsClass.leadingInItemToQ03(kq_dbase_list, start_date, end_date,"Q03","");
            }
            this.getFormHM().put("code", code);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
