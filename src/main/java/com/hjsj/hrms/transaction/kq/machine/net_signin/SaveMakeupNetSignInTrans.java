package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.EmpNetSignin;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.machine.RepairKqCard;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveMakeupNetSignInTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String singin_flag = (String) this.getFormHM().get("singin_flag");
            if (singin_flag == null || singin_flag.length() <= 0)
                return;

            NetSignIn netSignIn = new NetSignIn(this.userView, this.getFrameconn());
            String nbase = (String) this.getFormHM().get("nbase");
            String a0100 = (String) this.getFormHM().get("a0100");
            String work_date = (String) this.getFormHM().get("makeup_date");
            String work_tiem = (String) this.getFormHM().get("makeup_time");
            String oper_cause = (String) this.getFormHM().get("oper_cause");
            String ip_addr = (String) this.getFormHM().get("ip_addr");
            if (work_date == null || work_date.length() <= 0 || work_tiem == null || work_tiem.length() <= 0) {
                this.getFormHM().put("mess", "时间日期不能为空，补签申请失败！");
                return;
            }

            work_date = work_date.replaceAll("-", "\\.");
            try {
                Date dd = DateUtils.getDate(work_date, "yyyy.MM.dd");
            } catch (Exception e) {
                throw new GeneralException("日期格式不正确！yyyy-MM-dd");
            }

            try {
                Date dd = DateUtils.getDate(work_tiem, "HH:mm");
            } catch (Exception e) {
                throw new GeneralException("时间格式不正确！HH:mm");
            }

            /**服务器时间与补签时间对比;补签不等大于服务的当前时间; wangy 开始**/
            String work_date_server = netSignIn.getWork_date();
            String work_tiem_server = netSignIn.getWork_time();
            work_date_server = work_date_server.replaceAll("\\.", "-");
            String work_date2 = work_date.replaceAll("\\.", "-");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Calendar c1 = java.util.Calendar.getInstance();
            java.util.Calendar c2_server = java.util.Calendar.getInstance();
            try {
                c1.setTime(formatter.parse(work_date2));
                c2_server.setTime(formatter.parse(work_date_server));

            } catch (Exception e) {
                throw new GeneralException("日期时间类型不对！HH:mm");
            }
            int result = c1.compareTo(c2_server);
            if (result > 0) {
                throw new GeneralException("补签日期不能大于当前日期！");
            } else if (result == 0) {
                //日期相等就要对比时间
                SimpleDateFormat form = new SimpleDateFormat("HH:mm");
                java.util.Calendar c1_1 = java.util.Calendar.getInstance();
                java.util.Calendar c2_2_server = java.util.Calendar.getInstance();
                try {
                    c1_1.setTime(form.parse(work_tiem));
                    c2_2_server.setTime(form.parse(work_tiem_server));
                } catch (Exception e) {
                    throw new GeneralException("时间类型不对！");
                }
                int result_1 = c1_1.compareTo(c2_2_server);
                if (result_1 > 0)
                    throw new GeneralException("补签时间不能大于当前时间！");
            }
            /**结束**/

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String strDate = sdf.format(new Date());
            boolean isCorrect = false;
            String mess = "";

            String cardno = netSignIn.getKqCard(nbase, a0100);
            if (cardno == null || cardno.length() <= 0) {
                throw new GeneralException("没有分配考勤卡号，不能网上考勤！");
            }
            
            RepairKqCard repairKqCard = new RepairKqCard(this.getFrameconn(), this.userView);
            int numLimit = repairKqCard.getRepairCardNumLimit();
            if (numLimit > 0) {
                ArrayList datelist = RegisterDate.getKq_duration(work_date, this.getFrameconn());
                if (datelist != null && datelist.size() > 0) {
                    String start_date = datelist.get(0).toString();
                    String end_date = datelist.get(datelist.size() - 1).toString();
                    if (repairKqCard.isOverTopRepairdaynum(numLimit, nbase, a0100, start_date, end_date)) {
                        throw new GeneralException(ResourceFactory.getProperty("kq.repair.over.num_hint_1") + numLimit
                                + ResourceFactory.getProperty("kq.repair.over.num_hint_2"));
                    }
                }
            }

            String net_sign_approve = KqParam.getInstance().getNetSignApprove();//签到签退数据需审批 0：无需审批 1：需要审批
            String sp_flag = "03";
            if (net_sign_approve != null && "1".equals(net_sign_approve))
                sp_flag = "02";

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            if ("0".equals(singin_flag))//签到
            {
                if (!netSignIn.IsExists(nbase, a0100, work_date, work_tiem))
                    throw new GeneralException("规定时间间隔内不能签多次！");

                EmpNetSignin empNetSignin = new EmpNetSignin(this.userView, this.getFrameconn());
                LazyDynaBean bean = empNetSignin.getEmpBean(dao, a0100, nbase, work_date);
                netSignIn.signInScope(nbase, a0100, (String) bean.get("b0110"), (String) bean.get("e0122"), (String) bean
                        .get("e01a1"), work_date, work_tiem, singin_flag);
                String class_id = netSignIn.getClass_id();
                mess = netSignIn.signInCount(class_id, work_date, work_tiem, singin_flag);

                //增加了一个2 到 kq_originality_data 表里的 datafrom 字段2是补签
                Date oper_timest = DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm");
                isCorrect = empNetSignin.onNetSign(nbase, a0100, cardno, bean, oper_cause, oper_timest, work_date, work_tiem,
                        "补签到", sp_flag, ip_addr, "2");
                if (mess != null && mess.length() > 0)
                    mess = "，" + mess;

                if (isCorrect) {
                    mess = "补签到申请成功" + mess + "！";
                } else {
                    mess = "补签到申请失败！";
                }
            } else if ("1".equals(singin_flag))//签退
            {
                if (!netSignIn.IsExists(nbase, a0100, work_date, work_tiem))
                    throw new GeneralException("同一时刻不能签多次！");

                EmpNetSignin empNetSignin = new EmpNetSignin(this.userView, this.getFrameconn());
                LazyDynaBean bean = empNetSignin.getEmpBean(dao, a0100, nbase, work_date);
                netSignIn.signInScope(nbase, a0100, (String) bean.get("b0110"), (String) bean.get("e0122"), (String) bean
                        .get("e01a1"), work_date, work_tiem, singin_flag);
                String class_id = netSignIn.getClass_id();
                mess = netSignIn.signInCount(class_id, work_date, work_tiem, singin_flag);
                //增加了一个2 到 kq_originality_data 表里的 datafrom 字段2是补签
                Date oper_timest = DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm");
                isCorrect = empNetSignin.onNetSign(nbase, a0100, cardno, bean, oper_cause, oper_timest, work_date, work_tiem,
                        "补签退", sp_flag, ip_addr, "2");
                if (mess != null && mess.length() > 0)
                    mess = "，" + mess;

                if (isCorrect) {
                    mess = "补签退申请成功" + mess + "！";
                } else
                    mess = "补签退申请失败！";
            }
            this.getFormHM().put("mess", mess);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
