package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataAnalyseBusiness;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 数据处理的处理结果
 *<p>
 * Title:AnalyseResultTrans.java
 * </p>
 *<p>
 * Description:
 * </p>
 *<p>
 * Company:HJHJ
 * </p>
 *<p>
 * Create time:Oct 25, 2007
 * </p>
 * 
 * @author sunxin
 *@version 4.0
 */
public class AnalyseResultTrans extends IBusiness {
    private String tab_Name = "";

    public void execute() throws GeneralException {
        String analyse_type = (String) this.getFormHM().get("analyse_type");
        String a_code = (String) this.getFormHM().get("a_code");
        String start_date = (String) this.getFormHM().get("start_date");
        String start_hh = (String) this.getFormHM().get("start_hh");
        String start_mm = (String) this.getFormHM().get("start_mm");
        String end_date = (String) this.getFormHM().get("end_date");
        String end_hh = (String) this.getFormHM().get("end_hh");
        String end_mm = (String) this.getFormHM().get("end_mm");
        // String select_flag=(String)this.getFormHM().get("select_flag");
        String select_name = (String) this.getFormHM().get("select_name");
        String nbase = (String) this.getFormHM().get("nbase");
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String oneSotr = (String) hm.get("oneSort");
        hm.remove("oneSort");
        String checked = (String) hm.get("checked");
        String sort = (String) this.getFormHM().get("view");
        String sortStr = getSortItemStr(oneSotr,checked,sort);
        this.getFormHM().put("view", sortStr);
        
        // this.getFormHM().put("select_flag",select_flag);
        this.getFormHM().put("nbase", nbase);
        this.getFormHM().put("select_name", select_name);
        
        if (start_date == null || start_date.length() <= 0)
            start_date = PubFunc.getStringDate("yyyy.MM.dd");
        
        if (end_date == null || end_date.length() <= 0)
            end_date = PubFunc.getStringDate("yyyy.MM.dd");
        
        if (start_hh == null || start_hh.length() <= 0)
            start_hh = "00";
        
        if (start_mm == null || start_mm.length() <= 0)
            start_mm = "00";
        
        if (end_hh == null || end_hh.length() <= 0)
            end_hh = "23";
        
        if (end_mm == null || end_mm.length() <= 0)
            end_mm = "59";
        
        String start_time = start_hh + ":" + start_mm;
        String end_time = end_hh + ":" + end_mm;
        ArrayList kq_dbase_list = (ArrayList) this.getFormHM().get("kq_dbase_list");

        if (kq_dbase_list == null || kq_dbase_list.size() <= 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.analyse.no.nbase"), "", ""));

        String select_pre = (String) this.getFormHM().get("select_pre");
      

        if (a_code != null && a_code.length() > 0) {
            String codesetid = a_code.substring(0, 2);
            if ("EP".equalsIgnoreCase(codesetid)) {
                nbase = (String) hm.get("nbase");
                if (nbase != null && nbase.length() > 0)
                    select_pre = nbase;
            }
        }

        ArrayList sql_db_list = new ArrayList();
        if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
            sql_db_list.add(select_pre);
        } else {
            sql_db_list = kq_dbase_list;
        }

        this.getFormHM().put("select_pre", select_pre);
        
        String where_c = "";
        String sql = "";
        String column = "";
        ArrayList fieldlist = new ArrayList();
        int lockedNum = 0;
        
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
        String select_flag = (String) hm.get("select_flag");
        if ("1".equals(select_flag)) {
            where_c = kqUtilsClass.getWhere_C("1", "a0101", select_name);
            hm.remove("select_flag");
        }
        
        if (analyse_type == null || analyse_type.length() <= 0)
            analyse_type = "result";
        
        DataAnalyseBusiness analyseBusi = new DataAnalyseBusiness(this.getFrameconn(), this.userView, "kq");
        if ("result".equals(analyse_type))// 分析结果表
        {
            this.tab_Name = (String) this.getFormHM().get("fAnalyseTempTab");
            if (this.tab_Name == null)
                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.analyse.no.result.tab"), "", ""));
            
            ArrayList fieldList = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            analyseBusi.analyseResultBusi1(fieldList, this.tab_Name);
            
            if ("1".equals(select_flag)) {
                String select_type = (String) this.getFormHM().get("select_type");
                if ("0".equals(select_type)) {
                    where_c = kqUtilsClass.getWhere_C("1", "a0101", select_name);
                } else if ("1".equals(select_type)) {
                    if (analyseBusi.getG_no(this.tab_Name)) {
                        where_c = kqUtilsClass.getWhere_C("1", "g_no", select_name);
                    }
                } else if ("2".equals(select_type)) {
                    if (analyseBusi.getCard_no(this.tab_Name)) {
                        where_c = kqUtilsClass.getWhere_C("1", "card_no", select_name);
                    }
                }
                
                hm.remove("select_flag");
            }
            
            lockedNum = analyseBusi.getLockedNum();
            fieldlist = analyseBusi.getFieldList();// 指标list
            
            KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
            HashMap hashmap = para.getKqParamterMap();
            String kq_g_no = (String) hashmap.get("g_no");
            
            column = analyseBusi.getColumn();// 字段
            if (kqUtilsClass.isIntoField("Q03", kq_g_no)) {
                for (Iterator it = fieldlist.iterator(); it.hasNext();) {
                    FieldItem fi = (FieldItem) it.next();
                    if (fi.getItemid().equalsIgnoreCase(kq_g_no)) {
                        it.remove();
                    }
                }
            }
            
            sql = getAnalyeResUltTranSQL(sql_db_list, a_code, start_date, end_date, start_time, end_time, where_c, column, this.tab_Name, sortStr);
            this.getFormHM().put("analyseTempTab", this.tab_Name);
        } else if ("except".equals(analyse_type))// 异常表
        {
            this.tab_Name = (String) this.getFormHM().get("exceptCardTab");
            if (this.tab_Name == null)
                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.analyse.no.except.card.tab"), "", ""));
            
            analyseBusi.analyseExceptCard();
            fieldlist = analyseBusi.getFieldList();// 指标list
            column = analyseBusi.getColumn();// 字段
            sql = getAnalyeExceptCardSQL(sql_db_list, a_code, start_date, end_date, start_time, end_time, where_c, column);
            this.getFormHM().put("exceptCardTab", this.tab_Name);
        } else if ("overtime".equals(analyse_type))// 延时加班
        {
            this.tab_Name = (String) this.getFormHM().get("tranOverTimeTab");
            if (this.tab_Name == null)
                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.analyse.no.tran.overtime.tab"), "", ""));
            
            analyseBusi.analyseTranOverTimeTab(this.tab_Name);
            fieldlist = analyseBusi.getFieldList();// 指标list
            column = analyseBusi.getColumn();// 字段
            sql = getAnalyeOverTimeSQL(sql_db_list, a_code, start_date, end_date, start_time, end_time, where_c, column);
            
            this.getFormHM().put("tranOverTimeTab", this.tab_Name);
            putOverTimeTemplatesSetting();
        } else if ("cardtoovertime".equals(analyse_type)) {// 休息日转加班
            this.tab_Name = (String) this.getFormHM().get("cardToOverTimeTab");
            String app_type = (String) this.getFormHM().get("app_type");// 申请类型
            
            if (this.tab_Name == null)
                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.analyse.no.card.to.overtime.tab"), "", ""));
            
            analyseBusi.analayseCardToOverTimeTab();
            fieldlist = analyseBusi.getFieldList();// 指标list
            column = analyseBusi.getColumn();// 字段
            sql = getCardtoOverTime(sql_db_list, a_code, start_date, end_date, start_time, end_time, where_c, column, app_type, null);
            this.getFormHM().put("cardToOverTimeTab", this.tab_Name);
            putOverTimeTemplatesSetting();
        } else if ("busicompare".equals(analyse_type)) {// 申请业务对比

            this.tab_Name = (String) this.getFormHM().get("busiCompareTab");
            String app_type = (String) this.getFormHM().get("app_type");// 申请类型
            String busi_filtrate = (String) this.getFormHM().get("busi_filtrate");// 时长过滤，0：全部；1：申请时长大于时长；2申请时长小于实际时长
            if (busi_filtrate == null)
                busi_filtrate = "0";
            this.getFormHM().put("busi_filtrate", busi_filtrate);
            
            if (this.tab_Name == null)
                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.analyse.no.busi.compare.tab"), "", ""));
            
            ArrayList applist = analyseBusi.getAppTypeList();
            this.getFormHM().put("applist", applist);
            analyseBusi.analyseCompareBusiWithFactTab();
            fieldlist = analyseBusi.getFieldList();// 指标list
            column = analyseBusi.getColumn();// 字段
            sql = getBusiCompareSQL(sql_db_list, a_code, start_date, end_date, start_time, end_time, where_c, column, app_type, busi_filtrate);
            this.getFormHM().put("busiCompareTab", this.tab_Name);
        }
        
        
        this.getFormHM().put("column", column);
        //zxj 2014.5.8 对于有union的查询，外边再套一层select..from..,
        //   防止前台使用分页标签有keys的情况下分页错误
        this.getFormHM().put("sqlstr", "select * from (" + sql + ") A where 1=1");
        
        if ("result".equals(analyse_type))
            this.getFormHM().put("order", "order by i,b0110,e0122,e01a1,a0100,q03z0");
        else if ("cardtoovertime".equals(analyse_type)) {
            this.getFormHM().put("order", "order by b0110,e0122,e01a1,a0100,begin_date");
        } else
            this.getFormHM().put("order", "order by i,b0110,e0122,e01a1,a0100");
        
        this.getFormHM().put("a_code", a_code);
        this.getFormHM().put("start_date", start_date);
        this.getFormHM().put("start_hh", start_hh);
        this.getFormHM().put("start_mm", start_mm);
        this.getFormHM().put("end_date", end_date);
        this.getFormHM().put("end_hh", end_hh);
        this.getFormHM().put("end_mm", end_mm);
        this.getFormHM().put("fieldList", fieldlist);
        this.getFormHM().put("lockedNum", lockedNum + "");
    }

