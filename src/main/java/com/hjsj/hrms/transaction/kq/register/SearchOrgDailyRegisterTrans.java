package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** 浏览部门日考勤纪录 */

public class SearchOrgDailyRegisterTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String code = (String) hm.get("code");
            String b0110 = code;
            ArrayList datelist = (ArrayList) this.getFormHM().get("datelist");
            String kq_period = (String) this.getFormHM().get("kq_period");
            if (datelist == null || datelist.size() <= 0) {
                datelist = RegisterDate.getKqDurationList(this.frameconn);
            }
            String start_date = datelist.get(0).toString();
            String end_date = datelist.get(datelist.size() - 1).toString();

            String kq_duration = (String) this.getFormHM().get("kq_duration");
            if (kq_period == null || kq_period.length() <= 0) {
                kq_period = OrgRegister.getMonthRegisterDate(start_date, end_date);
            }
            if (b0110 == null || b0110.length() <= 0) {
                b0110 = RegisterInitInfoData.getKqPrivCodeValue(userView);
            }
            // 判断当前操作考勤期间是否有数据

            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            ArrayList list = OrgRegister.newFieldItemList(fielditemlist);
            String codesetid = "UN";
            if (!userView.isSuper_admin()) {
                if ("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
                    codesetid = "UM";
            }
            list = OrgRegister.newFieldItemListQ07(list, codesetid);
            ArrayList vo_datelist = (ArrayList) this.getFormHM().get("vo_datelist");
            String registerdate = (String) this.getFormHM().get("registerdate");
            String cur_date = "";
            if (vo_datelist == null || vo_datelist.size() <= 0) {
                vo_datelist = RegisterDate.registerdate(this.userView.getUserOrgId(), this.getFrameconn(), this.userView);
            } else {
                // zxj 20180619 vo_datelist中的数据可能是期间改变前的（封存或解封之前的期间）
                CommonData vo = (CommonData) vo_datelist.get(0);
                if (datelist != null && datelist.size() > 0 && !vo.getDataValue().equals((String) datelist.get(0)))
                    vo_datelist = RegisterDate.registerdate(this.userView.getUserOrgId(), this.getFrameconn(),
                            this.userView);
            }

            if (vo_datelist != null && vo_datelist.size() > 0) {
                if (registerdate != null && registerdate.length() > 0) {
                    //显示日期在当前期间内，则显示该日期数据，否则，显示当前期间第一天数据
                    if(registerdate.compareToIgnoreCase(start_date)>=0 && registerdate.compareToIgnoreCase(end_date)<=0)
                        cur_date = registerdate;
                    else
                        cur_date = start_date;
                } else {
                    // cur_date = vo.getDataValue();// 开始日期
                    String dateNow = PubFunc.FormatDate(new Date(), "yyyy.MM.dd");
                    DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                    Date startdate = null;
                    Date enddate = null;
                    try {
                        startdate = df.parse(start_date);
                        enddate = df.parse(end_date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // cur_date=start_date;
                    // 定位到现在的日期
                    if (new Date().getTime() >= startdate.getTime() && new Date().getTime() <= enddate.getTime()) {
                        cur_date = dateNow;
                    } else {
                        cur_date = start_date;
                    }
                }
            }
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
            ArrayList a0100whereIN = new ArrayList();
            for (int i = 0; i < kq_dbase_list.size(); i++) {
                String dbase = kq_dbase_list.get(i).toString();
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase);

                a0100whereIN.add(whereA0100In);
            }
            String workcalendar = RegisterInitInfoData.getDateSelectHtml(vo_datelist, cur_date);
            ArrayList sqllist = OrgRegister.getSqlstr(list, start_date, end_date, b0110, "Q07", cur_date, a0100whereIN);
            this.getFormHM().put("sqlstr", sqllist.get(0).toString());
            this.getFormHM().put("strwhere", sqllist.get(1).toString());
            this.getFormHM().put("columns", sqllist.get(2).toString());
            this.getFormHM().put("orderby", " order by b0110,q03z0");
            this.getFormHM().put("fielditemlist", list);
            this.getFormHM().put("kq_duration", kq_duration);
            this.getFormHM().put("orgvali", "");
            this.getFormHM().put("datelist", datelist);
            this.getFormHM().put("kq_period", kq_period);
            this.getFormHM().put("action", "collect_orgdailydata");
            this.getFormHM().put("workcalendar", workcalendar);
            this.getFormHM().put("registerdate", cur_date);
            this.getFormHM().put("vo_datelist", vo_datelist);

            // 将导出模板的sql语句保存至服务器
            String kq_sql_unit = sqllist.get(0).toString() + sqllist.get(1).toString() + " order by b0110,q03z0";
            this.userView.getHm().put("kq_sql_unit", kq_sql_unit);
            // 高级花名册条件 月汇总条件
            String strSQLWhere = sqllist.get(1).toString();
            strSQLWhere = strSQLWhere.substring(" from Q07  where ".length());
            // 涉及SQL注入直接放进userView里
            this.userView.getHm().put("kq_condition", "7`" + strSQLWhere);
            this.getFormHM().put("returnURL", "/kq/register/daily_registerdata.do?b_query=link");
            this.getFormHM().put("nprint", "7");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
