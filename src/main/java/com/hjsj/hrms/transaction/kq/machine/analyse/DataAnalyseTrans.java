package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.machine.DateAnalyseImp;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 数据处理
 *<p>
 * Title:DataAnalyseTrans.java
 * </p>
 *<p>
 * Description:
 * </p>
 *<p>
 * Company:HJHJ
 * </p>
 *<p>
 * Create time:Oct 29, 2007
 * </p>
 * 
 * @author sunxin
 *@version 4.0
 */
public class DataAnalyseTrans extends IBusiness implements DateAnalyseImp {

    public void execute() throws GeneralException {
        try {
            String a_code = (String) this.getFormHM().get("a_code");
            String nbase = (String) this.getFormHM().get("nbase");
            String start_date = (String) this.getFormHM().get("start_date");
            String end_date = (String) this.getFormHM().get("end_date");
            String isAsync = (String)this.getFormHM().get("isasync");
            isAsync = isAsync == null || "".equals(isAsync) ? "0" : "1";
            
            /** 数据处理模式 mark=1为集中处理，0为分用户 **/
            // String analyseType="1";
            String analyseType = "";
            String mark = KqParam.getInstance().getData_processing();
            mark = mark != null && mark.length() > 0 ? mark : "0";
            if ("1".equalsIgnoreCase(mark)) {
                analyseType = "101";
            } else {
                analyseType = "1";
            }
            /** 结束 **/

            if (a_code == null || a_code.length() <= 0) {
                a_code = "UN";
            }

            String kind = "2";
            if (a_code.indexOf("UN") != -1) {
                kind = "2";
            } else if (a_code.indexOf("UM") != -1) {
                kind = "1";
            } else if (a_code.indexOf("@K") != -1) {
                kind = "0";
            } else if (a_code.indexOf("EP") != -1) {
                kind = "-1";
            }

            String code = "";
            if (a_code.length() > 2) {
                code = a_code.substring(2);
            }

            if ("-1".equals(kind)) {
                code = nbase + code;
            }

            if (start_date == null || start_date.length() <= 0)
                throw new GeneralException("处理起始时间不能为空！");

            if (end_date == null || end_date.length() <= 0)
                throw new GeneralException("处理结束时间不能为空！");

            start_date = start_date.replaceAll("-", "\\.");
            end_date = end_date.replaceAll("-", "\\.");
            try {
                start_date = DateUtils.format(DateUtils.getDate(start_date, "yyyy.MM.dd"), "yyyy.MM.dd");
            } catch (Exception e) {
                throw new GeneralException("处理起始日期格式错误,请输入正确的日期格式！\nyyyy.MM.dd");
            }

            try {
                end_date = DateUtils.format(DateUtils.getDate(end_date, "yyyy.MM.dd"), "yyyy.MM.dd");
            } catch (Exception e) {
                throw new GeneralException("处理结束日期格式错误，,请输入正确的日期格式！\nyyyy.MM.dd");
            }
            
            //szk20131119
            if(DateUtils.getDate(start_date, "yyyy.MM.dd").after(DateUtils.getDate(end_date, "yyyy.MM.dd")) )
                throw new GeneralException("结束时间不能小于开始时间！");

            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            boolean isDateExcuse = kqUtilsClass.isExcuseCurKqDate(start_date, end_date);// 处理时间包含在考勤期间范围内
            if (!isDateExcuse) {
                throw new GeneralException("处理时间范围应在当前考勤期间范围内！");
            }

            KqParameter para = new KqParameter(this.userView, code, this.getFrameconn());
            HashMap hashmap = para.getKqParamterMap();
            String kq_type = (String) hashmap.get("kq_type");
            String kq_cardno = (String) hashmap.get("cardno");
            String kq_Gno = (String) hashmap.get("g_no");
            String dataUpdateType = "x";// 不更新q03表
            String analysBase = "all";

            ArrayList kq_dbase_list = this.userView.getPrivDbList();
            if (kind == null || kind.length() <= 0 || code == null || code.length() <= 0) {
                kind = RegisterInitInfoData.getKindValue(kind, this.userView);
                code = "";
                kq_dbase_list = kqUtilsClass.setKqPerList("", "2");
            } else {
                if ("-1".equals(kind)) {
                    String a0100 = a_code.substring(2);
                    String b0110 = getB0110ForA0100(nbase, a0100);
                    kq_dbase_list = kqUtilsClass.setKqPerList(b0110, "2");
                } else
                    kq_dbase_list = kqUtilsClass.setKqPerList(code, kind);
            }

            DataProcedureAnalyse dataProcedureAnalyse = new DataProcedureAnalyse(this.getFrameconn(), this.userView, analyseType, kq_type, kq_cardno, kq_Gno, dataUpdateType, kq_dbase_list);
            if ("0".equals(isAsync)) {
                dataProcedureAnalyse.dataAnalys(code, kind, start_date, end_date, analysBase); // 走数据处理class
                String fAnalyseTempTab = dataProcedureAnalyse.getFAnalyseTempTab(); // 数据处理表
                String fExceptCardTab = dataProcedureAnalyse.getFExceptCardTab(); // 临时异常表的名称
                String fTranOverTimeTab = dataProcedureAnalyse.getFTranOverTimeTab(); // 临时延时加班表
                String fBusiCompareTab = dataProcedureAnalyse.getFBusiCompareTab(); // 申请比对表
                String fCardToOverTimeTab = dataProcedureAnalyse.getCardToOverTime(); // 休息日转加班
                this.getFormHM().put("start_date", start_date);
                this.getFormHM().put("end_date", end_date);
                this.getFormHM().put("analyseTempTab", fAnalyseTempTab);// 分析结果表
                this.getFormHM().put("exceptCardTab", fExceptCardTab);// 异常刷卡
                this.getFormHM().put("tranOverTimeTab", fTranOverTimeTab);// 延时加班
                this.getFormHM().put("busiCompareTab", fBusiCompareTab);// 申请比对
                this.getFormHM().put("cardToOverTimeTab", fCardToOverTimeTab);// 休息日转加班
                this.getFormHM().put("kq_type", kq_type);
                this.getFormHM().put("kq_cardno", kq_cardno);
            } else {
                HashMap params = new HashMap();
                params.put("code", code);
                params.put("kind", kind);
                params.put("start_date", start_date);
                params.put("end_date", end_date);
                params.put("analysBase", analysBase);
                dataProcedureAnalyse.setDataAnayseParams(params);
                new Thread(dataProcedureAnalyse).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    private String getB0110ForA0100(String nbase, String a0100) {
        String b0110 = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rs = null;
        try {
            String sql = "select b0110 from " + nbase + "A01 where a0100='" + a0100 + "'";
            rs = dao.search(sql);
            if (rs.next()) {
                b0110 = rs.getString("b0110");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return b0110;
    }
}
