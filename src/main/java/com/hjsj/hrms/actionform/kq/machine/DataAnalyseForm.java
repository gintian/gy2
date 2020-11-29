package com.hjsj.hrms.actionform.kq.machine;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class DataAnalyseForm extends FrameForm {
    private String    treeCode;
    private String    strSql;
    private String    column;
    private ArrayList fieldList             = new ArrayList();
    private String    whereStr;
    private String    a_code;
    private String    nbase;
    private String    order;
    private String    temp_Table;
    private String    kq_type;
    private String    kq_cardno;
    private String    analyseTempTab;                         // 分析结果表
    private String    exceptCardTab;                          // 异常表
    private String    tranOverTimeTab;                        // 延时表
    private String    cardToOverTime;                         // 休息日转加班
    private String    busiCompareTab;                         // 申请表
    private String    start_date;                             // 开始时间
    private String    start_hh;
    private String    start_mm;
    private String    end_date;                               // 结束时间
    private String    end_hh;
    private String    end_mm;
    private ArrayList kq_dbase_list         = new ArrayList();
    private ArrayList kq_list               = new ArrayList(); // 人员库
    private String    select_name;                            // 筛选名字
    private String    select_flag;                            // 筛选表示
    private String    select_type           = "0";
    private String    select_pre;
    private String    inout_str;
    private String    cardno_len;                             // 考勤卡长度
    private String    sp_flag;                                // 审批标志
    private String    analyse_type;                           // 分析类型:result:分析结果表;except:异常表;overtime:延时加班;busicompare:申请业务对比；
    private String    app_type;                               // 申请类型
    private ArrayList applist               = new ArrayList();
    private String    busi_filtrate;                          // 时长过滤
    private String    busi_analyse_flag;                      // 申请比对处理方式private
    private String    view = "";
    private ArrayList outfieldlist          = new ArrayList();
    private String    outfieldsname         = "";
    private String    uplevel;
    /** 补刷卡 **/
    private String    sqlstrpatch;
    private String    columnpatch;
    private String    orderpatch;
    private String    repair_flag;
    private String    statr_date;
    private String    end_date_patch;
    private String    card_causation;                         // 补刷原因
    private String    repair_fashion;                         // 0:简单；1：复杂
    private String    into_flag;                              // 补签进出标示
    private String    iscommon;								  // 签到点标志
    private String    signs;                                   // 标记
    private String    causation;                              // 补卡原因
    private String    jddate;                                 // 简单规则补刷时间
    private String    easy_hh;
    private String    easy_mm;
    private String    class_flag;                             // 0:上下全补；1：只补上班；3：只补下班
    private String    ip_adr;
    private String    cycle_date;                             // 复杂规则循环时间
    private String    cycle_hh;                               // 复杂规则
    private String    cycle_num;                              // 复杂规则
    private String    cycle_mm;                               // 复杂规则
    private String    specdata;
    private String    returnvalue           = "1";
    private String    lockedNum;

    //延时加班走人事异动
    private ArrayList overtimeTemplates     = null;
    private String    overtimeTemplateId    = "";
    private boolean   haveOvertimeTemplates = false;

    //延时加班审批状态选项
    private ArrayList overtimeSpList        = null;
    private String    overtimeSpState       = "";

    public String getLockedNum() {
        return lockedNum;
    }

    public void setLockedNum(String lockedNum) {
        this.lockedNum = lockedNum;
    }

    public String getBusi_analyse_flag() {
        return busi_analyse_flag;
    }

    public void setBusi_analyse_flag(String busi_analyse_flag) {
        this.busi_analyse_flag = busi_analyse_flag;
    }

    public String getAnalyse_type() {
        return analyse_type;
    }

    public void setAnalyse_type(String analyse_type) {
        this.analyse_type = analyse_type;
    }

    public String getKq_cardno() {
        return kq_cardno;
    }

    public void setKq_cardno(String kq_cardno) {
        this.kq_cardno = kq_cardno;
    }

    public String getKq_type() {
        return kq_type;
    }

    public void setKq_type(String kq_type) {
        this.kq_type = kq_type;
    }

    public String getTemp_Table() {
        return temp_Table;
    }

    public void setTemp_Table(String temp_Table) {
        this.temp_Table = temp_Table;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getA_code() {
        return a_code;
    }

    public void setA_code(String a_code) {
        this.a_code = a_code;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public ArrayList getFieldList() {
        return fieldList;
    }

    public void setFieldList(ArrayList fieldList) {
        this.fieldList = fieldList;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStrSql() {
        return strSql;
    }

    public void setStrSql(String strSql) {
        this.strSql = strSql;
    }

    public String getWhereStr() {
        return whereStr;
    }

    public void setWhereStr(String whereStr) {
        this.whereStr = whereStr;
    }

    @Override
    public void outPutFormHM() {
        this.setStrSql((String) this.getFormHM().get("sqlstr"));
        this.setWhereStr((String) this.getFormHM().get("whereStr"));
        this.setColumn((String) this.getFormHM().get("column"));
        this.setFieldList((ArrayList) this.getFormHM().get("fieldList"));
        this.setStart_date((String) this.getFormHM().get("start_date"));
        this.setStart_hh((String) this.getFormHM().get("start_hh"));
        this.setStart_mm((String) this.getFormHM().get("start_mm"));
        this.setEnd_date((String) this.getFormHM().get("end_date"));
        this.setEnd_hh((String) this.getFormHM().get("end_hh"));
        this.setEnd_mm((String) this.getFormHM().get("end_mm"));
        this.setA_code((String) this.getFormHM().get("a_code"));
        this.setNbase((String) this.getFormHM().get("nbase"));
        this.setOrder((String) this.getFormHM().get("order"));
        this.setTemp_Table((String) this.getFormHM().get("temp_Table"));
        this.setKq_cardno((String) this.getFormHM().get("kq_cardno"));
        this.setKq_type((String) this.getFormHM().get("kq_type"));
        this.setTreeCode((String) this.getFormHM().get("treeCode"));
        this.setAnalyseTempTab((String) this.getFormHM().get("analyseTempTab"));
        this.setExceptCardTab((String) this.getFormHM().get("exceptCardTab"));
        this.setTranOverTimeTab((String) this.getFormHM().get("tranOverTimeTab"));
        this.setBusiCompareTab((String) this.getFormHM().get("busiCompareTab"));
        this.setKq_list((ArrayList) this.getFormHM().get("kq_list"));
        this.setSelect_name((String) this.getFormHM().get("select_name"));
        this.setSelect_flag((String) this.getFormHM().get("select_flag"));
        this.setSelect_pre((String) this.getFormHM().get("select_pre"));
        this.setKq_dbase_list((ArrayList) this.getFormHM().get("kq_dbase_list"));
        this.setAnalyse_type((String) this.getFormHM().get("analyse_type"));
        this.setApp_type((String) this.getFormHM().get("app_type"));
        this.setBusi_filtrate((String) this.getFormHM().get("busi_filtrate"));
        this.setBusi_analyse_flag((String) this.getFormHM().get("busi_analyse_flag"));
        this.setApplist((ArrayList) this.getFormHM().get("applist"));
        this.setOutfieldlist((ArrayList) this.getFormHM().get("outfieldlist"));
        this.setOutfieldsname((String) this.getFormHM().get("outfieldsname"));
        this.setView((String) this.getFormHM().get("view"));
        this.setUplevel((String) this.getFormHM().get("uplevel"));
        this.setSqlstrpatch((String) this.getFormHM().get("sqlstrpatch"));
        this.setColumnpatch((String) this.getFormHM().get("columnpatch"));
        this.setOrderpatch((String) this.getFormHM().get("orderpatch"));
        this.setRepair_flag((String) this.getFormHM().get("repair_flag"));
        this.setStatr_date((String) this.getFormHM().get("statr_date"));
        this.setEnd_date_patch((String) this.getFormHM().get("end_date_patch"));
        this.setCard_causation((String) this.getFormHM().get("card_causation"));
        this.setRepair_fashion((String) this.getFormHM().get("repair_fashion"));
        this.setInto_flag((String) this.getFormHM().get("into_flag"));
        this.setCausation((String) this.getFormHM().get("causation"));
        this.setJddate((String) this.getFormHM().get("jddate"));
        this.setClass_flag((String) this.getFormHM().get("class_flag"));
        this.setIp_adr((String) this.getFormHM().get("ip_adr"));
        this.setCycle_date((String) this.getFormHM().get("cycle_date"));
        this.setCycle_hh((String) this.getFormHM().get("cycle_hh"));
        this.setCycle_num((String) this.getFormHM().get("cycle_num"));
        this.setCycle_mm((String) this.getFormHM().get("cycle_mm"));
        this.setSpecdata((String) this.getFormHM().get("specdata"));
        this.setLockedNum((String) this.getFormHM().get("lockedNum"));
        this.setSelect_type((String) this.getFormHM().get("select_type"));
        this.setCardToOverTime((String) this.getFormHM().get("cardToOverTime"));
        this.setOvertimeTemplates((ArrayList) this.getFormHM().get("overtime_templates"));
        this.setOvertimeTemplateId((String) this.getFormHM().get("overtimeTemplateId"));
        this.setOvertimeSpList((ArrayList) this.getFormHM().get("overtimeSpList"));
        this.setOvertimeSpState((String) this.getFormHM().get("overtimeSpState"));
        this.setSigns((String) this.getFormHM().get("signs"));
    }

    @Override
    public void inPutTransHM() {
        this.getFormHM().put("analyse_type", this.getAnalyse_type());
        this.getFormHM().put("a_code", this.getA_code());
        this.getFormHM().put("start_date", this.getStart_date());
        this.getFormHM().put("start_hh", this.getStart_hh());
        this.getFormHM().put("start_mm", this.getStart_mm());
        this.getFormHM().put("end_date", this.getEnd_date());
        this.getFormHM().put("end_hh", this.getEnd_hh());
        this.getFormHM().put("end_mm", this.getEnd_mm());
        this.getFormHM().put("nbase", this.getNbase());
        this.getFormHM().put("strSql", this.getStrSql());
        this.getFormHM().put("whereStr", this.getWhereStr());
        this.getFormHM().put("column", this.getColumn());
        this.getFormHM().put("fieldList", this.getFieldList());
        this.getFormHM().put("temp_Table", this.getTemp_Table());
        if (this.getPagination() != null)
            this.getFormHM().put("selectedinfolist", (ArrayList) this.getPagination().getSelectedList());
        this.getFormHM().put("kq_cardno", this.getKq_cardno());
        this.getFormHM().put("kq_type", this.getKq_type());

        this.getFormHM().put("fAnalyseTempTab", this.getAnalyseTempTab());
        this.getFormHM().put("fExceptCardTab", this.getExceptCardTab());
        this.getFormHM().put("fTranOverTimeTab", this.getTranOverTimeTab());
        this.getFormHM().put("fBusiCompareTab", this.getBusiCompareTab());
        this.getFormHM().put("select_name", this.getSelect_name());
        this.getFormHM().put("select_flag", this.getSelect_flag());
        this.getFormHM().put("select_pre", this.getSelect_pre());
        this.getFormHM().put("sp_flag", this.getSp_flag());
        this.getFormHM().put("app_type", this.getApp_type());
        this.getFormHM().put("busi_filtrate", this.getBusi_filtrate());
        this.getFormHM().put("busi_analyse_flag", this.getBusi_analyse_flag());
        this.getFormHM().put("view", this.getView());
        this.getFormHM().put("sqlstrpatch", this.getSqlstrpatch());
        this.getFormHM().put("columnpatch", this.getColumnpatch());
        this.getFormHM().put("repair_flag", this.getRepair_flag());
        this.getFormHM().put("statr_date", this.getStatr_date());
        this.getFormHM().put("end_date_patch", this.getEnd_date_patch());
        this.getFormHM().put("card_causation", this.getCard_causation());
        this.getFormHM().put("repair_fashion", repair_fashion);
        this.getFormHM().put("into_flag", this.getInto_flag());
        this.getFormHM().put("iscommon", this.getIscommon());
        this.getFormHM().put("causation", this.getCausation());
        this.getFormHM().put("jddate", this.getJddate());
        this.getFormHM().put("class_flag", this.getClass_flag());
        this.getFormHM().put("easy_hh", this.getEasy_hh());
        this.getFormHM().put("easy_mm", this.getEasy_mm());
        this.getFormHM().put("ip_adr", this.getIp_adr());
        this.getFormHM().put("cycle_date", this.getCycle_date());
        this.getFormHM().put("cycle_hh", this.getCycle_hh());
        this.getFormHM().put("cycle_num", this.getCycle_num());
        this.getFormHM().put("cycle_mm", this.getCycle_mm());
        this.getFormHM().put("specdata", this.getSpecdata());
        this.getFormHM().put("select_type", this.getSelect_type());
        this.getFormHM().put("cardToOverTime", this.getCardToOverTime());
        this.getFormHM().put("overtime_templates", this.getOvertimeTemplates());
        this.getFormHM().put("overtimeTemplateId", this.getOvertimeTemplateId());
        this.getFormHM().put("overtimeSpList", this.getOvertimeSpList());
        this.getFormHM().put("overtimeSpState", this.getOvertimeSpState());
        this.getFormHM().put("signs", this.getSigns());
    }

    public String getNbase() {
        return nbase;
    }

    public void setNbase(String nbase) {
        this.nbase = nbase;
    }

    private void resetPagination() {
        if (this.getPagination() != null) {
            this.getPagination().firstPage();
            this.pagerows = 20;
        }
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if ("/kq/machine/analyse_card".equals(arg0.getPath()) && arg1.getParameter("b_analyse") != null) {
            resetPagination();
        }
        if ("/kq/machine/analyse/data_analyse_data".equals(arg0.getPath()) && arg1.getParameter("b_analyse") != null) {
            resetPagination();
        }
        if ("/kq/machine/analyse/data_analyse_data".equals(arg0.getPath()) && arg1.getParameter("b_query") != null) {
            resetPagination();
            this.getFormHM().put("a_code", "");
            this.setA_code("");
        }
        if ("/kq/machine/analyse/data_analyse".equals(arg0.getPath()) && arg1.getParameter("b_query") != null) {
            resetPagination();
            this.getFormHM().put("a_code", "");
            this.setA_code("");
            this.setSelect_pre("");
            this.getFormHM().put("select_pre", "");
        }
        if ("/kq/machine/analyse/analyse_result".equals(arg0.getPath()) && arg1.getParameter("b_search") != null) {
            resetPagination();
            this.setAnalyse_type("result");// 分析结果表
            this.getFormHM().put("analyse_type", this.getAnalyse_type());
        }
        if ("/kq/machine/analyse/exceptcard".equals(arg0.getPath()) && arg1.getParameter("b_search") != null) {
            resetPagination();
            this.setAnalyse_type("except");// :异常表
            this.getFormHM().put("analyse_type", this.getAnalyse_type());
        }
        if ("/kq/machine/analyse/tranovertime".equals(arg0.getPath()) && arg1.getParameter("b_search") != null) {
            resetPagination();
            this.setAnalyse_type("overtime");// 延时加班
            this.getFormHM().put("analyse_type", this.getAnalyse_type());
        }
        if ("/kq/machine/analyse/busicompare".equals(arg0.getPath()) && arg1.getParameter("b_search") != null) {
            resetPagination();
            this.setAnalyse_type("busicompare");// 申请业务对比
            this.getFormHM().put("analyse_type", this.getAnalyse_type());
            this.setBusi_analyse_flag("1");
            /* this.setBusi_filtrate("0"); */
        }
        if ("/kq/machine/analyse/cardtoovertime".equals(arg0.getPath()) && arg1.getParameter("b_search") != null) {
            resetPagination();
            this.setAnalyse_type("cardtoovertime");// 休息日转加班
            this.getFormHM().put("analyse_type", this.getAnalyse_type());
            this.setBusi_analyse_flag("1");

            /* this.setBusi_filtrate("0"); */
        }
        if ("/kq/machine/analyse/analyse_patch".equals(arg0.getPath()) && arg1.getParameter("b_patch") != null) {
            resetPagination();
        }
        
        if ("/kq/machine/analyse/analyse_result".equals(arg0.getPath()) && arg1.getParameter("b_patch") != null) {
            if (this.getPagination() != null) {
                this.getPagination().firstPage();
                this.pagerows = 4999;
            }
        }
        
        return super.validate(arg0, arg1);
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }

    public String getAnalyseTempTab() {
        return analyseTempTab;
    }

    public void setAnalyseTempTab(String analyseTempTab) {
        this.analyseTempTab = analyseTempTab;
    }

    public String getBusiCompareTab() {
        return busiCompareTab;
    }

    public void setBusiCompareTab(String busiCompareTab) {
        this.busiCompareTab = busiCompareTab;
    }

    public String getExceptCardTab() {
        return exceptCardTab;
    }

    public void setExceptCardTab(String exceptCardTab) {
        this.exceptCardTab = exceptCardTab;
    }

    public String getTranOverTimeTab() {
        return tranOverTimeTab;
    }

    public void setTranOverTimeTab(String tranOverTimeTab) {
        this.tranOverTimeTab = tranOverTimeTab;
    }

    public String getEnd_hh() {
        return end_hh;
    }

    public void setEnd_hh(String end_hh) {
        this.end_hh = end_hh;
    }

    public String getEnd_mm() {
        return end_mm;
    }

    public void setEnd_mm(String end_mm) {
        this.end_mm = end_mm;
    }

    public String getStart_hh() {
        return start_hh;
    }

    public void setStart_hh(String start_hh) {
        this.start_hh = start_hh;
    }

    public String getStart_mm() {
        return start_mm;
    }

    public void setStart_mm(String start_mm) {
        this.start_mm = start_mm;
    }

    public String getCardno_len() {
        return cardno_len;
    }

    public void setCardno_len(String cardno_len) {
        this.cardno_len = cardno_len;
    }

    public String getInout_str() {
        return inout_str;
    }

    public void setInout_str(String inout_str) {
        this.inout_str = inout_str;
    }

    public ArrayList getKq_dbase_list() {
        return kq_dbase_list;
    }

    public void setKq_dbase_list(ArrayList kq_dbase_list) {
        this.kq_dbase_list = kq_dbase_list;
    }

    public ArrayList getKq_list() {
        return kq_list;
    }

    public void setKq_list(ArrayList kq_list) {
        this.kq_list = kq_list;
    }

    public String getSelect_flag() {
        return select_flag;
    }

    public void setSelect_flag(String select_flag) {
        this.select_flag = select_flag;
    }

    public String getSelect_name() {
        return select_name;
    }

    public void setSelect_name(String select_name) {
        this.select_name = select_name;
    }

    public String getSelect_pre() {
        return select_pre;
    }

    public void setSelect_pre(String select_pre) {
        this.select_pre = select_pre;
    }

    public String getSp_flag() {
        return sp_flag;
    }

    public void setSp_flag(String sp_flag) {
        this.sp_flag = sp_flag;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public String getBusi_filtrate() {
        return busi_filtrate;
    }

    public void setBusi_filtrate(String busi_filtrate) {
        this.busi_filtrate = busi_filtrate;
    }

    public ArrayList getApplist() {
        return applist;
    }

    public void setApplist(ArrayList applist) {
        this.applist = applist;
    }

    public ArrayList getOutfieldlist() {
        return outfieldlist;
    }

    public void setOutfieldlist(ArrayList outfieldlist) {
        this.outfieldlist = outfieldlist;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getOutfieldsname() {
        return outfieldsname;
    }

    public void setOutfieldsname(String outfieldsname) {
        this.outfieldsname = outfieldsname;
    }

    public String getUplevel() {
        return uplevel;
    }

    public void setUplevel(String uplevel) {
        this.uplevel = uplevel;
    }

    public String getSqlstrpatch() {
        return sqlstrpatch;
    }

    public void setSqlstrpatch(String sqlstrpatch) {
        this.sqlstrpatch = sqlstrpatch;
    }

    public String getColumnpatch() {
        return columnpatch;
    }

    public void setColumnpatch(String columnpatch) {
        this.columnpatch = columnpatch;
    }

    public String getOrderpatch() {
        return orderpatch;
    }

    public void setOrderpatch(String orderpatch) {
        this.orderpatch = orderpatch;
    }

    public String getRepair_flag() {
        return repair_flag;
    }

    public void setRepair_flag(String repair_flag) {
        this.repair_flag = repair_flag;
    }

    public String getStatr_date() {
        return statr_date;
    }

    public void setStatr_date(String statr_date) {
        this.statr_date = statr_date;
    }

    public String getEnd_date_patch() {
        return end_date_patch;
    }

    public void setEnd_date_patch(String end_date_patch) {
        this.end_date_patch = end_date_patch;
    }

    public String getCard_causation() {
        return card_causation;
    }

    public void setCard_causation(String card_causation) {
        this.card_causation = card_causation;
    }

    public String getRepair_fashion() {
        return repair_fashion;
    }

    public void setRepair_fashion(String repair_fashion) {
        this.repair_fashion = repair_fashion;
    }

    public String getInto_flag() {
        return into_flag;
    }

    public void setInto_flag(String into_flag) {
        this.into_flag = into_flag;
    }

    public String getCausation() {
        return causation;
    }

    public void setCausation(String causation) {
        this.causation = causation;
    }

    public String getJddate() {
        return jddate;
    }

    public void setJddate(String jddate) {
        this.jddate = jddate;
    }

    public String getClass_flag() {
        return class_flag;
    }

    public void setClass_flag(String class_flag) {
        this.class_flag = class_flag;
    }

    public String getEasy_hh() {
        return easy_hh;
    }

    public void setEasy_hh(String easy_hh) {
        this.easy_hh = easy_hh;
    }

    public String getEasy_mm() {
        return easy_mm;
    }

    public void setEasy_mm(String easy_mm) {
        this.easy_mm = easy_mm;
    }

    public String getIp_adr() {
        return ip_adr;
    }

    public void setIp_adr(String ip_adr) {
        this.ip_adr = ip_adr;
    }

    public String getCycle_date() {
        return cycle_date;
    }

    public void setCycle_date(String cycle_date) {
        this.cycle_date = cycle_date;
    }

    public String getCycle_hh() {
        return cycle_hh;
    }

    public void setCycle_hh(String cycle_hh) {
        this.cycle_hh = cycle_hh;
    }

    public String getCycle_num() {
        return cycle_num;
    }

    public void setCycle_num(String cycle_num) {
        this.cycle_num = cycle_num;
    }

    public String getCycle_mm() {
        return cycle_mm;
    }

    public void setCycle_mm(String cycle_mm) {
        this.cycle_mm = cycle_mm;
    }

    public String getSpecdata() {
        return specdata;
    }

    public void setSpecdata(String specdata) {
        this.specdata = specdata;
    }

    public String getReturnvalue() {
        return returnvalue;
    }

    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }

    public String getSelect_type() {
        return select_type;
    }

    public void setSelect_type(String select_type) {
        this.select_type = select_type;
    }

    public String getCardToOverTime() {
        return cardToOverTime;
    }

    public void setCardToOverTime(String cardToOverTime) {
        this.cardToOverTime = cardToOverTime;
    }

    public void setOvertimeTemplates(ArrayList overtimeTemplates) {
        this.overtimeTemplates = overtimeTemplates;
        if (overtimeTemplates != null && overtimeTemplates.size() > 0)
            this.setHaveOvertimeTemplates(true);
        else
            this.setHaveOvertimeTemplates(false);
    }

    public ArrayList getOvertimeTemplates() {
        return overtimeTemplates;
    }

    public void setOvertimeTemplateId(String overtimeTemplateId) {
        this.overtimeTemplateId = overtimeTemplateId;
    }

    public String getOvertimeTemplateId() {
        return overtimeTemplateId;
    }

    public void setHaveOvertimeTemplates(boolean haveOvertimeTemplates) {
        this.haveOvertimeTemplates = haveOvertimeTemplates;
    }

    public boolean isHaveOvertimeTemplates() {
        return haveOvertimeTemplates;
    }

    public void setOvertimeSpList(ArrayList overtimeSpList) {
        this.overtimeSpList = overtimeSpList;
    }

    public ArrayList getOvertimeSpList() {
        return overtimeSpList;
    }

    public void setOvertimeSpState(String overtimeSpState) {
        this.overtimeSpState = overtimeSpState;
    }

    public String getOvertimeSpState() {
        return overtimeSpState;
    }

	public void setIscommon(String iscommon) {
		this.iscommon = iscommon;
	}

	public String getIscommon() {
		return iscommon;
	}

	public void setSigns(String signs) {
		this.signs = signs;
	}

	public String getSigns() {
		return signs;
	}
}
