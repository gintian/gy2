package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.SelectAllOperate;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 对员工考勤操作期间数据初始化
 * 
 * */
//u
public class InitEmpDailyDataTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            SelectAllOperate selectAllOperate = new SelectAllOperate(this.getFrameconn(), this.userView);
            selectAllOperate.allOperate("q03");
            selectAllOperate.allOperate("q05");
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            ArrayList kq_dbase_list = (ArrayList) this.getFormHM().get("kq_dbase_list");
            String kind = (String) hm.get("kind");
            String code = (String) hm.get("code");
            CheckPrivSafeBo checkPriv = new CheckPrivSafeBo(this.frameconn, this.userView);
            code = checkPriv.checkOrg(code, "");
            String error_message = "";
            String error_flag = "0";
            String error_return = "";
            String error_stuts = (String) this.getFormHM().get("error_stuts");
            if (error_stuts != null && "1".equals(error_stuts)) {
                error_flag = (String) this.getFormHM().get("error_flag");
                error_return = (String) this.getFormHM().get("error_return");
                error_message = (String) this.getFormHM().get("error_message");
            }

            if (code == null || code.length() <= 0) {
                code = "";
            }
            String b0110 = "";
            String code_kind = "";
            if (kind == null || kind.length() <= 0) {
                kind = RegisterInitInfoData.getKindValue(kind, this.userView);
                code = "";
            }
            if (code.length() <= 0) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                b0110 = managePrivCode.getUNB0110();

            } else {
                if ("2".equals(kind)) {
                    b0110 = "UN" + code;
                } else {
                    code_kind = RegisterInitInfoData.getDbB0100(code, kind, this.getFormHM(), this.userView, this.getFrameconn());
                    b0110 = "UN" + code_kind;
                }

            }

            DbWizard dbWizard = new DbWizard(frameconn);
            String status_flag = null;
            if (dbWizard.isExistField("Q03", "isok", false))
                status_flag = "1";
            this.getFormHM().put("status_flag", status_flag);

            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            kq_dbase_list = kqUtilsClass.setKqPerList(code, kind);
            if (kq_dbase_list != null && kq_dbase_list.size() > 0) {

            } else {
                //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.dbase.nosave"),"",""));
                error_message = ResourceFactory.getProperty("kq.register.dbase.nosave");
                if (code == null || code.length() <= 0) {
                    error_return = "/templates/menu/kq_m_menu.do?b_query=link&module=6";
                    error_flag = "4";
                } else {
                    error_return = "history.back();";
                    error_flag = "1";
                }
            }

            String cur_date = (String) this.getFormHM().get("registerdate");
            if (cur_date == null || cur_date.length() <= 0) {
                cur_date = "";
            }
            ArrayList datelist = RegisterDate.registerdate(b0110, this.getFrameconn(), this.userView);
            String start_date = "";
            String end_date = "";
            if (datelist == null || datelist.size() <= 0) {
                if (code == null || code.length() <= 0) {
                    error_return = "/templates/menu/kq_m_menu.do?b_query=link&module=6";
                    error_flag = "4";
                } else {
                    error_return = "history.back();";
                    error_flag = "1";
                }
                error_message = ResourceFactory.getProperty("kq.register.session.nosave");
                //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
            } else {
                String dateNow = PubFunc.FormatDate(new Date(), "yyyy.MM.dd");
                CommonData vo = (CommonData) datelist.get(0);
                start_date = vo.getDataValue();// 开始日期
                vo = (CommonData) datelist.get(datelist.size() - 1);
                end_date = vo.getDataValue();
                if (start_date.compareTo(dateNow) == 0) {
                    //开始时间	
                    cur_date = start_date;
                } else if (end_date.compareTo(dateNow) == 0) {
                    //结束时间
                    cur_date = end_date;
                } else if (start_date.compareTo(dateNow) < 0 && end_date.compareTo(dateNow) > 0) {
                    cur_date = dateNow;
                } else if (!cur_date.trim().equals(start_date.trim())) {

                    DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                    Date startdate = df.parse(start_date);
                    Date enddate = df.parse(end_date);
                    //    		    	cur_date=start_date;
                    //定位到现在的日期
                    if (new Date().getTime() >= startdate.getTime() && new Date().getTime() <= enddate.getTime()) {
                        cur_date = dateNow;
                    } else {
                        cur_date = start_date;
                    }

                }
            }
            selectAllOperate.operateQ03State(kq_dbase_list, start_date, end_date);
            ArrayList showalldatelist = new ArrayList();
            for (int i = 0; i < datelist.size(); i++) {
                showalldatelist.add(datelist.get(i));
            }
            CommonData vo_all = new CommonData("all", "全部");
            showalldatelist.add(0, vo_all);
            this.getFormHM().put("datelist", datelist);
            this.getFormHM().put("showalldatelist", showalldatelist);
            this.getFormHM().put("registerdate", cur_date);
            this.getFormHM().put("code", code);
            this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));
            this.getFormHM().put("kq_dbase_list", kq_dbase_list);
            String kq_duration = RegisterDate.getKqDuration(this.frameconn);
            this.getFormHM().put("kq_duration", kq_duration);
            this.getFormHM().put("pigeonhole_flag", "xxx");
            this.getFormHM().put("kind", kind);
            this.getFormHM().put("error_message", error_message);
            this.getFormHM().put("error_flag", error_flag);
            this.getFormHM().put("error_return", error_return);

            //修改日明细登记数据
            String up_dailyregister = KqParam.getInstance().getUpdateDailyRegister();
            this.getFormHM().put("up_dailyregister", up_dailyregister);

            if (this.userView.hasTheFunction("270206")) {
                this.getFormHM().put("haveApprove", "1");
            }
            if ("zizhu".equals((String) hm.get("returnvalue")))
                this.getFormHM().put("moduleFlag", "1");
            else
                this.getFormHM().put("moduleFlag", "0");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }
}
