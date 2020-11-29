package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.machine.EmpNetSignin;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class BatchNetSigninTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            ArrayList objlist = (ArrayList) this.getFormHM().get("objlist");
            String nbase = (String) this.getFormHM().get("nbase");
            String registerdate = (String) this.getFormHM().get("workdate");
            String singin_flag = (String) this.getFormHM().get("singin_flag");
            
            if (objlist == null || objlist.size() <= 0)
                throw new GeneralException("人员库前缀为空，错误！");
            
            if (registerdate == null || registerdate.length() <= 0)
                throw new GeneralException("工作日期为空，错误");
            
            if (registerdate != null && registerdate.length() > 0)
                registerdate = registerdate.replaceAll("-", ".");

            //签到签退数据需审批 0：无需审批 1：需要审批
            String net_sign_approve = KqParam.getInstance().getNetSignApprove();
            String sp_flag = "03";
            if (net_sign_approve != null && "1".equals(net_sign_approve))
                sp_flag = "02";

            EmpNetSignin empNetSignin = new EmpNetSignin(this.userView, this.getFrameconn());
            String work_time = empNetSignin.getWork_tiem();
            String workdate = empNetSignin.getWork_date();
            if (!registerdate.equals(workdate)) {
                throw new GeneralException("考勤期间日期,必须选择当前日期！");
            }
            
            boolean isCorrect = empNetSignin.bacthEmpNetSingin(objlist, nbase, workdate, work_time, sp_flag, singin_flag);
            if (isCorrect) {
                this.getFormHM().put("flag", "ok");
                this.getFormHM().put("mess", empNetSignin.getSignmess());
            } else
                this.getFormHM().put("flag", "no");
            
            this.getFormHM().put("signin_type", "bacth");
            this.getFormHM().put("signin_flag", singin_flag);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
