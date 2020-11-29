package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.EmpNetSignin;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * 员工网上签到
 * 
 * @author Owner
 * 
 */
public class EmpNetSigninTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String code = (String) this.getFormHM().get("code");
            String kind = (String) this.getFormHM().get("kind");
            String registerdate = (String) this.getFormHM().get("registerdate");
            if (registerdate != null && registerdate.length() > 0)
                registerdate = registerdate.replaceAll("-", ".");

            ArrayList kq_dbase_list = (ArrayList) this.getFormHM().get("kq_dbase_list");
            if (null == kq_dbase_list || 0 == kq_dbase_list.size())
                throw new GeneralException(ResourceFactory.getProperty("kq.nbase.no"));

            String select_pre = (String) this.getFormHM().get("select_pre");
            String select_name = (String) this.getFormHM().get("select_name");
            String cur_date = registerdate;
            String curclass = (String) this.getFormHM().get("curclass");// 当前过滤班次
            String cursignin = (String) this.getFormHM().get("cursignin");// 当前过滤签到
            if (curclass == null || curclass.length() <= 0 || "All".equalsIgnoreCase(curclass)) {
                cursignin = "All";
                this.getFormHM().put("cursignin", cursignin);
            }
            String sdao_count_field = (String) this.getFormHM().get("sdao_count_field");

            // 主集中的班组
            String A01C010k = KqParam.getInstance().getShiftGroupItem();
            String classA01 = "0";
            ArrayList fieldlists = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
            if (A01C010k != null) {
                if (!"".equals(A01C010k) || A01C010k.length() > 0) {
                    ArrayList fieldlist = getFieldlist(fieldlists, A01C010k);
                    this.getFormHM().put("fieldlist", fieldlist);
                    classA01 = "1";
                }
            } else {
                this.getFormHM().put("fieldlist", fieldlists);
            }
            this.getFormHM().put("classA01", classA01);
            // 工号用来区分人员重名
            String cardno = getCardno();
            String cardnoId = "0"; // 工号是否展现
            if (!"".equals(cardno) || cardno.length() > 0) {
                cardnoId = "1";
                String cardnoName = getCardnoName(cardno); // 得到中文名称
                this.getFormHM().put("cardnoName", cardnoName);// 工号名称
            }
            this.getFormHM().put("cardnoId", cardnoId);
            String columnstr = "";
            if ("1".equals(classA01) && "1".equals(cardnoId)) {
                columnstr = "B0110,E0122,E01A1,A0101,A0100,A0000," + A01C010k + "," + cardno;
            } else {
                if ("1".equals(classA01) && "0".equals(cardnoId)) {
                    columnstr = "B0110,E0122,E01A1,A0101,A0100,A0000," + A01C010k;
                } else if ("0".equals(classA01) && "1".equals(cardnoId)) {
                    columnstr = "B0110,E0122,E01A1,A0101,A0100,A0000," + cardno;
                } else if ("0".equals(classA01) && "0".equals(cardnoId)) {
                    columnstr = "B0110,E0122,E01A1,A0101,A0100,A0000";
                }
            }

            // String columnstr="B0110,E0122,E01A1,A0101,A0100,A0000";
            // if(select_pre==null||select_pre.length()<=0)
            // {
            // if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
            // select_pre=kq_dbase_list.get(0).toString();
            // }
            // 展现全部人员库中的人员
            ArrayList sql_db_list = new ArrayList();
            if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
                sql_db_list.add(select_pre);
            } else {
                sql_db_list = kq_dbase_list;
            }
            StringBuffer sqlstr = new StringBuffer();

            for (int k = 0; k < sql_db_list.size(); k++) {
                StringBuffer sqlstr2 = new StringBuffer();
                String nbase = (String) sql_db_list.get(k);
                // StringBuffer sqlstr=new StringBuffer();
                String field = KqParam.getInstance().getKqDepartment();
                sqlstr.append("select " + columnstr + ",");
                sqlstr.append("'" + nbase);
                sqlstr.append("' as nbase ");
                sqlstr.append(" from " + nbase + "A01 where");

                if (code == null || code.length() <= 0) {
                    code = RegisterInitInfoData.getKqPrivCodeValue(userView);
                }
                sqlstr2.append(sqlstr.toString());
                if ("1".equals(kind)) {
                    sqlstr.append(" e0122 like '" + code + "%'");
                    sqlstr2.append(" " + field + " like '" + code + "%'");
                } else if ("0".equals(kind)) {
                    sqlstr.append(" e01a1 like '" + code + "%'");
                    sqlstr2.append(" e01a1 like '" + code + "%'");
                } else {
                    sqlstr.append(" b0110 like '" + code + "%'");
                    sqlstr2.append(" b0110 like '" + code + "%'");
                }

                KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, code, this.getFrameconn());
                String kq_type = kq_paramter.getKq_type();
                if (kq_type == null || kq_type.length() <= 0)
                    throw new GeneralException(ResourceFactory.getProperty("kq.init.kqtype.nosave"));
                
                sqlstr.append(" and " + kq_type + "<>'04'");
                sqlstr2.append(" and " + kq_type + "<>'04'");
                KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
                String where_c = kqUtilsClass.getWhere_C("1", "a0101", select_name);
                if (where_c != null && where_c.length() > 0) {
                    sqlstr.append(" " + where_c + "");
                    sqlstr2.append(" " + where_c + "");
                }
                String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                String whereINs = "";
                if (field.length() > 0) {
                    String expres = userView.getPrivExpression();
                    expres = expres.replaceAll("E0122", field);

                    try {
                        whereINs = userView.getPrivSQLExpression(expres, nbase, false, new ArrayList());
                        whereINs = whereINs.replaceAll("E0122", field);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!this.userView.isSuper_admin()) {
                    if (whereIN.indexOf("WHERE") != -1) {
                        sqlstr.append(" and " + whereIN.substring(whereIN.indexOf("WHERE") + 5));
                        if (field.length() > 0) {
                            sqlstr2.append(" and ((" + whereIN.substring(whereIN.indexOf("WHERE") + 5) + ") or(" + whereINs.substring(whereINs.indexOf("WHERE") + 5) + "))");
                        }
                    } else if (whereIN.indexOf("where") != -1) {
                        sqlstr.append(" and " + whereIN.substring(whereIN.indexOf("where") + 5));
                        if (field.length() > 0) {
                            sqlstr2.append(" and ((" + whereIN.substring(whereIN.indexOf("where") + 5) + ") or (" + whereINs.substring(whereINs.indexOf("where") + 5) + "))");
                        }
                    }
                }

                int index = sqlstr.length();

                if (cursignin != null && cursignin.length() > 0 && !"All".equalsIgnoreCase(cursignin) && !"loadon".equalsIgnoreCase(cursignin) && !"loadoff".equalsIgnoreCase(cursignin)) {

                    if (curclass == null || curclass.length() <= 0 || "All".equalsIgnoreCase(curclass))
                        throw new GeneralException("请选择考勤班次！");
                }
                if (curclass != null && curclass.length() > 0 && !"All".equalsIgnoreCase(curclass))

                {
                    // 过滤班次
                    sqlstr.append(" and EXISTS(select a0100 from kq_employ_shift");
                    sqlstr.append(" where q03z0='" + cur_date + "' and nbase='" + nbase + "' and class_id='" + curclass + "' and kq_employ_shift.a0100=" + nbase + "A01.a0100");
                    sqlstr.append(")");
                    if (cursignin != null && cursignin.length() > 0 && !"All".equalsIgnoreCase(cursignin)) {
                        EmpNetSignin empNetSignin = new EmpNetSignin(this.userView, this.getFrameconn());
                        if ("oned".equalsIgnoreCase(cursignin) || "unon".equalsIgnoreCase(cursignin))// 已签到||未签到
                        {

                            HashMap map = empNetSignin.getOnOffTime(curclass);
                            String on_start_time = "";
                            String on_end_time = "";
                            if (map != null) {
                                on_start_time = (String) map.get("on_start_time");
                                on_end_time = (String) map.get("on_end_time");
                                if (on_start_time != null && on_start_time.length() > 0 && on_end_time != null && on_end_time.length() > 0) {
                                    // StringBuffer sql=new StringBuffer();
                                    // sql.append("select a0100 ");
                                    // sql.append(" from kq_originality_data");
                                    // sql.append(" where UPPER(nbase)='"+nbase.toUpperCase()+"'");
                                    // sql.append(" and work_date='"+cur_date+"'");
                                    // sql.append(" and work_time>='"+on_start_time+"'");
                                    // sql.append(" and work_time<='"+on_end_time+"'");
                                    // if(cursignin.equalsIgnoreCase("oned"))
                                    // sqlstr.append(" and a0100 in("+sql.toString()+")");
                                    // else
                                    // if(cursignin.equalsIgnoreCase("unon"))
                                    // sqlstr.append(" and a0100 not in("+sql.toString()+")");
                                    if (on_start_time.compareTo(on_end_time) > 0) {
                                        Calendar c = Calendar.getInstance();
                                        c.add(Calendar.DAY_OF_MONTH, 0);
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                        String mDateTime = formatter.format(c.getTime());
                                        String strStart = mDateTime.substring(0, 10);//
                                        strStart = strStart.replaceAll("-", "\\.");
                                        // System.out.println("当前日期 = "+strStart);
                                        // 得到现在时间
                                        String xzdate = PubFunc.getStringDate("HH-mm");
                                        xzdate = xzdate.replaceAll("-", "\\:");
                                        if (strStart.equals(cur_date)) {
                                            // 今天时间大于23：30分；就查询今天开始
                                            if (xzdate.compareTo(on_start_time) > 0) {
                                                // 得到后一天时间
                                                GregorianCalendar gc = new GregorianCalendar();
                                                String cur_date1 = cur_date.replaceAll("\\.", "-");
                                                gc.setTime(DateUtils.getDate(cur_date1, "yyyy-MM-dd"));
                                                gc.add(Calendar.DAY_OF_MONTH, +1);
                                                String date = DateUtils.format(gc.getTime(), "yyyy-MM-dd");
                                                date = date.replaceAll("-", "\\.");
                                                StringBuffer sql = new StringBuffer();
                                                sql.append("select a0100 ");
                                                sql.append(" from kq_originality_data");
                                                sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                                sql.append(" and ((work_date='" + strStart + "' and work_time>='" + on_start_time + "' and work_time<='23:59') ");
                                                sql.append(" or (work_date='" + date + "' and work_time>='00:00' and work_time<='" + on_end_time + "'))");
                                                if ("oned".equalsIgnoreCase(cursignin))
                                                    sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                                else if ("unon".equalsIgnoreCase(cursignin))
                                                    sqlstr.append(" and  not EXISTS(" + sql.toString() + ")");
                                            } else {
                                                // 得到前一天时间
                                                GregorianCalendar gc = new GregorianCalendar();
                                                String cur_date1 = cur_date.replaceAll("\\.", "-");
                                                gc.setTime(DateUtils.getDate(cur_date1, "yyyy-MM-dd"));
                                                gc.add(Calendar.DAY_OF_MONTH, -1);
                                                String date = DateUtils.format(gc.getTime(), "yyyy-MM-dd");
                                                date = date.replaceAll("-", "\\.");
                                                // System.out.println("111 = "+date);
                                                StringBuffer sql = new StringBuffer();
                                                sql.append("select a0100 ");
                                                sql.append(" from kq_originality_data");
                                                sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                                sql.append(" and ((work_date='" + date + "' and work_time>='" + on_start_time + "' and work_time<='23:59') ");
                                                sql.append(" or (work_date='" + cur_date + "' and work_time>='00:00' and work_time<='" + on_end_time + "'))");
                                                if ("oned".equalsIgnoreCase(cursignin))
                                                    sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                                else if ("unon".equalsIgnoreCase(cursignin))
                                                    sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                            }
                                        } else {
                                            // 得到前一天时间
                                            GregorianCalendar gc = new GregorianCalendar();
                                            String cur_date1 = cur_date.replaceAll("\\.", "-");
                                            gc.setTime(DateUtils.getDate(cur_date1, "yyyy-MM-dd"));
                                            gc.add(Calendar.DAY_OF_MONTH, -1);
                                            String date = DateUtils.format(gc.getTime(), "yyyy-MM-dd");
                                            date = date.replaceAll("-", "\\.");
                                            // System.out.println("111 = "+date);
                                            StringBuffer sql = new StringBuffer();
                                            sql.append("select a0100 ");
                                            sql.append(" from kq_originality_data");
                                            sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                            sql.append(" and ((work_date='" + date + "' and work_time>='" + on_start_time + "' and work_time<='23:59') ");
                                            sql.append(" or (work_date='" + cur_date + "' and work_time>='00:00' and work_time<='" + on_end_time + "'))");
                                            if ("oned".equalsIgnoreCase(cursignin))
                                                sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                            else if ("unon".equalsIgnoreCase(cursignin))
                                                sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                        }
                                    } else {
                                        StringBuffer sql = new StringBuffer();
                                        sql.append("select a0100 ");
                                        sql.append(" from kq_originality_data");
                                        sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                        sql.append(" and work_date='" + cur_date + "'");
                                        sql.append(" and work_time>='" + on_start_time + "'");
                                        sql.append(" and work_time<='" + on_end_time + "'");
                                        if ("oned".equalsIgnoreCase(cursignin))
                                            sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                        else if ("unon".equalsIgnoreCase(cursignin))
                                            sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                    }

                                }
                            }
                        } else if ("offed".equalsIgnoreCase(cursignin) || "unoff".equalsIgnoreCase(cursignin))// 已签退||未签退
                        {
                            if (curclass == null || curclass.length() <= 0 || "All".equalsIgnoreCase(curclass))
                                throw new GeneralException("请选择考勤班次！");
                            
                            HashMap map = empNetSignin.getOnOffTime(curclass);
                            String off_start_time = "";
                            String off_end_time = "";
                            if (map != null) {
                                off_start_time = (String) map.get("off_start_time");
                                off_end_time = (String) map.get("off_end_time");
                                if (off_start_time != null && off_start_time.length() > 0 && off_end_time != null && off_end_time.length() > 0) {
                                    StringBuffer sql = new StringBuffer();
                                    sql.append("select a0100 ");
                                    sql.append(" from kq_originality_data");
                                    sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                    sql.append(" and work_date='" + cur_date + "'");
                                    sql.append(" and work_time>='" + off_start_time + "'");
                                    sql.append(" and work_time<='" + off_end_time + "'");
                                    if ("offed".equalsIgnoreCase(cursignin))
                                        sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                    else if ("unoff".equalsIgnoreCase(cursignin))
                                        sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                }
                            }
                        } else if ("mend".equalsIgnoreCase(cursignin))// 补签到
                        {
                            if (cur_date != null) {
                                StringBuffer sql = new StringBuffer();
                                sql.append("select a0100 ");
                                sql.append(" from kq_originality_data");
                                sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                sql.append(" and work_date='" + cur_date + "'");
                                sql.append(" and datafrom='1'");
                                sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                            }
                        }
                    }
                }
                if (sdao_count_field != null && sdao_count_field.length() > 0) {
                    if ("loadon".equalsIgnoreCase(cursignin))// 已上岛
                    {
                        // sqlstr.append(" and a0100 in(select a0100 from q03 where UPPER(nbase)='"+select_pre.toLowerCase()+"'");
                        // //ora 区分大小写，转成小的会查不到
                        sqlstr.append(" and EXISTS(select a0100 from q03 where q03.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                        sqlstr.append(" and q03z0='" + cur_date + "' and " + sdao_count_field + "='1')");
                    } else if ("loadoff".equalsIgnoreCase(cursignin))// 未上岛
                    {
                        // sqlstr.append(" and a0100 in(select a0100 from q03 where UPPER(nbase)='"+select_pre.toLowerCase()+"'");//ora
                        // 区分大小写，转成小的会查不到
                        sqlstr.append(" and EXISTS(select a0100 from q03 where q03.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                        sqlstr.append(" and q03z0='" + cur_date + "' and " + Sql_switcher.isnull("" + sdao_count_field + "", "'0'") + "<>'1')");
                    }
                }
                String pdindex = KqParam.getInstance().getKqDepartment(); // 如果考勤参数设置了调换班组走这部分
                if (pdindex != null && pdindex.length() > 0) {
                    /*
                     * if(code!=null&&code.length()>0)
                     * sqlstr.append(" or ("+pdindex
                     * +" like '"+code+"%' and "+pdindex+" is not null)"); else
                     * { if(!this.userView.isSuper_admin()) {
                     * sqlstr.append(" or ("
                     * +pdindex+" in (select distinct e0122 "+whereIN+"))"); } }
                     */

                    if (where_c != null && where_c.length() > 0)
                        sqlstr.append(" " + where_c + "");
                    if (curclass != null && curclass.length() > 0 && !"All".equalsIgnoreCase(curclass)) {
                        // 过滤班次
                        sqlstr.append(" and EXISTS(select a0100 from kq_employ_shift");
                        sqlstr.append(" where kq_employ_shift.a0100=" + nbase + "A01.a0100 and q03z0='" + cur_date + "' and nbase='" + nbase + "' and class_id='" + curclass + "'");
                        sqlstr.append(")");
                        if (cursignin != null && cursignin.length() > 0 && !"All".equalsIgnoreCase(cursignin)) {
                            EmpNetSignin empNetSignin = new EmpNetSignin(this.userView, this.getFrameconn());
                            if ("oned".equalsIgnoreCase(cursignin) || "unon".equalsIgnoreCase(cursignin))// 已签到||未签到
                            {

                                HashMap map = empNetSignin.getOnOffTime(curclass);
                                String on_start_time = "";
                                String on_end_time = "";
                                if (map != null) {
                                    on_start_time = (String) map.get("on_start_time");
                                    on_end_time = (String) map.get("on_end_time");
                                    if (on_start_time != null && on_start_time.length() > 0 && on_end_time != null && on_end_time.length() > 0) {
                                        if (on_start_time.compareTo(on_end_time) > 0) {
                                            Calendar c = Calendar.getInstance();
                                            c.add(Calendar.DAY_OF_MONTH, 0);
                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                            String mDateTime = formatter.format(c.getTime());
                                            String strStart = mDateTime.substring(0, 10);//
                                            strStart = strStart.replaceAll("-", "\\.");
                                            // System.out.println("当前日期 = "+strStart);
                                            // 得到现在时间
                                            String xzdate = PubFunc.getStringDate("HH-mm");
                                            xzdate = xzdate.replaceAll("-", "\\:");
                                            if (strStart.equals(cur_date)) {
                                                // 今天时间大于23：30分；就查询今天开始
                                                if (xzdate.compareTo(on_start_time) > 0) {
                                                    // 得到后一天时间
                                                    GregorianCalendar gc = new GregorianCalendar();
                                                    String cur_date1 = cur_date.replaceAll("\\.", "-");
                                                    gc.setTime(DateUtils.getDate(cur_date1, "yyyy-MM-dd"));
                                                    gc.add(Calendar.DAY_OF_MONTH, +1);
                                                    String date = DateUtils.format(gc.getTime(), "yyyy-MM-dd");
                                                    date = date.replaceAll("-", "\\.");
                                                    StringBuffer sql = new StringBuffer();
                                                    sql.append("select a0100 ");
                                                    sql.append(" from kq_originality_data");
                                                    sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                                    sql.append(" and ((work_date='" + strStart + "' and work_time>='" + on_start_time + "' and work_time<='23:59') ");
                                                    sql.append(" or (work_date='" + date + "' and work_time>='00:00' and work_time<='" + on_end_time + "'))");
                                                    if ("oned".equalsIgnoreCase(cursignin))
                                                        sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                                    else if ("unon".equalsIgnoreCase(cursignin))
                                                        sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                                } else {
                                                    // 得到前一天时间
                                                    GregorianCalendar gc = new GregorianCalendar();
                                                    String cur_date1 = cur_date.replaceAll("\\.", "-");
                                                    gc.setTime(DateUtils.getDate(cur_date1, "yyyy-MM-dd"));
                                                    gc.add(Calendar.DAY_OF_MONTH, -1);
                                                    String date = DateUtils.format(gc.getTime(), "yyyy-MM-dd");
                                                    date = date.replaceAll("-", "\\.");
                                                    // System.out.println("111 = "+date);
                                                    StringBuffer sql = new StringBuffer();
                                                    sql.append("select a0100 ");
                                                    sql.append(" from kq_originality_data");
                                                    sql.append(" where  kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                                    sql.append(" and ((work_date='" + date + "' and work_time>='" + on_start_time + "' and work_time<='23:59') ");
                                                    sql.append(" or (work_date='" + cur_date + "' and work_time>='00:00' and work_time<='" + on_end_time + "'))");
                                                    if ("oned".equalsIgnoreCase(cursignin))
                                                        sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                                    else if ("unon".equalsIgnoreCase(cursignin))
                                                        sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                                }
                                            } else {
                                                // 得到前一天时间
                                                GregorianCalendar gc = new GregorianCalendar();
                                                String cur_date1 = cur_date.replaceAll("\\.", "-");
                                                gc.setTime(DateUtils.getDate(cur_date1, "yyyy-MM-dd"));
                                                gc.add(Calendar.DAY_OF_MONTH, -1);
                                                String date = DateUtils.format(gc.getTime(), "yyyy-MM-dd");
                                                date = date.replaceAll("-", "\\.");
                                                // System.out.println("111 = "+date);
                                                StringBuffer sql = new StringBuffer();
                                                sql.append("select a0100 ");
                                                sql.append(" from kq_originality_data");
                                                sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                                sql.append(" and ((work_date='" + date + "' and work_time>='" + on_start_time + "' and work_time<='23:59') ");
                                                sql.append(" or (work_date='" + cur_date + "' and work_time>='00:00' and work_time<='" + on_end_time + "'))");
                                                if ("oned".equalsIgnoreCase(cursignin))
                                                    sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                                else if ("unon".equalsIgnoreCase(cursignin))
                                                    sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                            }
                                        } else {
                                            StringBuffer sql = new StringBuffer();
                                            sql.append("select a0100 ");
                                            sql.append(" from kq_originality_data");
                                            sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and nbase='" + nbase + "'");
                                            sql.append(" and work_date='" + cur_date + "'");
                                            sql.append(" and work_time>='" + on_start_time + "'");
                                            sql.append(" and work_time<='" + on_end_time + "'");
                                            if ("oned".equalsIgnoreCase(cursignin))
                                                sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                            else if ("unon".equalsIgnoreCase(cursignin))
                                                sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                        }

                                    }
                                }
                            } else if ("offed".equalsIgnoreCase(cursignin) || "unoff".equalsIgnoreCase(cursignin))// 已签退||未签退
                            {
                                if (curclass == null || curclass.length() <= 0 || "All".equalsIgnoreCase(curclass))
                                    throw new GeneralException("请选择考勤班次！");
                                
                                HashMap map = empNetSignin.getOnOffTime(curclass);
                                String off_start_time = "";
                                String off_end_time = "";
                                if (map != null) {
                                    off_start_time = (String) map.get("off_start_time");
                                    off_end_time = (String) map.get("off_end_time");
                                    if (off_start_time != null && off_start_time.length() > 0 && off_end_time != null && off_end_time.length() > 0) {
                                        StringBuffer sql = new StringBuffer();
                                        sql.append("select a0100 ");
                                        sql.append(" from kq_originality_data");
                                        sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and  nbase='" + nbase + "'");
                                        sql.append(" and work_date='" + cur_date + "'");
                                        sql.append(" and work_time>='" + off_start_time + "'");
                                        sql.append(" and work_time<='" + off_end_time + "'");
                                        if ("offed".equalsIgnoreCase(cursignin))
                                            sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                        else if ("unoff".equalsIgnoreCase(cursignin))
                                            sqlstr.append(" and not EXISTS(" + sql.toString() + ")");
                                    }
                                }
                            } else if ("mend".equalsIgnoreCase(cursignin))// 补签到
                            {
                                if (cur_date != null) {
                                    StringBuffer sql = new StringBuffer();
                                    sql.append("select a0100 ");
                                    sql.append(" from kq_originality_data");
                                    sql.append(" where kq_originality_data.a0100=" + nbase + "A01.a0100 and  nbase='" + nbase + "'");
                                    sql.append(" and work_date='" + cur_date + "'");
                                    sql.append(" and datafrom='1'");
                                    sqlstr.append(" and EXISTS(" + sql.toString() + ")");
                                }
                            }
                        }
                    }
                }
                if (field.length() > 0) {
                    sqlstr2.append(" " + sqlstr.substring(index));
                    sqlstr.append(" union " + sqlstr2.toString());
                }
                sqlstr.append(" UNION ");
            }
            sqlstr.setLength(sqlstr.length() - 7);
            // StringBuffer sqlstr=new StringBuffer();
            // sqlstr.append("select "+columnstr+" ");
            // sqlstr.append(" from "+select_pre+"A01 where");
            //		
            // if(code==null||code.length()<=0)
            // {
            // code=RegisterInitInfoData.getKqPrivCodeValue(userView);
            // }
            // if(kind.equals("1"))
            // {
            // sqlstr.append(" e0122 like '"+code+"%'");
            // }else if(kind.equals("0"))
            // {
            // sqlstr.append(" e01a1 like '"+code+"%'");
            // }else
            // {
            // sqlstr.append(" b0110 like '"+code+"%'");
            // }
            //		
            // KqParameter kq_paramter = new
            // KqParameter(this.getFormHM(),this.userView,code,this.getFrameconn());
            // String kq_type=kq_paramter.getKq_type();
            // if(kq_type==null||kq_type.length()<=0)
            // throw GeneralExceptionHandler.Handle(new
            // GeneralException("没有定义考勤类型，错误！"));
            // sqlstr.append(" and "+kq_type+"<>'04'");
            // KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
            // String where_c=kqUtilsClass.getWhere_C("1","a0101",select_name);
            // if(where_c!=null&&where_c.length()>0)
            // sqlstr.append(" "+where_c+"");
            // String
            // whereIN=RegisterInitInfoData.getWhereINSql(userView,select_pre);
            // if(!this.userView.isSuper_admin())
            // sqlstr.append(" and a0100 in(select a0100 "+whereIN+") ");
            // if(cursignin!=null&&cursignin.length()>0&&!cursignin.equalsIgnoreCase("All")&&
            // !cursignin.equalsIgnoreCase("loadon")&&!cursignin.equalsIgnoreCase("loadoff"))
            // {
            //			
            // if(curclass==null||curclass.length()<=0||curclass.equalsIgnoreCase("All"))
            // throw GeneralExceptionHandler.Handle(new
            // GeneralException("请选择考勤班次！"));
            // }
            // if(curclass!=null&&curclass.length()>0&&!curclass.equalsIgnoreCase("All"))
            //	    	
            // {
            // //过滤班次
            // sqlstr.append(" and a0100 in(select a0100 from kq_employ_shift");
            // sqlstr.append(" where q03z0='"+cur_date+"' and UPPER(nbase)='"+select_pre.toUpperCase()+"' and class_id='"+curclass+"'");
            // sqlstr.append(")");
            // if(cursignin!=null&&cursignin.length()>0&&!cursignin.equalsIgnoreCase("All"))
            // {
            // EmpNetSignin empNetSignin=new
            // EmpNetSignin(this.userView,this.getFrameconn());
            // if(cursignin.equalsIgnoreCase("oned")||cursignin.equalsIgnoreCase("unon"))//已签到||未签到
            // {
            //	 	    		
            // HashMap map=empNetSignin.getOnOffTime(curclass);
            // String on_start_time="";
            // String on_end_time="";
            // if(map!=null)
            // {
            // on_start_time=(String)map.get("on_start_time");
            // on_end_time=(String)map.get("on_end_time");
            // if(on_start_time!=null&&on_start_time.length()>0&&on_end_time!=null&&on_end_time.length()>0)
            // {
            // StringBuffer sql=new StringBuffer();
            // sql.append("select a0100 ");
            // sql.append(" from kq_originality_data");
            // sql.append(" where UPPER(nbase)='"+select_pre.toUpperCase()+"'");
            // sql.append(" and work_date='"+cur_date+"'");
            // sql.append(" and work_time>='"+on_start_time+"'");
            // sql.append(" and work_time<='"+on_end_time+"'");
            // if(cursignin.equalsIgnoreCase("oned"))
            // sqlstr.append(" and a0100 in("+sql.toString()+")");
            // else if(cursignin.equalsIgnoreCase("unon"))
            // sqlstr.append(" and a0100 not in("+sql.toString()+")");
            //	 	   			        	
            // }
            // }
            // }else
            // if(cursignin.equalsIgnoreCase("offed")||cursignin.equalsIgnoreCase("unoff"))//已签退||未签退
            // {
            // if(curclass==null||curclass.length()<=0||curclass.equalsIgnoreCase("All"))
            // throw GeneralExceptionHandler.Handle(new
            // GeneralException("请选择考勤班次！"));
            // HashMap map=empNetSignin.getOnOffTime(curclass);
            // String off_start_time="";
            // String off_end_time="";
            // if(map!=null)
            // {
            // off_start_time=(String)map.get("off_start_time");
            // off_end_time=(String)map.get("off_end_time");
            // if(off_start_time!=null&&off_start_time.length()>0&&off_end_time!=null&&off_end_time.length()>0)
            // {
            // StringBuffer sql=new StringBuffer();
            // sql.append("select a0100 ");
            // sql.append(" from kq_originality_data");
            // sql.append(" where UPPER(nbase)='"+select_pre.toUpperCase()+"'");
            // sql.append(" and work_date='"+cur_date+"'");
            // sql.append(" and work_time>='"+off_start_time+"'");
            // sql.append(" and work_time<='"+off_end_time+"'");
            // if(cursignin.equalsIgnoreCase("offed"))
            // sqlstr.append(" and a0100 in("+sql.toString()+")");
            // else if(cursignin.equalsIgnoreCase("unoff"))
            // sqlstr.append(" and a0100 not in("+sql.toString()+")");
            // }
            // }
            // }
            // }
            // }
            // if(sdao_count_field!=null&&sdao_count_field.length()>0)
            // {
            // if(cursignin.equalsIgnoreCase("loadon"))//已上岛
            // {
            // //
            // sqlstr.append(" and a0100 in(select a0100 from q03 where UPPER(nbase)='"+select_pre.toLowerCase()+"'");
            // //ora 区分大小写，转成小的会查不到
            // sqlstr.append(" and a0100 in(select a0100 from q03 where UPPER(nbase)='"+select_pre.toUpperCase()+"'");
            // sqlstr.append(" and q03z0='"+cur_date+"' and "+sdao_count_field+"='1')");
            // }else if(cursignin.equalsIgnoreCase("loadoff"))//未上岛
            // {
            // //
            // sqlstr.append(" and a0100 in(select a0100 from q03 where UPPER(nbase)='"+select_pre.toLowerCase()+"'");//ora
            // 区分大小写，转成小的会查不到
            // sqlstr.append(" and a0100 in(select a0100 from q03 where UPPER(nbase)='"+select_pre.toUpperCase()+"'");
            // sqlstr.append(" and q03z0='"+cur_date+"' and "+Sql_switcher.isnull(""+sdao_count_field+"","'0'"
            // )+"<>'1')");
            // }
            // }
            // System.out.println(sqlstr.toString());
            String ordeby = " order by nbase,a0000";
            // String
            // workcalendar=RegisterInitInfoData.getDateSelectHtml(datelist,cur_date);
            // this.getFormHM().put("workcalendar",workcalendar);
            this.getFormHM().put("select_name", ""); // 查询出来名字清空
            columnstr += ",nbase";
            this.getFormHM().put("columns", columnstr);
            this.getFormHM().put("sqlstr", sqlstr.toString());
            this.getFormHM().put("ordeby", ordeby);
            this.getFormHM().put("select_pre", select_pre);
            this.getFormHM().put("cardno", cardno);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList getFieldlist(ArrayList fielditemlist, String c010k) {
        ArrayList list = new ArrayList();
        try {
            for (int i = 0; i < fielditemlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                if (fielditem.getItemid().equalsIgnoreCase(c010k)) {
                    list.add(fielditem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String getCardno() {
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
        String cardn = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");
        if (cardn == null)
            cardn = "";
        return cardn;
    }

    // 工号对应的中文名称
    private String getCardnoName(String id) {
        String cardnoName = "";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        id = id.toUpperCase();
        String sql = "select itemdesc from fielditem where fieldsetid='A01' and itemid='" + id + "'";
        try {
            rowSet = dao.search(sql);
            while (rowSet.next()) {
                cardnoName = rowSet.getString("itemdesc");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rowSet != null)
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return cardnoName;
    }
}
