package com.hjsj.hrms.transaction.kq.register.history.statfx;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.statfx.RegisterStatBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 历史查询 统计分析 统计
 * @author Owner
 * wangyao
 */
public class SaveDataTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String code = (String) this.getFormHM().get("code");
        String kind = (String) this.getFormHM().get("kind");
        ArrayList kq_dbase_list = (ArrayList) userView.getPrivDbList(); //数据库类型
        String registertime = (String) this.getFormHM().get("registertime"); //开始时间
        String jsdatetime = (String) this.getFormHM().get("jsdatetime"); //结束时间
        String dbpre = (String) this.getFormHM().get("dbpre");
        if (dbpre.length() != 3)
            dbpre = "all";
        String file = (String) hm.get("file");
        hm.remove("file");
        registertime = registertime.replaceAll("-", "\\.");
        jsdatetime = jsdatetime.replaceAll("-", "\\.");
        String kq_period = "";
        String q03z0 = "";
        String cur_course = "";//页面需要开始时间
        String end_date = "";//页面需要结束时间
        kq_period = CollectRegister.getMonthRegisterDate(registertime, jsdatetime); //考勤期间  2008.2.2-2008.2.3
        q03z0 = kq_period.substring(0, 4); //年
        q03z0 = q03z0 + "-PT";
        if (kind == null || kind.length() <= 0) {
            kind = "2";
        }
        if (code == null || code.length() <= 0) {
            code = RegisterInitInfoData.getKqPrivCodeValue(this.userView);
        }
        if (kq_dbase_list == null || kq_dbase_list.size() <= 0) {
            kq_dbase_list = userView.getPrivDbList(); //求应用库前缀权限列表
        }

        ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        ArrayList a0100whereIN = new ArrayList();
        for (int i = 0; i < kq_dbase_list.size(); i++) //XXA01 库
        {
            String dbase = kq_dbase_list.get(i).toString();
            String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase); //判断是走UN活着UM
            a0100whereIN.add(whereA0100In);
        }

        if ((registertime != null && !"".equals(registertime)) || (jsdatetime != null && !"".equals(jsdatetime))) {
            cur_course = registertime;
            end_date = jsdatetime;
        } else {
            /**得到当前考勤期间 **/

            ArrayList list = RegisterDate.getKqDayList(this.getFrameconn());
            if (list != null && list.size() > 0) {
                cur_course = list.get(0).toString(); //当前考勤期间开始时间 1号
                end_date = list.get(1).toString(); // 30号，结束时间
            }
        }
        cur_course = cur_course.replaceAll("\\.", "-");
        end_date = end_date.replaceAll("\\.", "-");
        this.getFormHM().put("start_datetj", cur_course);
        this.getFormHM().put("end_datetj", end_date);

        if (kind == null || kind.length() <= 0) {
            kind = "2";
        }
        /**
         *页面头展现 
         */
        String kqname = "KQ_PARAM";
        ArrayList kqq03list = RegisterStatBo.savekqq03list(kqname, this.getFrameconn(), fielditemlist);
        String codesetid = "UN";
        if (!userView.isSuper_admin()) {
            if ("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
                codesetid = "UM";
        }
        kqq03list = RegisterStatBo.newFieldItemListQ09(kqq03list, codesetid);
        String whereE0122 = "";
        ArrayList orgide0122List = new ArrayList();
        String codeUN = kq_AllOrgId(a0100whereIN, code);
        if ("UN".equals(codeUN)) {
            whereE0122 = OrgRegister.selcet_kq_AllOrgId("b0110", a0100whereIN, code);
            orgide0122List = OrgRegister.getQrgE0122List(this.frameconn, whereE0122, "b0110");
        } else {
            whereE0122 = OrgRegister.selcet_kq_AllOrgId("e0122", a0100whereIN, code);
            orgide0122List = OrgRegister.getQrgE0122List(this.frameconn, whereE0122, "e0122");
        }

        StringBuffer b0110Str = new StringBuffer();
        for (int i = 0; i < orgide0122List.size(); i++) {
            b0110Str.append("'" + orgide0122List.get(i).toString() + "',");
        }
        ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
        String userOrgId = "";//managePrivCode.getPrivOrgId();    //权限ID号
        if (userOrgId != null && userOrgId.length() > 0) {
            b0110Str.append("'" + userOrgId + "',");
        }
        if (code != null && code.length() > 0) {
            b0110Str.append("'" + code + "',");
        }
        String b0100s = "";
        if (b0110Str.toString() != null && b0110Str.toString().length() > 0) {
            b0100s = b0110Str.toString().substring(0, b0110Str.length() - 1);
        }

        /**分析Q03，汇总到Q05表中 ’2009-PT’，其中2009为当前年份，PT为Person-Time缩写 **/
        getCourseCollect(q03z0, registertime, jsdatetime, kq_dbase_list, fielditemlist, 
                kq_period, a0100whereIN, userOrgId,
                codesetid, dbpre);
        /**通过Q05成成Q09**/
        getCourseCollectQ09(q03z0, userOrgId, codesetid, registertime, jsdatetime, fielditemlist, 
                kq_period, kq_dbase_list);

        String cur_d = cur_course.substring(0, 4);
        cur_d = cur_d + "-PT";
        String B0110z = selectB0110(code);
        ArrayList sqllist = RegisterStatBo.getSqlstrHistory(kqq03list, b0100s, cur_d, "Q09", 
                userOrgId, code, B0110z, this.getFrameconn(), file);

        this.getFormHM().put("kqq03list", kqq03list);
        this.getFormHM().put("kq_dbase_list", kq_dbase_list);
        this.getFormHM().put("code", code);
        this.getFormHM().put("kind", kind);
        this.getFormHM().put("sqlstr", sqllist.get(0).toString());
        this.getFormHM().put("strwhere", sqllist.get(1).toString());
        this.getFormHM().put("orderby", sqllist.get(2).toString());
        this.getFormHM().put("columns", sqllist.get(3).toString());
    }

    /**
     * 生成Q05信息
     * @param q03z0  2009-PT’  
     * @param start_date  开始时间
     * @param end_date 结束时间
     * @param kq_dbase_list  人员库结构
     * @param fielditemlist  Q03 的指标
     * @param kq_period   时间范围
     * @param a0100whereIN  
     * @throws GeneralException
     */
    private void getCourseCollect(String q03z0, String start_date, String end_date, ArrayList kq_dbase_list,
            ArrayList fielditemlist, String kq_period, ArrayList a0100whereIN, String userOrgId, String codesetid, String dbpre)
            throws GeneralException {
        boolean delrecord = delRecord(q03z0, kq_dbase_list, codesetid, userOrgId);
        if (delrecord) {
            collectRecord1(start_date, end_date, q03z0, kq_period, userOrgId, codesetid, dbpre); //得到 q03 组合的数据 
        } else {
            String error_message = ResourceFactory.getProperty("kq.register.collect.lost");
            this.getFormHM().put("error_message", error_message);
            this.getFormHM().put("error_flag", "3");
            return;
        }
    }

    /**
     * 通过Q05数据生成到Q09表中
     * @param codeitemid  id
     * @param UNandUM  UN或者UM
     * @param start_date 开始时间
     * @param end_date  结束时间
     * @param fielditemlist Q03 指标
     * @param kq_period 时间范围
     */
    private void getCourseCollectQ09(String q03z0, String codeitemid, String UNandUM, String start_date, String end_date,
            ArrayList fielditemlist, String kq_period, ArrayList kq_dbase_list) {
        try {
            if (userView.isSuper_admin())
                codeitemid = "";
            countByOrg(codeitemid, kq_period, fielditemlist, q03z0, kq_dbase_list);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 部门汇总
     * @param codeitemid
     * @param kq_period
     * @param fielditemlist
     * @param q03z0
     * @param kq_dbase_list
     */
    private void countByOrg(String codeitemid, String kq_period, ArrayList fielditemlist, String q03z0, ArrayList kq_dbase_list) {
        try {
            StringBuffer statcolumn = new StringBuffer();
            StringBuffer insertcolumn = new StringBuffer();
            StringBuffer un_statcolumn = new StringBuffer();
            StringBuffer un_insertcolumn = new StringBuffer();
            int un_num = 0;
            String sdao_count_field = SystemConfig.getPropertyValue("sdao_count_field"); //上岛签到字段不走考勤规则
            int num = 0;
            String want_sum_field = CollectRegister.getWant_Sum(this.getFrameconn());
            for (int i = 0; i < fielditemlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);

                if (!"N".equals(fielditem.getItemtype()))
                    continue;

                if ("i9999".equals(fielditem.getItemid()))
                    continue;

                if (sdao_count_field != null) {
                    if (sdao_count_field.equalsIgnoreCase(fielditem.getItemid())) {
                        statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                        insertcolumn.append("" + fielditem.getItemid() + ",");
                        num++;
                        un_statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                        un_insertcolumn.append("" + fielditem.getItemid() + ",");
                        un_num++;
                    } else {
                        if (want_sum_field.indexOf(fielditem.getItemid()) != -1) {
                            statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                    + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                            insertcolumn.append("" + fielditem.getItemid() + ",");
                            num++;
                        }
                        un_statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                        un_insertcolumn.append("" + fielditem.getItemid() + ",");
                        un_num++;
                    }
                } else {
                    if (want_sum_field.indexOf(fielditem.getItemid()) != -1) {
                        statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                        insertcolumn.append("" + fielditem.getItemid() + ",");
                        num++;
                    }
                    un_statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                            + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                    un_insertcolumn.append("" + fielditem.getItemid() + ",");
                    un_num++;
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
                num = un_num;
            }

            StringBuffer sql = new StringBuffer();
            ContentDAO dao = new ContentDAO(this.frameconn);
            if (delRecord(codeitemid)) {
                sql.append("insert into Q09 (");
                sql.append("Q03Z0,b0110,");
                sql.append(insertcolumnstr);
                sql.append(",q03z3,setid,q03z5,scope)");
                sql.append("select '" + q03z0 + "',org.codeitemid," + statcolumnstr + ",'1',org.codesetid,'01','" + kq_period
                        + "' from q05 bo right join organization org on ("
                        + Sql_switcher.left("bo.B0110", Sql_switcher.length("org.codeitemid")) + "=org.codeitemid or "
                        + Sql_switcher.left("bo.e0122", Sql_switcher.length("org.codeitemid"))
                        + "=org.codeitemid)  where org.codesetid in('UN','UM') and bo.scope='" + kq_period + "' and bo.q03z0='"
                        + q03z0 + "' and org.codeitemid like '" + codeitemid + "%' group by org.codesetid,org.codeitemid");
                dao.update(sql.toString());

                sql.setLength(0);
                sql.append("insert into Q09 (");
                sql.append("Q03Z0,b0110,q03z3,setid,q03z5,scope)");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sql.append("select '" + q03z0 + "',codeitemid,'1',codesetid,'01','" + kq_period + "' from organization where "
                        + Sql_switcher.dateValue(sdf.format(new Date()))
                        + " between start_date and end_date and codesetid in('UN','UM') and codeitemid like '" + codeitemid
                        + "%' and codeitemid not in(select b0110 from q09 where scope='" + kq_period + "' and q03z0='" + q03z0
                        + "')");
                dao.update(sql.toString());
            } else {
                String error_message = ResourceFactory.getProperty("kq.register.collect.lost");
                this.getFormHM().put("error_message", error_message);
                this.getFormHM().put("error_flag", "3");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean delRecord(String b0110) {
        boolean iscorrect = false;
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            //判断是否已经汇总过
            StringBuffer delete_org = new StringBuffer();
            delete_org.append("delete from Q09 where");
            delete_org.append(" b0110 like ? and q03z0 like '%-PT%'");
            ArrayList dellist = new ArrayList();
            dellist.add(b0110 + "%");
            dao.update(delete_org.toString(), dellist);
            iscorrect = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iscorrect;
    }

    /**
     *  组合复合要求的Q03数据，并且写到Q05表里
     * @param org_id  e0122
     * @param org_value  对应的 e0122 数据
     * @param start_date   开始时间
     * @param end_date   结束时间
     * @param q03z0    2009-PT
     * @param codesetid   UM
     * @param kq_period   考勤时间范围
     * @throws GeneralException
     */
    private void collectRecord1(String start_date, String end_date, String q03z0, String kq_period, String userOrgId,
            String codesetid, String dbpre) throws GeneralException {
        RowSet roset = null;
        try {
            /*****分析Q03   ******/
            StringBuffer statcolumn = new StringBuffer();
            StringBuffer insertcolumn = new StringBuffer();
            StringBuffer un_statcolumn = new StringBuffer();
            StringBuffer un_insertcolumn = new StringBuffer();

            int un_num = 0;
            //q03z0 = q03z0.substring(0, 4);
            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            int num = 0;
            String sdao_count_field = SystemConfig.getPropertyValue("sdao_count_field"); //上岛签到字段不走考勤规则
            String want_sum_items = CollectRegister.getWant_Sum(this.getFrameconn());
            for (int i = 0; i < fielditemlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                if ("N".equals(fielditem.getItemtype())) {
                    if (!"i9999".equals(fielditem.getItemid())) {
                        //int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
                        if (sdao_count_field != null) {
                            if (sdao_count_field.equalsIgnoreCase(fielditem.getItemid())) {
                                statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                        + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                                insertcolumn.append("" + fielditem.getItemid() + ",");
                                num++;
                                un_statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                        + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                                un_insertcolumn.append("" + fielditem.getItemid() + ",");
                                un_num++;
                            } else {
                                if (want_sum_items.indexOf(fielditem.getItemid()) != 1) {
                                    statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                            + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                                    insertcolumn.append("" + fielditem.getItemid() + ",");
                                    num++;
                                }
                                un_statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                        + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                                un_insertcolumn.append("" + fielditem.getItemid() + ",");
                                un_num++;
                            }
                        } else {
                            if (want_sum_items.indexOf(fielditem.getItemid()) != 1) {
                                statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                        + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                                insertcolumn.append("" + fielditem.getItemid() + ",");
                                num++;
                            }
                            un_statcolumn.append("count(case when " + fielditem.getItemid() + "<=0 then null else "
                                    + fielditem.getItemid() + " end) as " + fielditem.getItemid() + ",");
                            un_insertcolumn.append("" + fielditem.getItemid() + ",");
                            un_num++;
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
                num = un_num;
            }
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            StringBuffer sql = new StringBuffer();
            sql.append("insert into Q05 (");
            sql.append("nbase,a0100,");
            sql.append(insertcolumnstr);
            sql.append(",q03z0,q03z3,q03z5,scope,b0110,e0122,E01a1,a0101)");
            sql.append("select t1.*,t2.b0110,t2.e0122,t2.e01a1,t2.a0101 from (select nbase,a0100,");
            sql.append(statcolumnstr);
            sql.append(",'" + q03z0 + "' as q03z0,'0' as q03z3,'01' as q03z5,'" + kq_period + "' as scope");
            sql.append(" from Q03 q");
            sql.append(" where 1=1 ");
            if (!"all".equals(dbpre)) {
                sql.append(" and upper(nbase)='" + dbpre.toUpperCase() + "' ");
            }
            sql.append(OrgRegister.where_Date(start_date, end_date));
            if (!userView.isSuper_admin()) {
                if ("UN".equals(codesetid)) {
                    sql.append(" and B0110 like '" + userOrgId + "%'");
                } else if ("UM".equals(codesetid)) {
                    sql.append(" and E0122 like '" + userOrgId + "%'");
                }
            }
            sql.append(" group by nbase,a0100) t1,");
            sql.append("(select a0101,q03z0,nbase,a0100,b0110,e0122,e01a1 from Q03) t2");
            sql.append(" where t1.nbase=t2.nbase and t1.a0100=t2.a0100");
            sql.append(" and t2.q03z0=(select max(q03z0) from Q03 where nbase=t1.nbase and a0100=t2.a0100");
            sql.append(OrgRegister.where_Date(start_date, end_date));
            sql.append(")");
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(roset);
        }
    }

    /**
     * UN或者UM
     * @param a0100whereIN
     * @param b0110
     * @return
     */
    private String kq_AllOrgId(ArrayList a0100whereIN, String b0110) {
        String codeUn = "";
        StringBuffer sqlstr = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowSet = null;
        try {
            sqlstr.append("select distinct b0110,e0122 from Q03 where 1=1 ");
            if (b0110 != null && b0110.length() > 0) {
                sqlstr.append(" and b0110 like '" + b0110 + "%'  ");
            }
            rowSet = dao.search(sqlstr.toString());
            while (rowSet.next()) {
                String dd = rowSet.getString("b0110");
                if (dd != null || b0110.length() > 0) {
                    if (!dd.equalsIgnoreCase(b0110)) {
                        codeUn = "UN";
                        break;
                    } else {
                        codeUn = "UM";
                        break;
                    }
                } else {
                    codeUn = "UM";
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }

        return codeUn;
    }

    private boolean delRecord(String q03z0, ArrayList kq_dbase_list, String codesetid, String userOrgId) {
        boolean iscorrect = false;
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            //判断是否已经汇总过
            StringBuffer delete_org = new StringBuffer();
            delete_org.append("delete from Q05 where");
            delete_org.append(" Q03Z0 like ?"); //删除者还需要 确定组织 权限
            if (!userView.isSuper_admin()) {
                if ("UN".equals(codesetid)) {
                    delete_org.append(" and B0110 like ?");
                } else if ("UM".equals(codesetid)) {
                    delete_org.append(" and E0122 like ?");
                }

            }
            ArrayList dellist = new ArrayList();
            dellist.add("%-PT%");
            if (!userView.isSuper_admin()) {
                dellist.add(userOrgId + "%");
            }

            dao.update(delete_org.toString(), dellist);
            iscorrect = true;
            delete_org.setLength(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iscorrect;
    }

    /**
     * 通过 organization  得到对应的B0110
     * @param code
     * @return
     */
    private String selectB0110(String code) {
        String zh = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer sql1 = new StringBuffer();
        RowSet rowSet = null;
        try {
            sql1.append("select codeitemid from organization where grade='1' order by codeitemid");
            rowSet = dao.search(sql1.toString());
            while (rowSet.next()) {
                zh = rowSet.getString("codeitemid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return zh;
    }
}
