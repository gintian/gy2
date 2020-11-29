package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.machine.EmpNetSignin;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 网上签到
 * @author Owner
 *
 */
public class OneNetSigninTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String a0100 = (String) this.getFormHM().get("a0100");
            a0100 = PubFunc.decrypt(a0100);
            String nbase = (String) this.getFormHM().get("nbase");
            nbase = PubFunc.decrypt(nbase);
            String registerdate = (String) this.getFormHM().get("workdate");
            String singin_flag = (String) this.getFormHM().get("singin_flag");
            if (a0100 == null || a0100.length() <= 0)
                throw new GeneralException("人员编号为空，错误！");
            
            if (nbase == null || nbase.length() <= 0)
                throw new GeneralException("人员库前缀为空，错误！");
            
            if (registerdate == null || registerdate.length() <= 0)
                throw new GeneralException("工作日期为空，错误！");
            
            if (registerdate != null && registerdate.length() > 0)
                registerdate = registerdate.replaceAll("-", ".");
            String net_sign_approve = KqParam.getInstance().getNetSignApprove();//签到签退数据需审批 0：无需审批 1：需要审批
            String sp_flag = "03";
            if (net_sign_approve != null && "1".equals(net_sign_approve))
                sp_flag = "02";
            EmpNetSignin empNetSignin = new EmpNetSignin(this.userView, this.getFrameconn());
            String work_time = empNetSignin.getWork_tiem();
            String workdate = empNetSignin.getWork_date();
            if (!registerdate.equals(workdate)) {
                throw new GeneralException("工作日期，必须选择当前日期！");
            }
            
            boolean isCorrect = empNetSignin.empNetSingin(a0100, nbase, workdate, work_time, sp_flag, singin_flag);
            this.getFormHM().put("mess", empNetSignin.getSignmess());
            if (isCorrect)
                this.getFormHM().put("flag", "ok");
            else
                this.getFormHM().put("flag", "no");
            
            nbase = PubFunc.encrypt(nbase);
            a0100 = PubFunc.encrypt(a0100);
            this.getFormHM().put("a0100", a0100);
            this.getFormHM().put("signin_type", "one");
            this.getFormHM().put("signin_flag", singin_flag);
            this.getFormHM().put("nbase", nbase);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
