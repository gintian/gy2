package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.machine.DataAnalyse;
import com.hjsj.hrms.businessobject.kq.machine.DateAnalyseImp;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 刷卡数据数据分析
 * <p>Title:DataAnalyseTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 30, 2007 1:59:55 PM</p>
 * @author sunxin
 * @version 1.0
 *
 * WARNING!!! 此交易类毫无用处，已作废，请不要使用！！！
 * @moidfy zhaoxj 2013-08-27
 */
public class DataAnalyseTrans extends IBusiness implements DateAnalyseImp {

    public void execute() throws GeneralException {
        try {
            String a_code = (String) this.getFormHM().get("a_code");
            String nbase = (String) this.getFormHM().get("nbase");
            String start_date = (String) this.getFormHM().get("start_date");
            String end_date = (String) this.getFormHM().get("end_date");
            String analyseType = "1";
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
            start_date = start_date.replaceAll("-", "\\.");
            end_date = end_date.replaceAll("-", "\\.");
            if (start_date == null || start_date.length() <= 0)
                throw new GeneralException("", "处理起始时间不能为空！");
            
            if (end_date == null || end_date.length() <= 0)
                throw new GeneralException("", "处理结束时间不能为空！");
            
            try {
                start_date = DateUtils.format(DateUtils.getDate(start_date, "yyyy.MM.dd"), "yyyy.MM.dd");
            } catch (Exception e) {
                throw new GeneralException("处理起始时间错误！");
            }
            try {
                end_date = DateUtils.format(DateUtils.getDate(end_date, "yyyy.MM.dd"), "yyyy.MM.dd");
            } catch (Exception e) {
                throw new GeneralException("处理结束时间错误！");
            }
            KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, code, this.getFrameconn());
            String kq_type = kq_paramter.getKq_type();
            String kq_cardno = kq_paramter.getCardno();
            String dataUpdateType = "0";
            DataAnalyse dataAnalyse = new DataAnalyse(this.getFrameconn(), this.userView, analyseType, kq_type, kq_cardno,
                    dataUpdateType, this.userView.getPrivDbList());
            ArrayList fieldlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            dataAnalyse.setColumnlist(fieldlist);
            if (1 == 1) {
                throw new GeneralException("文件已丢失，请联系管理员！");
            }

            String column = dataAnalyse.getColumn();
            ArrayList fieldList = dataAnalyse.getFieldList();
            String strSql = dataAnalyse.getStrSql();
            String whereStr = dataAnalyse.getWhereStr();
            String temp_Table = dataAnalyse.getTemp_Table();
            this.getFormHM().put("strSql", strSql);
            this.getFormHM().put("whereStr", whereStr);
            this.getFormHM().put("fieldList", fieldList);
            this.getFormHM().put("column", column);
            this.getFormHM().put("order", dataAnalyse.getOrderBy());
            this.getFormHM().put("temp_Table", temp_Table);
            this.getFormHM().put("kq_type", kq_type);
            this.getFormHM().put("kq_cardno", kq_cardno);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
