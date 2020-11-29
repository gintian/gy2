package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.CountMoInfo;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class CollectRegisterTrans extends IBusiness {
    /**
     * 汇总日考期日考勤明细表
     * 
     * */
    private String error_return = "/kq/register/daily_register.do?b_search=link&action=daily_registerdata.do";

    public void execute() throws GeneralException {
        String error_message = "";
        String error_flag = "0";
        boolean isCorrect = true;
        try {
            String code = (String) this.getFormHM().get("code");
            String kind = (String) this.getFormHM().get("kind");
            code = code.trim();
            if (code == null || code.length() <= 0) {
                code = "";
            }
            String kq_duration = RegisterDate.getKqDuration(this.getFrameconn());
            this.getFormHM().put("validate", "");
            ArrayList datelist = (ArrayList) this.getFormHM().get("datelist");

            CommonData vo_date = (CommonData) datelist.get(0);
            String start_date = vo_date.getDataValue();
            vo_date = (CommonData) datelist.get(datelist.size() - 1);
            String end_date = vo_date.getDataValue();

            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);

            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList dblist = kqUtilsClass.getKqPreList();

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            
            KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
            HashMap hashmap = para.getKqParamterMap();
            String kq_type = (String) hashmap.get("kq_type");// 考勤方式字段
            
            StringBuffer statcolumn = new StringBuffer();
            StringBuffer insertcolumn = new StringBuffer();
            StringBuffer un_statcolumn = new StringBuffer();
            StringBuffer un_insertcolumn = new StringBuffer();
            DbWizard dbWizard = new DbWizard(this.frameconn);
            String sdao_count_field = SystemConfig.getPropertyValue("sdao_count_field"); // 得到上岛标识 对应的字段
            // String retud=gettichu(sdao_count_field,dao);
            /*
             * 首钢 上岛标识 不在考勤规则里，但是月统计还需要计算进来；这里过滤一下
             */
            if ("".equals(sdao_count_field) || sdao_count_field.length() < 0) {
                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    // 类型为N的时候，如果指标为主集中的指标也不能计算
                    // boolean
                    // booindex=getindexA01(fielditem.getItemtype(),fielditem.getItemid(),fielditem.getItemdesc());
                    {
                        if ("N".equals(fielditem.getItemtype())) {

                            if (!"i9999".equals(fielditem.getItemid())) {
                                int want_sum = CollectRegister.getWant_Sum(fielditem.getItemid(), this.getFrameconn());

                                if (want_sum == 1) {
                                    statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                    insertcolumn.append("" + fielditem.getItemid() + ",");

                                }
                                un_statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                un_insertcolumn.append("" + fielditem.getItemid() + ",");
                            }
                        }
                    }

                }
            } else {
                if (dbWizard.isExistField("Q03", sdao_count_field.toLowerCase(), false)) {
                    for (int i = 0; i < fielditemlist.size(); i++) {
                        FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                        // 类型为N的时候，如果指标为主集中的指标也不能计算
                        // boolean
                        // booindex=getindexA01(fielditem.getItemtype(),fielditem.getItemid(),fielditem.getItemdesc());
                        {
                            if ("N".equals(fielditem.getItemtype())) {

                                if (!"i9999".equals(fielditem.getItemid())) {
                                    int want_sum = CollectRegister.getWant_Sum(fielditem.getItemid(), this.getFrameconn());

                                    if (want_sum == 1 || sdao_count_field.equalsIgnoreCase(fielditem.getItemid())) {
                                        statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                        insertcolumn.append("" + fielditem.getItemid() + ",");

                                    }
                                    un_statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                    un_insertcolumn.append("" + fielditem.getItemid() + ",");
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < fielditemlist.size(); i++) {
                        FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                        // 类型为N的时候，如果指标为主集中的指标也不能计算
                        // boolean
                        // booindex=getindexA01(fielditem.getItemtype(),fielditem.getItemid(),fielditem.getItemdesc());
                        {
                            if ("N".equals(fielditem.getItemtype())) {

                                if (!"i9999".equals(fielditem.getItemid())) {
                                    int want_sum = CollectRegister.getWant_Sum(fielditem.getItemid(), this.getFrameconn());

                                    if (want_sum == 1) {
                                        statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                        insertcolumn.append("" + fielditem.getItemid() + ",");

                                    }
                                    un_statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                                    un_insertcolumn.append("" + fielditem.getItemid() + ",");
                                }
                            }
                        }
                    }
                }
            }
            String statcolumnstr = "";
            String insertcolumnstr = "";
            if (statcolumn.toString() != null & statcolumn.toString().length() > 0) {
                int l = statcolumn.toString().length() - 1;
                statcolumnstr = statcolumn.toString().substring(0, l);
                l = insertcolumn.toString().length() - 1;
                insertcolumnstr = insertcolumn.toString().substring(0, l);
            } else {
                int l = un_statcolumn.toString().length() - 1;
                statcolumnstr = un_statcolumn.toString().substring(0, l);
                l = un_insertcolumn.toString().length() - 1;
                insertcolumnstr = un_insertcolumn.toString().substring(0, l);
            }

            String field = KqParam.getInstance().getKqDepartment();// 考勤部门

            String kq_period = CollectRegister.getMonthRegisterDate(start_date, end_date);
            CollectRegister collectRegister = new CollectRegister();
            collectRegister.setConn(this.frameconn);

            DataProcedureAnalyse dAnalyse = new DataProcedureAnalyse(frameconn, userView);
            String mainindex = dAnalyse.getmainsql();
            String mainindex1 = dAnalyse.getmainsql2();

            //考勤管理范围机构编码
            String kqDeptCode = RegisterInitInfoData.getKqPrivCodeValue(userView);
            
            for (int r = 0; r < dblist.size(); r++) {
                String base = dblist.get(r).toString();

                String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, base);
                if ("".equals(code)) {
                    if (!userView.isSuper_admin()) {
                        if (whereIN.indexOf("WHERE") != -1) {
                            whereIN = whereIN.replace("WHERE", "WHERE (");
                            if (field != null && field.length() > 0 && !"".equals(kqDeptCode))
                                whereIN += " OR " + base + "A01." + field + " like '"
                                        + RegisterInitInfoData.getKqPrivCodeValue(userView) + "%'";
                            whereIN += ")";
                        } else {
                            whereIN = whereIN.replace("WHERE", "WHERE (");
                            if (field != null && field.length() > 0 && !"".equals(kqDeptCode))
                                whereIN += base + "A01." + field + " like '" + RegisterInitInfoData.getKqPrivCodeValue(userView)
                                        + "%'";
                            whereIN += ")";
                        }

                        String existsWhr = whereIN;
                        boolean if_delete = dAnalyse.delRecord(base, "", null, null, kq_duration, start_date, end_date, existsWhr);
                        if (if_delete) {
                            isCorrect = collectRegister.collectRecord2(dao, base, start_date, end_date, "", null, null,
                                    fielditemlist, existsWhr, kq_duration, kq_type, insertcolumnstr, statcolumnstr, kq_period,
                                    mainindex, mainindex1);
                        } else {
                            isCorrect = false;
                            error_message = ResourceFactory.getProperty("kq.register.collect.lost");
                            this.getFormHM().put("error_message", error_message);
                            this.getFormHM().put("error_return", this.error_return);
                            this.getFormHM().put("error_flag", "3");
                            return;
                        }
                    } else {
                        boolean if_delete = dAnalyse.delRecord(base, "", null, null, kq_duration, start_date, end_date, whereIN); // 超级用户用下面的
                        if (if_delete) {
                            isCorrect = collectRegister.collectRecord2(dao, base, start_date, end_date, "", null, null,
                                    fielditemlist, whereIN, kq_duration, kq_type, insertcolumnstr, statcolumnstr, kq_period,
                                    mainindex, mainindex1);
                        } else {
                            isCorrect = false;
                            error_message = ResourceFactory.getProperty("kq.register.collect.lost");
                            this.getFormHM().put("error_message", error_message);
                            this.getFormHM().put("error_return", this.error_return);
                            this.getFormHM().put("error_flag", "3");
                            return;
                        }
                    }
                } else {// 选单位汇总
                    boolean if_delete = dAnalyse.delRecord(base, code, kind, null, kq_duration, start_date, end_date, whereIN); // 超级用户用下面的
                    if (if_delete) {
                        String whereinSql = whereIN;
                        String kindField = "";
                        if ("1".equals(kind))
                            kindField = "e0122";
                        else if ("0".equals(kind))
                            kindField = "e01a1";
                        else if ("-1".equals(kind))
                            kindField = "a0100";
                        else
                            kindField = "b0110";
                        if (whereIN.contains("WHERE") || whereIN.contains("where")) {
                            whereinSql = whereinSql + " and " + kindField + " like '" + code + "%'";
                        } else {
                            whereinSql = whereinSql + " where " + kindField + " like '" + code + "%'";
                        }

                        isCorrect = collectRegister.collectRecord2(dao, base, start_date, end_date, code, kind, null,
                                fielditemlist, whereinSql, kq_duration, kq_type, insertcolumnstr, statcolumnstr, kq_period,
                                mainindex, mainindex1);
                    } else {
                        isCorrect = false;
                        error_message = ResourceFactory.getProperty("kq.register.collect.lost");
                        this.getFormHM().put("error_message", error_message);
                        this.getFormHM().put("error_return", this.error_return);
                        this.getFormHM().put("error_flag", "3");
                        return;
                    }

                }
            }
            
            KqUtilsClass.setIncludeA01ForLeadingInItem(false);
            kqUtilsClass.leadingInItemToQ05(dblist, start_date, end_date, "", "", kq_duration);// 加入导入项
            
            // 对月汇总进行计算
            CountMoInfo countMoInfo = new CountMoInfo(this.userView, this.getFrameconn());
            countMoInfo.setCode(code);
            countMoInfo.setKind(kind);
            countMoInfo.countKQInfo(kq_duration);
            /*************** 准备向员工月考勤表汇总 **********************/
        }catch(GeneralException e) {
            isCorrect = false;
            this.getFormHM().put("sp_result", e.getErrorDescription());
            this.getFormHM().put("error_flag","3");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(e);
            isCorrect = false;
            error_message = ResourceFactory.getProperty("kq.error.register.collect");
            this.getFormHM().put("error_message", error_message);
            this.getFormHM().put("error_return", this.error_return);
            this.getFormHM().put("error_flag", "3");
            return;
        }
        
        if (isCorrect) {
            this.getFormHM().put("sp_result", "数据汇总成功！");
        } else {
            this.getFormHM().put("sp_result", "数据汇总失败！");
        }
        
        this.getFormHM().put("error_flag", error_flag);
    }
}