    private void putOverTimeTemplatesSetting() {
        try {
            TemplateTableParamBo templateBo = new TemplateTableParamBo(this.getFrameconn());
            ArrayList templateList = templateBo.getOvertimeTemplateList();
            this.getFormHM().put("overtime_templates", templateList);
            this.getFormHM().put("overtimeTemplateId", "");
            
            CommonData spData = null;
            ArrayList spList = new ArrayList();
            ArrayList spCodeItems = AdminCode.getCodeItemList("23");
            for (int i=0; i<spCodeItems.size(); i++) {
                CodeItem item = (CodeItem)spCodeItems.get(i);
                if ("01".equals(item.getCodeitem())) {
                    spData = new CommonData("01", item.getCodename());
                    spList.add(spData);
                } else if ("03".equals(item.getCodeitem())){
                    spData = new CommonData("03", item.getCodename());
                    spList.add(spData);
                }
            }
            this.getFormHM().put("overtimeSpList", spList);
            this.getFormHM().put("overtimeSpState", "03");
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理结果表组合sql
     * 
     * @param kq_nbase_list
     * @param a_code
     * @param start_date
     * @param end_date
     * @param start_time
     * @param end_time
     * @param where_c
     * @param column
     * @param tableName
     * @return
     */
    private String getAnalyeResUltTranSQL(ArrayList kq_nbase_list, String a_code, String start_date, String end_date, String start_time, String end_time, String where_c, String column, String tableName, String view) {
        KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
        StringBuffer sqlstr = new StringBuffer();
        
        if (start_date != null && start_date.length() > 0)
            start_date = start_date.replaceAll("-", "\\.");
        
        if (end_date != null && end_date.length() > 0)
            end_date = end_date.replaceAll("-", "\\.");
        
        String viewSql = "";
        if (view != null && view.indexOf(",zc,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'正常'") + " = '正常' ";
        
        if (view != null && view.indexOf(",xx,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'休息'") + " = '休息'";
        
        if (view != null && view.indexOf(",kg,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'正常'") + " like '%旷工%'";
        
        if (view != null && view.indexOf(",cd,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'正常'") + " like '%迟到%'";
        
        if (view != null && view.indexOf(",zt,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'正常'") + " like '%早退%'";
        
        if (view != null && view.indexOf(",lg,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'正常'") + " like '%离岗%'";
        
        if (view != null && view.indexOf(",qj,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'正常'") + " like '%请假%'";
        
        if (view != null && view.indexOf(",gc,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'正常'") + " like '%公出%'"; 
        
        if (view != null && view.indexOf(",jb,") != -1)
            viewSql += " or " + Sql_switcher.isnull("isok", "'正常'") + " like '%加班%'";
        
        if (view != null && view.indexOf("abnor") != -1)
            viewSql = " or " + Sql_switcher.isnull("isok", "'正常'") + " NOT IN('正常','休息')";
        
        if(viewSql.length() > 4){
            viewSql = viewSql.substring(4);
            viewSql = " and (" + viewSql + ")" ;
        }
        
        /*zxj 2014.5.9 
         * 因分页分页标签底层实现对有keys的sqlserver语句的orderby字段顺序有要求，
         * 字段顺序不一致将导致排序被抛弃
         * 此处特殊处理一下，使得到的sql中包含一致的排序指标串i,b0110,e0122,e01a1,a0100,q03z0 
         */
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            column = ("," + column.toLowerCase() + ",")
                    .replace(",b0110,", ",")
                    .replace(",e0122,", ",")
                    .replace(",e01a1,", ",")
                    .replace(",a0100,", ",")
                    .replace(",q03z0,", ",");
            column = "b0110,e0122,e01a1,a0100,q03z0" + column.substring(0, column.lastIndexOf(","));
        }
        
        for (int i = 0; i < kq_nbase_list.size(); i++) {
            String nbase = kq_nbase_list.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            sqlstr.append("select " + i + " as i," + column + " ");
            sqlstr.append(" from " + tableName);
            sqlstr.append(" where  ");
            sqlstr.append(" nbase='" + nbase + "'");
            sqlstr.append(" and q03z0>='" + start_date + "'");
            sqlstr.append(" and q03z0<='" + end_date + "'");
            sqlstr.append(" and " + kqCardData.getCodeItemWhere(a_code, nbase));
            if (where_c != null && where_c.length() > 0)
                sqlstr.append(" " + where_c + "");
            if (viewSql != null && viewSql.length() > 0)
                sqlstr.append(" " + viewSql);
            // sqlstr.append(" and a0100 in(select a0100 "+whereIN+") ");
            // 首钢优化，in的速度会慢，改用EXISTS
            if (!userView.isSuper_admin()) {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " AND " + nbase + "A01.a0100=" + tableName + ".a0100 ");
                sqlstr.append(" and " + kqCardData.getCodeItemWhere(a_code, nbase));
                sqlstr.append(")");
            } else {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " where " + nbase + "A01.a0100=" + tableName + ".a0100 ");
                sqlstr.append(" and " + kqCardData.getCodeItemWhere(a_code, nbase));
                sqlstr.append(")");
            }
            sqlstr.append(" UNION ALL ");
        }
        sqlstr.setLength(sqlstr.length() - 11);
        return sqlstr.toString();
    }

    /**
     * 异常表组合sql
     * 
     * @param kq_nbase_list
     * @param a_code
     * @param start_date
     * @param end_date
     * @param start_time
     * @param end_time
     * @param where_c
     * @param column
     * @param tableName
     * @return
     */
    private String getAnalyeExceptCardSQL(ArrayList kq_nbase_list, String a_code, String start_date, String end_date, String start_time, String end_time, String where_c, String column) {
        KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
        StringBuffer sqlstr = new StringBuffer();
        if (start_date != null && start_date.length() > 0)
            start_date = start_date.replaceAll("-", "\\.");
        if (end_date != null && end_date.length() > 0)
            end_date = end_date.replaceAll("-", "\\.");
        for (int i = 0; i < kq_nbase_list.size(); i++) {
            String nbase = kq_nbase_list.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            sqlstr.append("select " + i + " as i," + column + " ");
            sqlstr.append(" from " + this.tab_Name);
            sqlstr.append(" where 1=1 and ");
            sqlstr.append(kqCardData.getCodeItemWhere(a_code, nbase));
            sqlstr.append(" and work_date>='" + start_date + "'");
            sqlstr.append(" and work_time>='" + start_time + "'");
            sqlstr.append(" and work_date<='" + end_date + "'");
            sqlstr.append(" and work_time<='" + end_time + "'");
            sqlstr.append(" and nbase='" + nbase + "'");
            if (where_c != null && where_c.length() > 0)
                sqlstr.append(" " + where_c + "");
            // sqlstr.append(" and a0100 in(select a0100 "+whereIN+") ");
            // 首钢优化，in的速度会慢，改用EXISTS
            if (!userView.isSuper_admin()) {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " AND " + nbase + "A01.a0100=" + this.tab_Name + ".a0100) ");
            } else {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " where " + nbase + "A01.a0100=" + this.tab_Name + ".a0100) ");
            }
            sqlstr.append(" UNION ");
        }
        sqlstr.setLength(sqlstr.length() - 7);
        return sqlstr.toString();
    }

    /**
     * 延时加班
     * 
     * @param kq_nbase_list
     * @param a_code
     * @param start_date
     * @param end_date
     * @param where_c
     * @param column
     * @return
     */
    private String getAnalyeOverTimeSQL(ArrayList kq_nbase_list, String a_code, 
            String start_date, String end_date, String start_time, String end_time, String where_c, String column) {
        
        KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
        StringBuffer sqlstr = new StringBuffer();
        start_date = start_date.replaceAll("\\.", "-");
        end_date = end_date.replaceAll("\\.", "-");
        String z1 = DateUtils.format(DateUtils.getDate(start_date + " " + start_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
        String z2 = DateUtils.format(DateUtils.getDate(end_date + " " + end_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");

        
        String durationWhr = " AND 1=1";
        ArrayList curDuration = RegisterDate.getKqDayList(this.getFrameconn());
        if (curDuration != null && 2 == curDuration.size()){
            String durationStart = (String)curDuration.get(0);
            //只显示未封存期间内的数据
            durationWhr = " AND begin_date>=" + Sql_switcher.dateValue(durationStart);
        }
        
        column = tranDateFieldToCharForSqlColumnsmns(column, "begin_date");
        column = tranDateFieldToCharForSqlColumnsmns(column, "end_date");
        
        for (int i = 0; i < kq_nbase_list.size(); i++) {
            String nbase = kq_nbase_list.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            sqlstr.append("select " + i + " as i," + column + " ");
            sqlstr.append(" from " + this.tab_Name);
            sqlstr.append(" where 1=1 and ");
            sqlstr.append(kqCardData.getCodeItemWhere(a_code, nbase));
            sqlstr.append(" and ((begin_date>" + Sql_switcher.dateValue(z1));
            sqlstr.append(" and begin_date<" + Sql_switcher.dateValue(z2) + ")");
            sqlstr.append(" or (end_date>" + Sql_switcher.dateValue(z1));
            sqlstr.append(" and end_date<" + Sql_switcher.dateValue(z2) + ")");
            sqlstr.append(" or (begin_date<=" + Sql_switcher.dateValue(z1));
            sqlstr.append(" and end_date>=" + Sql_switcher.dateValue(z2) + ")");
            sqlstr.append(")");
            sqlstr.append(durationWhr);
            /*
             * sqlstr.append(" and begin_date>="+Sql_switcher.dateValue(z1)+"");
             * sqlstr.append(" and end_date<="+Sql_switcher.dateValue(z2)+"");
             */
            sqlstr.append(" and nbase='" + nbase + "'");
            if (where_c != null && where_c.length() > 0)
                sqlstr.append(" " + where_c + "");
            // sqlstr.append(" and a0100 in(select a0100 "+whereIN+") ");
            // 首钢优化，in的速度会慢，改用EXISTS
            if (!userView.isSuper_admin()) {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " AND " + nbase + "A01.a0100=" + this.tab_Name + ".a0100) ");
            } else {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " where " + nbase + "A01.a0100=" + this.tab_Name + ".a0100) ");
            }
            sqlstr.append(" UNION ");
        }
        sqlstr.setLength(sqlstr.length() - 7);
        return sqlstr.toString();
    }

    /**
     * 申请业务对比
     * 
     * @param kq_nbase_list
     * @param a_code
     * @param start_date
     * @param end_date
     * @param where_c
     * @param column
     * @param app_type申请过滤
     * @param busi_filtrate时长过滤
     * @return
     */
    private String getBusiCompareSQL(ArrayList kq_nbase_list, String a_code, String start_date, String end_date, String start_time, String end_time, String where_c, String column, String app_type, String busi_filtrate) {
        KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
        StringBuffer sqlstr = new StringBuffer();
        StringBuffer where_is = new StringBuffer();
        if (app_type != null && "q11".equalsIgnoreCase(app_type))// 加班
        {
            where_is.append(" and busi_type like '1%'");
        } else if (app_type != null && "q13".equalsIgnoreCase(app_type))// 公出
        {
            where_is.append(" and busi_type like '3%'");
        } else if (app_type != null && "q15".equalsIgnoreCase(app_type))// 请假
        {
            where_is.append(" and busi_type like '0%'");
        }
        if (busi_filtrate != null && "1".equals(busi_filtrate))// 时长过滤，0：全部；1：申请时长大于时长；2申请时长小于实际时长
        {
            where_is.append(" and busi_timelen>fact_timelen");
        } else if (busi_filtrate != null && "2".equals(busi_filtrate)) {
            where_is.append(" and busi_timelen<fact_timelen");
        }
        start_date = start_date.replaceAll("\\.", "-");
        end_date = end_date.replaceAll("\\.", "-");
        String z1 = DateUtils.format(DateUtils.getDate(start_date + " " + start_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
        String z2 = DateUtils.format(DateUtils.getDate(end_date + " " + end_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
        
        column = tranDateFieldToCharForSqlColumnsmns(column, "busi_begin");
        column = tranDateFieldToCharForSqlColumnsmns(column, "busi_end");
        column = tranDateFieldToCharForSqlColumnsmns(column, "fact_begin");
        column = tranDateFieldToCharForSqlColumnsmns(column, "fact_end");
        
        for (int i = 0; i < kq_nbase_list.size(); i++) {
            String nbase = kq_nbase_list.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            sqlstr.append("select " + i + " as i," + column + " ");
            sqlstr.append(" from " + this.tab_Name);
            sqlstr.append(" where 1=1 and ");
            sqlstr.append(kqCardData.getCodeItemWhere(a_code, nbase));
            sqlstr.append(" and (busi_begin<=" + Sql_switcher.dateValue(z2));
            sqlstr.append(" and busi_end>=" + Sql_switcher.dateValue(z1) + ")");
            /*
             * sqlstr.append(" and busi_begin>="+Sql_switcher.dateValue(z1)+"");
             * sqlstr.append(" and busi_end<="+Sql_switcher.dateValue(z2)+"");
             */
            sqlstr.append(" and nbase='" + nbase + "'");
            if (where_c != null && where_c.length() > 0)
                sqlstr.append(" " + where_c + "");
            sqlstr.append(where_is.toString());
            // sqlstr.append(" and a0100 in(select a0100 "+whereIN+") ");
            // 首钢优化，in的速度会慢，改用EXISTS
            if (!userView.isSuper_admin()) {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " AND " + nbase + "A01.a0100=" + this.tab_Name + ".a0100) ");
            } else {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " where " + nbase + "A01.a0100=" + this.tab_Name + ".a0100) ");
            }
            sqlstr.append(" UNION ");
        }
        sqlstr.setLength(sqlstr.length() - 7);
        
        return sqlstr.toString();
    }

    private String getCardtoOverTime(ArrayList kq_nbase_list, String a_code, String start_date, String end_date, String start_time, String end_time, String where_c, String column, String app_type, String busi_filtrate) {
        KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
        StringBuffer sqlstr = new StringBuffer();
        StringBuffer where_is = new StringBuffer();
        if (app_type != null && "q11".equalsIgnoreCase(app_type))// 加班
        {
            where_is.append(" and busi_type like '1%'");
        } else if (app_type != null && "q13".equalsIgnoreCase(app_type))// 公出
        {
            where_is.append(" and busi_type like '3%'");
        } else if (app_type != null && "q15".equalsIgnoreCase(app_type))// 请假
        {
            where_is.append(" and busi_type like '0%'");
        }
        if (busi_filtrate != null && "1".equals(busi_filtrate))// 时长过滤，0：全部；1：申请时长大于时长；2申请时长小于实际时长
        {
            where_is.append(" and busi_timelen>fact_timelen");
        } else if (busi_filtrate != null && "2".equals(busi_filtrate)) {
            where_is.append(" and busi_timelen<fact_timelen");
        }
        start_date = start_date.replaceAll("\\.", "-");
        end_date = end_date.replaceAll("\\.", "-");
        String z1 = DateUtils.format(DateUtils.getDate(start_date + " " + start_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
        String z2 = DateUtils.format(DateUtils.getDate(end_date + " " + end_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");

        column = tranDateFieldToCharForSqlColumnsmns(column, "begin_date");
        column = tranDateFieldToCharForSqlColumnsmns(column, "end_date");
        for (int i = 0; i < kq_nbase_list.size(); i++) {
            String nbase = kq_nbase_list.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            sqlstr.append("select " + i + " as i," + column + " ");
            sqlstr.append(" from " + this.tab_Name);
            sqlstr.append(" where 1=1 ");
            sqlstr.append(" AND " + kqCardData.getCodeItemWhere(a_code, nbase));
            sqlstr.append(" and (begin_date<=" + Sql_switcher.dateValue(z2));
            sqlstr.append(" and end_date>=" + Sql_switcher.dateValue(z1) + ")");
            sqlstr.append(" and nbase='" + nbase + "'");
            if (where_c != null && where_c.length() > 0)
                sqlstr.append(" " + where_c + "");
            sqlstr.append(where_is.toString());
            
            // sqlstr.append(" and a0100 in(select a0100 "+whereIN+") ");
            // 首钢优化，in的速度会慢，改用EXISTS
            if (!userView.isSuper_admin()) {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " AND " + nbase + "A01.a0100=" + this.tab_Name + ".a0100) ");
            } else {
                sqlstr.append(" and EXISTS (select a0100 " + whereIN + " where " + nbase + "A01.a0100=" + this.tab_Name + ".a0100) ");
            }
            sqlstr.append(" UNION ");
        }
        sqlstr.setLength(sqlstr.length() - 7);
        return sqlstr.toString();
    }
    
    private String tranDateFieldToCharForSqlColumnsmns(String sqlColumns, String dateField) {
        String format = "yyyy-MM-dd hh:mm";
        if (Sql_switcher.searchDbServer() != Constant.MSSQL)
            format = "yyyy-mm-dd hh24:mi";
        
        return sqlColumns.replace(dateField, Sql_switcher.dateToChar(dateField, format) + " " + dateField);
    }
    
    /**
     * 组合分类显示类别字符串
     * @param oneSort 当前选中的类别 kg、qj and so on
     * @param checked 点击的分类是不是已经是勾选的 1 是  0 否
     * @param sort 当前分类显示类别字符串
     * @return
     */
    private String getSortItemStr(String oneSort, String checked, String sort){
        String sortStr = sort;
        
        oneSort = oneSort == null ? "" : oneSort;
        int oneSortLen = oneSort.length();//选择的分类的长度
        
        if ("all".equals(oneSort)) {
            if ("1".equals(checked)) 
                sortStr = "";
            else 
                sortStr = oneSort;
        }else {
            if ("".equals(sort) && oneSortLen > 0) {//第一次点
                sortStr = "," + oneSort + ",";
            }else {
                sort = "all".equals(sort) ? "," : sort;
                
                if (oneSortLen > 0) {
                    int index = sort.indexOf(oneSort);
                    
                    if ("1".equals(checked)) {//点击已选的分类,则去掉
                        
                        sortStr = sort.substring(0, index -1) + sort.substring(index + oneSortLen);
                        
                    }else if ("0".equals(checked)) {//点击未选的分类,则增加
                        
                        if ("abnor".equals(oneSort)) //点击异常
                            sortStr = "," + oneSort + ",";
                        else {
                            sortStr = sort + oneSort + ",";
                            
                            if (!"abnor".equals(oneSort)) {//点击非异常,去掉异常
                                index = sortStr.indexOf("abnor");
                                if(index > 0)
                                    sortStr = sortStr.substring(0, index -1) + sortStr.substring(index + 5);
                            }
                        } 
                    }
                }
            }
        }
        
        return sortStr;
    }
}
