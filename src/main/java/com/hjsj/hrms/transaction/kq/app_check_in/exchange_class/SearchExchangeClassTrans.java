package com.hjsj.hrms.transaction.kq.app_check_in.exchange_class;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.SelectAllOperate;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.app_check_in.exchange_class.ExchangeClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchExchangeClassTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            this.getFormHM().put("table", "q19");
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String returnvalue = "";
            if (hm != null) {
                returnvalue = (String) hm.get("returnvalue");
            }
            this.getFormHM().put("returnvalue", returnvalue);
            /**判断考勤期间*/
            ArrayList kqlists = RegisterDate.getKqDayList(this.getFrameconn());
            if (kqlists == null || kqlists.size() <= 0) {
                throw new GeneralException(ResourceFactory.getProperty("error.kq.please"));
            }
            /*添加字段*/
            SelectAllOperate selectAllOperate = new SelectAllOperate(this.getFrameconn(), this.userView);
            selectAllOperate.allOperate("q19");
            String code = (String) this.getFormHM().get("code");
            String kind = (String) this.getFormHM().get("kind");
            String frist = (String) this.getFormHM().get("frist_flag");
            if (kind == null || kind.length() <= 0) {
                kind = RegisterInitInfoData.getKindValue(kind, this.userView);
            }

            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            if (code == null || code.length() <= 0) {
                code = managePrivCode.getPrivOrgId();
                if ("".equals(code) || code.length() < 0) {
                    kind = "2";
                }
            } else if (kind == null || kind.length() <= 0) {
                code = managePrivCode.getPrivOrgId();
                kind = "2";
            }

            /* 获得考勤期间列表 */
            String kq_year = RegisterDate.getNoSealMinYear(this.getFrameconn());
            ArrayList yearlist = (ArrayList) this.getFormHM().get("yearlist");
            SearchAllApp searchAllApp = new SearchAllApp(this.getFrameconn(), this.userView);
            yearlist = searchAllApp.opin_Yearlist(yearlist);
            ArrayList durationlist = new ArrayList();
            /****判断年****/
            if (yearlist.size() == 0 || yearlist == null) {
                this.getFormHM().put("durationlist", durationlist);
                throw new GeneralException(ResourceFactory.getProperty("error.kq.please"));
            }
            this.getFormHM().put("yearlist", yearlist);
            /***********
            if (kq_year == null || kq_year.equals("")) {
                kq_year = RegisterDate.getNoSealMinYear(this.getFrameconn());
            }
            *********/
            this.getFormHM().put("kq_year", kq_year);

            RegisterDate registerDate = new RegisterDate();
            durationlist = registerDate.getOneYearDuration(kq_year, this.getFrameconn());
            if (durationlist.size() == 0 || durationlist == null)
                return;

            this.getFormHM().put("durationlist", durationlist);
            String kq_duration = "";
        
                ArrayList dlist = registerDate.getYearMinYearList(kq_year, this.getFrameconn());
                if (dlist.size() > 0)
                    kq_duration = searchAllApp.getFirstOfList(dlist);
                else
                    kq_duration = searchAllApp.getFirstOfList(durationlist);

                if (kq_duration == null || "".equals(kq_duration))
                    return;
            

            if (frist == null || "".equals(frist))
                frist = "";

            if ("1".equals(frist))
                kq_duration = searchAllApp.getFirstOfList(durationlist);
            this.getFormHM().put("kq_duration", kq_duration);

            String kq_start_str = "";
            String kq_end_str = "";
            if ("3".equals(frist)) {
                String start_date = (String) this.getFormHM().get("start_date");
                kq_start_str = start_date.replaceAll("-", "\\.");
                String end_date = (String) this.getFormHM().get("end_date");
                kq_end_str = end_date.replaceAll("-", "\\.");
            } else {
                ArrayList datelist = RegisterDate.getOneDurationDate(this.getFrameconn(), kq_duration);
                kq_start_str = datelist.get(0).toString();
                kq_end_str = datelist.get(1).toString();
                //szk取该年考勤区间的最后一天
               // String end_duration = ((CommonData) durationlist.get(durationlist.size() - 1)).getDataValue();
              //  ArrayList datelist2 = RegisterDate.getOneDurationDate(this.getFrameconn(), end_duration);
               //去今年最后一天
                kq_end_str = kq_end_str.substring(0, 4) + ".12.31";
                this.getFormHM().put("start_date", kq_start_str.replace(".", "-"));
                this.getFormHM().put("end_date", kq_end_str.replace(".", "-"));
            }

            ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldlist");
            ExchangeClass exchangeClass = new ExchangeClass();
            if (fieldlist == null || fieldlist.size() <= 0) {
                fieldlist = DataDictionary.getFieldList("q19", Constant.USED_FIELD_SET);// 字段名	
                fieldlist = exchangeClass.getNewFiledList(fieldlist);
            }

            String column = (String) this.getFormHM().get("column");
            if (column == null || column.length() <= 0) {
                KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
                HashMap hashmap = para.getKqParamterMap();
                String g_no = (String) hashmap.get("g_no");
                column = exchangeClass.getColumn(fieldlist);
                column = column.replace(",", ",q.");
                column += ",A01." + g_no;
            }

            /****人员库***/
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList dblist = kqUtilsClass.setKqPerList(code, kind);
            if (dblist.size() == 0 || dblist == null) {
                throw new GeneralException(ResourceFactory.getProperty("kq.register.dbase.nosave"));
            }

            StringBuffer sql = new StringBuffer();
            for (int i = 0; i < dblist.size(); i++) {
                String nbase = dblist.get(i).toString();
                String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                sql.append("select " + column);
                sql.append(" from q19 q," + nbase + "A01 A01");
                sql.append(" where 1=1 and q.a0101=A01.a0101");
                sql.append(" and nbase='" + nbase + "'");
                sql.append(" and q.a0100=A01.a0100");
                sql.append(" and (( q19z1>='" + kq_start_str + "'");
                sql.append(" and q19z1<='" + kq_end_str + "' )");
                sql.append(" or( q19z3>='" + kq_start_str + "'");
                sql.append(" and q19z3<='" + kq_end_str + "' ))");
                sql.append(" and q19z5 <> '01'");
                if ("1".equals(kind)) {
                    sql.append(" and q.e0122 like '" + code + "%'");
                } else if ("0".equals(kind)) {
                    sql.append(" and q.e01a1 like '" + code + "%'");
                } else {
                    sql.append(" and q.b0110 like '" + code + "%'");
                }
                sql.append(" and q.a0100 in(select a0100 " + whereIN + ")");
                sql.append(" UNION ");
            }
            sql.setLength(sql.length() - 7);
            selectAllOperate.allSelectApp("Q19", dblist);
            //System.out.println(sql.toString());
           //xiexd 2014.09.11 将导出模板的sql语句保存至服务器
            String kq_sql = sql.toString();
            this.userView.getHm().put("kq_sql_1",kq_sql);
            this.getFormHM().put("sql", sql.toString());
            this.getFormHM().put("fieldlist", fieldlist);
            this.getFormHM().put("column", column);
            String relatTableid = "19";
            this.getFormHM().put("returnURL", "/kq/app_check_in/exchange_class/exchangedata.do?b_search=link");
            StringBuffer whereINStr = new StringBuffer();
            whereINStr.append("1=1 and");
            whereINStr.append(" (( q19z1>='" + kq_start_str + "'");
            whereINStr.append(" and q19z1<='" + kq_end_str + "' )");
            whereINStr.append(" or( q19z3>='" + kq_start_str + "'");
            whereINStr.append(" and q19z3<='" + kq_end_str + "' ))");
            if ("1".equals(kind)) {
                whereINStr.append(" and e0122 like '" + code + "%'");
            } else if ("0".equals(kind)) {
                whereINStr.append(" and e01a1 like '" + code + "%'");
            } else {
                whereINStr.append(" and b0110 like '" + code + "%'");
            }

            String cond0 = searchAllApp.getPrivWhere(kind, code, dblist, "q19");
            if (cond0.length() > 0) {
                whereINStr.append(" and a0100 in (");
                whereINStr.append(cond0);
                whereINStr.append(")");
            }
            // 涉及SQL注入直接放进userView里
 			this.userView.getHm().put("kq_condition", relatTableid + "`" + whereINStr.toString());
//            this.getFormHM().put("condition", relatTableid + "`" + whereINStr.toString());
            this.getFormHM().put("relatTableid", relatTableid);

            //已批申请登记数据是否可以删除;0:不删除；1：删除
            String approved_delete = KqParam.getInstance().getApprovedDelete();
            approved_delete = approved_delete != null && approved_delete.length() > 0 ? approved_delete : "1";
            this.getFormHM().put("approved_delete", approved_delete);
            
            getOpinionlength();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**
     * 获取领导建议字段长度
     * @throws SQLException
     */
    private void getOpinionlength() throws SQLException{
        FieldItem item = DataDictionary.getFieldItem("q1915", "q19");
        this.getFormHM().put("opinionlength", item.getItemlength()+"");
    }
}
