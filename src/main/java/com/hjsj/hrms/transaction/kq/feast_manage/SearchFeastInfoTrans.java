package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.DataAnalyseUtils;
import com.hjsj.hrms.businessobject.kq.machine.DateAnalyseImp;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 考勤年假管理
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jul 29, 2006:2:18:27 PM
 * </p>
 * 
 * @author sx
 * @version 1.0
 * 
 */
public class SearchFeastInfoTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String code = (String) hm.get("code");
            String kind = (String) hm.get("kind");
            if (!this.userView.isSuper_admin()) {
                String codeSet;
                if (code == null || code.length() == 0) {
                    codeSet = this.userView.getManagePrivCode();
                    if("".equals(codeSet)){
                    	 codeSet = this.userView.getKqManageValue().substring(0, 2);
                    }
                } else {
                    codeSet = getCodeSetByCode(code);
                }

                if ("UM".equalsIgnoreCase(codeSet)) {
                    kind = "1";
                } else if ("UN".equalsIgnoreCase(codeSet)) {
                    kind = "2";
                } else if ("@K".equalsIgnoreCase(codeSet)) {
                    kind = "0";
                }
            }

            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            if (code == null || code.length() <= 0) {
                code = managePrivCode.getPrivOrgId();
                if ((code == null || "".equals(code)) && !this.userView.isSuper_admin()) {
                    code = ""; //this.getOrgRootCode();// 防止人员范围勾选的是组织机构，那样的话code为"",会出现死循环
                }

                if (this.userView.isSuper_admin()) {
                    kind = "2";
                }
            } else if (kind == null || kind.length() <= 0) {
                code = managePrivCode.getPrivOrgId();
                kind = "2";
            }
            String kq_year = (String) this.getFormHM().get("kq_year");
            String hols_status = (String) this.getFormHM().get("hols_status");
            if (hols_status != null && "q33".equals(hols_status)) // 调休假
            {
                ArrayList fielditemlist = DataDictionary.getFieldList("Q33", Constant.USED_FIELD_SET);
                String leaveUsedOvertime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
                HashMap kqItem_hash = annualApply.count_Leave(leaveUsedOvertime);
                String fielditemid = (String) kqItem_hash.get("fielditemid");
                int unit = 1;
                if (fielditemid != null && fielditemid.length() > 0) 
				{
					FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid);
					unit = fieldItem.getDecimalwidth();
				}
                int q3301=0;
                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fieldItem = (FieldItem) fielditemlist.get(i);
                    if ("nbase".equals(fieldItem.getItemid())) {
                        fieldItem.setCodesetid("@@");
                    }
                    if ("q3301".equals(fieldItem.getItemid())) {
                    	q3301= i;
                    }
                    if ("q3305".equals(fieldItem.getItemid()) || "q3307".equals(fieldItem.getItemid()) || "q3309".equals(fieldItem.getItemid())) {
                        fieldItem.setDecimalwidth(unit);// 单位小时 显示unit位小数
                    }
                }
                
                fielditemlist.remove(q3301); // 去掉单据序号
                FeastComputer feastComputer = new FeastComputer(this.getFrameconn(), this.userView);
                ArrayList fieldlist = FeastComputer.newFieldItemList(fielditemlist);
                
                
                KqParameter kq_paramter = new KqParameter(this.userView, code, this.getFrameconn());
                HashMap hashmap = kq_paramter.getKqParamterMap();
                String kq_gno = (String) hashmap.get("g_no");
                
                FieldItem field_gno = new FieldItem();
                field_gno.setItemid("f1");
                field_gno.setItemdesc("工号");
                field_gno.setItemtype("A");
                field_gno.setCodesetid("0");
                field_gno.setVisible(true);
                
                //把工号指标固定插入到a0100之后
                for (int i = 0; i < fieldlist.size(); i++) {
                    FieldItem field = (FieldItem) fieldlist.get(i);
                    if ("a0100".equalsIgnoreCase(field.getItemid())) {
                        fieldlist.add(i + 1, field_gno);
                    }
                }
                
                String columns = feastComputer.getColumn(fieldlist);
                String strsql = getQueryString(code, kind, columns, unit, kq_gno);
                //xiexd 2014.09.15sql存入
                this.getUserView().getHm().put("key_kq_sql1", strsql);
                this.getUserView().getHm().put("key_kq_sql2", columns);
                this.getFormHM().put("strsql", strsql);
                this.getFormHM().put("strsql_encode", SafeCode.encode(strsql));
                this.getFormHM().put("columns", columns);
                this.getFormHM().put("fieldlist", fieldlist);
                this.getFormHM().put("orderby", "order by i,b0110,e0122,e01A1,a0100,q3303");
                this.getFormHM().put("hols_status", hols_status);
            } else {
                ArrayList yearlist = (ArrayList) this.getFormHM().get("yearlist");
                String select_flag = (String) hm.get("select_flag");
                if (select_flag == null || select_flag.length() <= 0)
                    select_flag = "0";
                /*
                 * 条件查询 b_search=query,在近来以后 不能保留 whereIN 结果 hols_manage.jsp
                 * 过来的url 中的 改为 b_search=query wy
                 */
                String whereIN = "";
                String b_search = (String) hm.get("b_search");
                if ("2".equals(select_flag)) {
                    String selectResult = (String) hm.get("selectResult");
                    whereIN = new CodingAnalytical().analytical(selectResult);
                }
                hm.remove("select_flag");
                String error_flag_session = "0";
                if (kq_year == null || kq_year.length() <= 0) {
                    if (yearlist == null || yearlist.size() <= 0)
                        yearlist = RegisterDate.getKqYear(this.getFrameconn());
                    if (yearlist != null && yearlist.size() > 0) {
                        Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        kq_year = year + "";
                    } else {
                        String error_return = "";

                        String error_message_session = "";
                        if (code == null || code.length() <= 0) {
                            error_return = "history.back();";
                            error_flag_session = "1";
                        } else {
                            error_return = "history.back();";
                            error_flag_session = "1";
                        }
                        error_message_session = ResourceFactory.getProperty("kq.register.session.nosave");
                        this.getFormHM().put("error_message_session", error_message_session);
                        this.getFormHM().put("error_return", error_return);
                    }

                }
                this.getFormHM().put("yearlist", yearlist);
                this.getFormHM().put("error_flag_session", error_flag_session);
                
                ArrayList fielditemlist = DataDictionary.getFieldList("Q17", Constant.USED_FIELD_SET);
                FeastComputer feastComputer = new FeastComputer(this.getFrameconn(), this.userView);
                ArrayList fieldlist = FeastComputer.newFieldItemList(fielditemlist);
                
                KqParameter kq_paramter = new KqParameter(this.userView, code, this.getFrameconn());
                HashMap hashmap = kq_paramter.getKqParamterMap();
                String kq_gno = (String) hashmap.get("g_no");
                
                FieldItem field_gno = new FieldItem();
                field_gno.setItemid("f1");
                field_gno.setItemdesc("工号");
                field_gno.setItemtype("A");
                field_gno.setCodesetid("0");
                field_gno.setVisible(true);
                
                //把工号指标固定插入到a0100之后
                for (int i = 0; i < fieldlist.size(); i++) {
                    FieldItem field = (FieldItem) fieldlist.get(i);
                    if ("a0100".equalsIgnoreCase(field.getItemid())) {
                        fieldlist.add(i + 1, field_gno);
                    }
                }

                String columns = feastComputer.getColumn(fieldlist);
                String strsql = getQueryString(code, kind, kq_year, columns, hols_status, whereIN, kq_gno, fieldlist);
              //xiexd 2014.09.15sql存入
                this.getUserView().getHm().put("key_kq_sql1", strsql);
                this.getUserView().getHm().put("key_kq_sql2", columns);
                this.getFormHM().put("strsql", strsql);
                this.getFormHM().put("strsql_encode", SafeCode.encode(strsql));
                this.getFormHM().put("columns", columns);
                this.getFormHM().put("fieldlist", fieldlist);
                this.getFormHM().put("orderby", "order by i,b0110,e0122,e01a1,a0100");
                ArrayList holi_list = (ArrayList) this.getFormHM().get("holi_list");
                this.getFormHM().put("hols_status", hols_status);
                this.getFormHM().put("holi_list", holi_list);
                this.getFormHM().put("kq_year", kq_year);
                this.getFormHM().put("code", code);
                this.getFormHM().put("kind", kind);
                this.getFormHM().put("returnURL", "/kq/feast_manage/managerdata.do?b_search=link");
                String condition = getCondition(code, kind, kq_year, hols_status, whereIN);
        	    // 涉及SQL注入直接放进userView里
         		this.userView.getHm().put("kq_condition", "17`" + condition);
                this.getFormHM().put("relatTableid", "17");
                this.getFormHM().put("select_flag", "0");
                
                // 显示部门层数
                Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
                String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                if (uplevel == null || uplevel.length() == 0)
                    uplevel = "0";
                this.getFormHM().put("uplevel", uplevel);
                
                if (!"query".equalsIgnoreCase(b_search)) {
                    whereIN = "";
                    this.getFormHM().put("whereIN", whereIN);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据code，获得组织机构的codeset
     * 
     * @param code
     * @return
     */
    private String getCodeSetByCode(String code) {
        String codeSet = "";
        String sql = "select codesetid from organization where codeitemid=?";
        ContentDAO dao = new ContentDAO(frameconn);
        try {
            ArrayList params = new ArrayList();
            params.add(code);
            this.frecset = dao.search(sql, params);
            if (this.frecset.next()) {
                codeSet = this.frecset.getString("codesetid");
                if (codeSet == null) {
                    codeSet = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return codeSet;
    }

    /**
     * 取登录考勤用户库的列表
     * 
     * @return
     * @throws Exception
     */
    private ArrayList getDbList(String code, String kind) throws GeneralException {
        String b0110 = code;
        String codesetid = "";
        if ("1".equals(kind) || "0".equals(kind)) {
            codesetid = code;
            do {
                String codeset[] = getB0100(b0110);
                if (codeset != null && codeset.length >= 0) {
                    codesetid = codeset[0];
                    b0110 = codeset[1];
                }
            } while (!"UN".equals(codesetid));
        }
        ArrayList dblist = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), b0110);

        return dblist;
    }

    public String[] getB0100(String codeitemid) throws GeneralException {
        String codeset[] = new String[2];
        String parentid = "";
        try {
            String orgSql = "SELECT parentid,codeitemid from organization where codeitemid='" + codeitemid + "'";
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            this.frowset = dao.search(orgSql);
            if (this.frowset.next()) {
                parentid = this.frowset.getString("parentid");
                if (parentid.equals(codeitemid)) {
                    codeset[0] = "UN";
                    codeset[1] = parentid;
                } else {
                    orgSql = "SELECT parentid,codesetid from organization where codeitemid='" + parentid + "'";
                    this.frecset = dao.search(orgSql);
                    if (this.frecset.next()) {
                        codeset[0] = this.frecset.getString("codesetid");
                        codeset[1] = parentid;
                    }
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return codeset;
    }

    /**
     * 组合年假查询SQL串
     * 
     * @param type
     * @param fieldlist
     * @return
     */
    private String getQueryString(String code, String kind, String q1701, String column, String hols_status, String whereSelectIN, String g_no, ArrayList fieldlist) throws GeneralException {
        StringBuffer strsql = new StringBuffer();
        String error_flag_nbase = "0";
        String select_where = whereSelectIN;
        try {
            String select_pre = (String) this.getFormHM().get("select_pre");
            String select_sturt = (String) this.getFormHM().get("select_sturt");
            String select_name = (String) this.getFormHM().get("select_name");
            this.getFormHM().put("select_sturt", select_sturt);
            this.getFormHM().remove("select_name");
            
            ArrayList sql_db_list = getDbList(code, kind);
            
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
            this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(sql_db_list));
            
            ArrayList dblist = new ArrayList();
            if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
                dblist.add(select_pre);
            } else {
                dblist = sql_db_list;
            }
            
            if (dblist.size() <= 0 || dblist == null) {
                this.getFormHM().put("codenull", "1");
                String error_return = "";
                String error_message_nbase = "";
                error_message_nbase = ResourceFactory.getProperty("kq.param.nosave.userbase");
                if (code == null || code.length() <= 0) {
                    error_return = "history.back();";
                    error_flag_nbase = "1";
                } else {
                    error_return = "history.back();";
                    error_flag_nbase = "1";
                }
                
                this.getFormHM().put("error_message_nbase", error_message_nbase);
                this.getFormHM().put("error_return", error_return);
            } else {
                String where_c = kqUtilsClass.getWhere_C(select_sturt, "q17.a0101", select_name);
                for (int i = 0; i < dblist.size(); i++) {
                    String nbase = (String) dblist.get(i);
                    String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                    strsql.append("select " + i + " as i,");
                    strsql.append(this.getSQLColumn(fieldlist, nbase, g_no));
                    strsql.append(" from q17," + nbase + "A01");
                    strsql.append(" where ");
                    if ("1".equals(kind)) {
                        strsql.append("q17.e0122 like '" + code + "%'");
                    } else if ("0".equals(kind)) {
                        strsql.append("q17.e01a1 like '" + code + "%'");
                    } else {
                        strsql.append("q17.b0110 like '" + code + "%'");
                    }
                    strsql.append(" and nbase='" + nbase + "'");
                    if (where_c != null && where_c.length() > 0)
                        strsql.append(where_c);
                    strsql.append(" and q1701='" + q1701 + "'");
                    strsql.append(" and q1709='" + hols_status + "'");
                    strsql.append(" and q17.a0100 in(select a0100 " + whereIN);
                    if (select_where != null && select_where.length() > 0) {
                        // &&改成||gdd 20130619
                        if (whereIN.indexOf("where") != -1 || whereIN.indexOf("WHERE") != -1) 
                            strsql.append(" and " + select_where);
                        else
                            strsql.append(" where " + select_where);
                    }
                    strsql.append(")");
                    strsql.append(" and q17.a0100 = " + nbase + "A01.a0100");
                    strsql.append(" UNION ");
                }
                strsql.setLength(strsql.length() - 7);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        this.getFormHM().put("error_flag_nbase", error_flag_nbase);
        return strsql.toString();
    }

    public String getCondition(String code, String kind, String q1701, String hols_status, String whereSelectIN) throws GeneralException {
        ArrayList dblist = getDbList(code, kind);
        StringBuffer condition = new StringBuffer();
        String select_where = whereSelectIN;
        // if(whereSelectIN==null||whereSelectIN.length()<=0)
        // whereSelectIN="";
        // else
        // {
        // String[] whereS=whereSelectIN.split("`");
        //			 
        // if(whereS!=null&&whereS.length==2)
        // {
        // String sel_status=whereS[0];
        // if(sel_status!=null&&sel_status.equalsIgnoreCase(hols_status))
        // select_where=whereS[1];
        // }
        // }
        if (code != null | code.length() <= 0) {
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        }
        if ("1".equals(kind)) {
            condition.append("e0122 like '" + code + "%'");
        } else if ("0".equals(kind)) {
            condition.append("e01a1 like '" + code + "%'");
        } else {
            condition.append("b0110 like '" + code + "%'");
        }
        condition.append(" and q1701='" + q1701 + "'");
        condition.append(" and q1709='" + hols_status + "'");
        if (select_where != null && select_where.length() > 0)
            condition.append(" and " + select_where);
        String isWhere = RegisterInitInfoData.getPrvListWhere(dblist, this.userView);
        if (isWhere != null && isWhere.length() > 0) {
            condition.append(" " + isWhere);
        }
        return condition.toString();
    }

    /**
     * 如果code为空，获取根节点的code值，code不为空
     * 
     * @return
     */
    private String getOrgRootCode() {
        String rootCode = "";
        StringBuffer sb = new StringBuffer();
        //sql有问题
//        sb.append("select * from organization A");
//        sb.append(" left join");
//        sb.append(" organization B");
//        sb.append(" on A.codeitemid = B.parentid");
//        sb.append(" where A.codesetid = B.codesetid and A.codeitemdesc = B.codeitemdesc");
        sb.append("select codeitemid from organization where codeitemid = parentid order by codeitemid");
        ContentDAO dao = new ContentDAO(frameconn);
        try {
            this.frowset = dao.search(sb.toString());
            if (this.frowset.next()) {
                rootCode = this.frowset.getString("codeitemid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rootCode;
    }

    private String getSQLColumn(ArrayList fieldlist, String nbase, String g_no) {
        StringBuffer column = new StringBuffer();
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            if ("a0100".equalsIgnoreCase(fielditem.getItemid())) {
                column.append("q17." + fielditem.getItemid() + " as a0100,");
                column.append(nbase + "A01." + g_no + " f1,");
                continue;
            }
            
            if ("b0110".equalsIgnoreCase(fielditem.getItemid())) {
                column.append("q17." + fielditem.getItemid() + " as b0110,");
                continue;
            }
            
            if ("e0122".equalsIgnoreCase(fielditem.getItemid())) {
                column.append("q17." + fielditem.getItemid() + " as e0122,");
                continue;
            }
            
            if ("e01a1".equalsIgnoreCase(fielditem.getItemid())) {
                column.append("q17." + fielditem.getItemid() + " as e01a1,");
                continue;
            }
            
            if ("a0101".equalsIgnoreCase(fielditem.getItemid())) {
                column.append("q17." + fielditem.getItemid() + " as a0101,");
                continue;
            }
            
            if ("a0100".equalsIgnoreCase(fielditem.getItemid())) {
                column.append("q17." + fielditem.getItemid() + " as a0100,");
                continue;
            }
            
            if ("f1".equalsIgnoreCase(fielditem.getItemid()))
                continue;
            
            column.append("q17." + fielditem.getItemid() + ",");
        }
        column.setLength(column.length() - 1);
        return column.toString();
    }

    /**
     * 组合调休假查询sql
     * 
     * @param code
     * @param kind
     * @param columns
     * @param kq_gno
     * 
     * @return
     */
    private String getQueryString(String code, String kind, String columns, int unit ,String kq_gno) {
        KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(this.frameconn, this.userView);
        HashMap period = kqOverTimeForLeave.getEffectivePeriod();
        String start_d = ((String)period.get("from")).replaceAll("-", ".");
        String end_d = ((String)period.get("to")).replaceAll("-", ".");
        
        StringBuffer strsql = new StringBuffer();
        ArrayList kq_dbase_list = new ArrayList();
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
        try {
            kq_dbase_list = kqUtilsClass.getKqPreList();
            String select_pre = (String) this.getFormHM().get("select_pre");
            String select_name = (String) this.getFormHM().get("select_name");
            String select_sturt = (String) this.getFormHM().get("select_sturt");
            this.getFormHM().remove("select_name");
            ArrayList dblist = new ArrayList();
            if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
                dblist.add(select_pre);
            } else {
                dblist = kq_dbase_list;
            }
            if (dblist != null && dblist.size() > 0){
                String itemId = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                DataAnalyseUtils analyseUtils = new DataAnalyseUtils(this.getFrameconn(), this.userView);
                HashMap kqItems = analyseUtils.count_Leave();
                HashMap kqItem = (HashMap)kqItems.get(itemId);
                String itemUnit = (String)kqItem.get("item_unit");
                
                String standardUnit = KqParam.getInstance().getSTANDARD_HOURS();
                String tranUnit = standardUnit;
                if (itemUnit == null || itemUnit.length() <= 0) {
                    itemUnit = DateAnalyseImp.unit_HOUR;
                }
                if (itemUnit.equals(DateAnalyseImp.unit_HOUR)) {
                    tranUnit = "60.0";
                } else if (itemUnit.equals(DateAnalyseImp.unit_MINUTE)) {
                    tranUnit = "1.0";
                } else if (itemUnit.equals(DateAnalyseImp.unit_DAY)) {
                    tranUnit = "(60.0*" + standardUnit + ")";
                }
                
                String where_c = kqUtilsClass.getWhere_C(select_sturt, "q33.a0101", select_name);
                for (int i = 0; i < dblist.size(); i++) {
                    String nbase = (String) dblist.get(i);
                    String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                    strsql.append("select " + i + " as i,");
                    strsql.append("q33.nbase as nbase,q33.a0100 as a0100,q33.b0110 as b0110,q33.e0122 as e0122,q33.e01a1 as e01a1,q33.a0101 as a0101,q33.q3303 as q3303,");
                    strsql.append( nbase + "A01." + kq_gno + " f1,");
                    strsql.append("ROUND(q33.q3305/" + tranUnit + "," + unit + ") as q3305,ROUND(q33.q3307/" + tranUnit + "," + unit + ") as q3307,ROUND(q33.q3309/" + tranUnit + "," + unit + ") as q3309");
                    strsql.append(" from q33,"+ nbase + "A01");
                    strsql.append(" where ");
                    if ("1".equals(kind)) {
                        strsql.append("q33.e0122 like '" + code + "%'");
                    } else if ("0".equals(kind)) {
                        strsql.append("q33.e01a1 like '" + code + "%'");
                    } else {
                        strsql.append("q33.b0110 like '" + code + "%'");
                    }
                    strsql.append(" and nbase='" + nbase + "'");
                    strsql.append(" and q3303 >= '" + start_d + "' and q3303 <= '" + end_d + "' ");
                    strsql.append(" and q33.a0100 in(select a0100 " + whereIN + ")");
                    if (null != where_c && where_c.length() > 0) {
                        strsql.append(where_c);
                    }
                    strsql.append(" and q33.a0100 = " + nbase + "A01.a0100");
                    strsql.append(" UNION ");
                }
                strsql.setLength(strsql.length() - 7);
            }

        } catch (GeneralException e) {
            e.printStackTrace();
        }
        return strsql.toString();
    }
}
