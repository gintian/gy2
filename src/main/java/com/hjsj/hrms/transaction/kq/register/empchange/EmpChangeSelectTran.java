package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.empchange.KqEmpChangeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 现行的变动比对是按当前考勤期间的考勤数据和人员库比较,如果有上个考勤期间的最后一天那么就和上一个考勤期间的最后一天比
 *<p>Title:EmpChangeSelectTran.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 7, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class EmpChangeSelectTran extends IBusiness {
    /**
     * 考勤人员变动情况
     * */
    private String bdstatic = "";
    private KqDBHelper kqDB = null;

    public void execute() throws GeneralException {
        try {
            kqDB = new KqDBHelper(this.getFrameconn());
            KqEmpChangeBo empChangeBo = new KqEmpChangeBo(this.getFrameconn(), this.getUserView());
            
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

            // 人员库与上一个考勤期间比对变动人员 1为与上月做对比
            String re_static = (String) hm.get("re_static");

            if (re_static == null || re_static.length() <= 0) {
                re_static = (String) this.getFormHM().get("re_static");
                if (re_static == null || re_static.length() <= 0)
                    re_static = "0";
            }
            this.bdstatic = re_static;
            this.getFormHM().put("re_static", re_static);
            
            // 得到一个考勤期间内所有的日期
            ArrayList kq_daylist = RegisterDate.getKqDurationList(this.frameconn);
            // 该考勤期间的开始结束时间
            String kqstart = kq_daylist.get(0).toString();
            String kqend = kq_daylist.get(kq_daylist.size() - 1).toString();
            
            HashMap<String, String> compareResult = empChangeBo.CompareEmpChange(kqstart, kqend);
            this.getFormHM().put("TabName", compareResult.get("TabName"));
            this.getFormHM().put("change_date", compareResult.get("change_date"));
            this.getFormHM().put("state_date", kqstart);
            this.getFormHM().put("end_date", kqend);
            this.getFormHM().put("ishaveadd", compareResult.get("ishaveadd"));
            this.getFormHM().put("ishavecut", compareResult.get("ishavecut"));
            this.getFormHM().put("ishavechange", compareResult.get("ishavechange"));
            this.getFormHM().put("ishaveexce", compareResult.get("ishaveexce"));
            // 部门层级
            Sys_Oth_Parameter sys = new Sys_Oth_Parameter(this.frameconn);
            String uplevel = sys.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (uplevel == null || uplevel.length() <= 0) {
                uplevel = "0";
            }
            this.getFormHM().put("uplevel", uplevel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}