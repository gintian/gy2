package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.*;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class OrgDailyCollectTrans extends IBusiness {
    public void execute() throws GeneralException {

        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String kq_duration = RegisterDate.getKqDuration(this.getFrameconn());
            // 转换小时 1=默认；2=HH:MM
            String selectys = (String) hm.get("selectys");
            if (selectys == null || "".equals(selectys)) {
                selectys = "1";
            }
            this.getFormHM().put("selectys", selectys);
            
            ArrayList datelist = (ArrayList) this.getFormHM().get("datelist");
            ArrayList vo_datelist = (ArrayList) this.getFormHM().get("vo_datelist");
            String registerdate = (String) this.getFormHM().get("registerdate");
            if (datelist == null || datelist.size() <= 0) {
                datelist = RegisterDate.getKqDurationList(this.frameconn);
            }
            if (vo_datelist == null || vo_datelist.size() <= 0) {
                vo_datelist = RegisterDate.registerdate(this.userView.getUserOrgId(), this.getFrameconn(), this.userView);
            } else {
                // zxj 20180619 vo_datelist中的数据可能是期间改变前的（封存或解封之前的期间）
                CommonData vo = (CommonData) vo_datelist.get(0);
                if (datelist != null && datelist.size() > 0 && !vo.getDataValue().equals((String) datelist.get(0)))
                    vo_datelist = RegisterDate.registerdate(this.userView.getUserOrgId(), this.getFrameconn(),
                            this.userView);
            }

            String cur_date = ((CommonData)vo_datelist.get(0)).getDataValue();
            this.formHM.put("registerdate", cur_date);

            String workcalendar = RegisterInitInfoData.getDateSelectHtml(vo_datelist, cur_date);

            String start_date = datelist.get(0).toString();
            String end_date = datelist.get(datelist.size() - 1).toString();
            
            ArrayList a0100whereIN = new ArrayList();
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
            for (int i = 0; i < kq_dbase_list.size(); i++) {
                String dbase = kq_dbase_list.get(i).toString();
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase);
                a0100whereIN.add(whereA0100In);
            }
            /************ 得到部门权限 **********/
            String whereE0122 = OrgRegister.selcet_kq_OrgId(start_date, end_date, "e0122", a0100whereIN, "");

            ArrayList orgide0122List = OrgRegister.getQrgE0122List(this.frameconn, whereE0122, "e0122");
            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            StringBuffer statcolumn = new StringBuffer();
            StringBuffer insertcolumn = new StringBuffer();
            StringBuffer un_statcolumn = new StringBuffer();
            StringBuffer un_insertcolumn = new StringBuffer();
            DbWizard dbWizard = new DbWizard(this.frameconn);
            int num = 0;
            int un_num = 0;
            CollectRegister collectRegister = new CollectRegister();

            /*
             * 首钢 上岛标识 不在考勤规则里，但是月统计还需要计算进来；这里过滤一下
             */
            
            // 得到上岛标识 对应的字段
            String sdao_count_field = SystemConfig.getPropertyValue("sdao_count_field"); 
            if (StringUtils.isNotEmpty(sdao_count_field)) {
                if (!dbWizard.isExistField("Q03", sdao_count_field.toLowerCase())) {
                    sdao_count_field = "";
                }
            }
            
            for (int i = 0; i < fielditemlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    
                if (!"N".equals(fielditem.getItemtype()))
                    continue;
                    
                if ("i9999".equals(fielditem.getItemid())) 
                    continue;
                
                int want_sum = CollectRegister.getWant_Sum(fielditem.getItemid(), this.getFrameconn());

                if (want_sum == 1 || sdao_count_field.equalsIgnoreCase(fielditem.getItemid())) {
                    statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                    insertcolumn.append("" + fielditem.getItemid() + ",");
                }
                un_statcolumn.append("sum(" + fielditem.getItemid() + ") as " + fielditem.getItemid() + ",");
                un_insertcolumn.append("" + fielditem.getItemid() + ",");
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
            // 插入汇总人员记录，修改：Q03Z3为从Q03表查询出来不为常量值‘0’
            StringBuffer sql = new StringBuffer();

            CountMoInfo countMoInfo = new CountMoInfo(this.userView, this.getFrameconn());

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            for (int r = 0; r < orgide0122List.size(); r++) {
                String e0122 = orgide0122List.get(r).toString();
                // 判断是否已经汇总过返回false则表示没有
                boolean delrecord = delRecord(e0122, start_date, end_date, "UM");
                if (delrecord) {
                    collectRecord2("e0122", e0122, dao, "UM", start_date, end_date, insertcolumnstr, statcolumnstr);
                    countMoInfo.countOrgKqInfo("Q07", e0122, start_date, end_date);
                } else {
                    throw GeneralExceptionHandler.Handle(
                            new GeneralException("", ResourceFactory.getProperty("kq.register.collect.lost"), "", ""));
                }
            }
            /********** 得到单位权限 **********/
            String whereB0110 = OrgRegister.selcet_kq_OrgId(start_date, end_date, "b0110", a0100whereIN, "");
            ArrayList orgidb0110List = OrgRegister.getQrgE0122List(this.frameconn, whereB0110, "b0110");
            for (int r = 0; r < orgidb0110List.size(); r++) {
                String b0110 = orgidb0110List.get(r).toString();

                // 判断该考勤期间是否可以重新统计
                boolean delrecord = delRecord(b0110, start_date, end_date, "UN");
                if (delrecord) {
                    collectRecord2("b0110", b0110, dao, "UM", start_date, end_date, insertcolumnstr, statcolumnstr);
                    countMoInfo.countOrgKqInfo("Q07", b0110, start_date, end_date);
                } else {
                    throw new GeneralException("", ResourceFactory.getProperty("kq.register.collect.lost"), "", "");
                }
            }

            String kind = (String) this.getFormHM().get("kind");

            // 判断当前操作考勤期间是否有数据
            ArrayList list = OrgRegister.newFieldItemList(fielditemlist);
            String codesetid = "UN";
            if (!userView.isSuper_admin()) {
                if ("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
                    codesetid = "UM";
            }
            list = OrgRegister.newFieldItemListQ07(list, codesetid);
            String kq_period = OrgRegister.getMonthRegisterDate(start_date, end_date);
            String code = (String) this.getFormHM().get("code");
            String b0110 = code;
            if (b0110 == null || b0110.length() <= 0) {
                b0110 = RegisterInitInfoData.getKqPrivCodeValue(userView);
            }
            
            for (int i = 0; i < kq_dbase_list.size(); i++) {
                String dbase = kq_dbase_list.get(i).toString();
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase);

                a0100whereIN.add(whereA0100In);
            }
            
            ArrayList sqllist = OrgRegister.getSqlstr(list, start_date, end_date, b0110, "Q07", start_date, a0100whereIN);
            // 显示部门层数
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            this.getFormHM().put("uplevel", uplevel);
            this.getFormHM().put("sqlstr", sqllist.get(0).toString());
            this.getFormHM().put("strwhere", sqllist.get(1).toString());
            this.getFormHM().put("columns", sqllist.get(2).toString());
            this.getFormHM().put("orderby", " order by b0110");
            this.getFormHM().put("fielditemlist", list);
            this.getFormHM().put("kq_duration", kq_duration);
            this.getFormHM().put("kind", kind);
            this.getFormHM().put("orgvali", "");
            this.getFormHM().put("datelist", datelist);
            this.getFormHM().put("kq_period", kq_period);
            this.getFormHM().put("action", "collect_orgdailydata");
            this.getFormHM().put("workcalendar", workcalendar);

            // 将导出模板的sql语句保存至服务器
            String kq_sql_unit = sqllist.get(0).toString() + sqllist.get(1).toString() + " order by b0110";
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

    private boolean collectRecord2(String org_id, String org_value, ContentDAO dao, String codesetid, String start_date,
            String end_date, String insertcolumnstr, String statcolumnstr) throws GeneralException {
        boolean isCorrect = true;
        // 建立一张临时表
        String table_name = "q07";
        // 拼写sum的sql语句
        StringBuffer sql = new StringBuffer();

        // 人员库控制
        KqParameter kq_paramter = new KqParameter(formHM, userView, "", this.frameconn);
        String kq__BASE = kq_paramter.getNbase();
        String[] base = kq__BASE.split(",");
        ArrayList list = new ArrayList();
        try {
            sql.delete(0, sql.length());
            sql.append("insert into " + table_name + "(b0110,q03z0," + insertcolumnstr + " ,setid,Q03Z5)");//
            sql.append("select  " + org_id + ",q03z0," + statcolumnstr + ",'" + codesetid + "','01' from Q03");
            sql.append(" where 1=1");
            sql.append(" and Q03Z0 >= '" + start_date + "' and q03z0<='" + end_date + "'");
            sql.append(" and " + org_id + " ='" + org_value + "'");

            for (int i = 0; i < base.length; i++) {
                if (i == 0) {
                    sql.append(" and (");
                    sql.append("upper(nbase)='" + base[i].toUpperCase() + "'");
                } else {
                    sql.append(" or ");
                    sql.append("upper(nbase)='" + base[i].toUpperCase() + "'");
                }

                if (i == base.length - 1) {
                    sql.append(")");
                }
            }
            sql.append("  GROUP BY " + org_id + ",Q03Z0 ");
            String delsql = "delete from q07 where  b0110 ='" + org_value + "' and Q03Z0 >= '" + start_date
                    + "' and q03z0<='" + end_date + "'";
            dao.delete(delsql, new ArrayList());
            dao.insert(sql.toString(), new ArrayList());

        } catch (Exception e) {
            isCorrect = false;
            e.printStackTrace();
        }
        return isCorrect;

    }

    /**********
     * 对部门日表统计过的记录清除纪录*********
     * 
     * @param userbase
     *            数据库前缀
     * @param collectdate
     *            操作时间
     * @param code
     *            部门
     * @param userbase
     *            数据库前缀
     * @return 是否清除成功
     *
     *****/
    private boolean delRecord(String b0110, String start_date, String end_date, String codesetid) {
        boolean iscorrect = false;
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            // 判断是否已经汇总过
            StringBuffer delete_org = new StringBuffer();
            delete_org.append("delete from Q07 where");
            delete_org.append(" b0110 =? ");
            delete_org.append(OrgRegister.where_Date());
            delete_org.append(" and setid=? ");
            ArrayList dellist = new ArrayList();
            dellist.add(b0110);
            dellist.add(start_date);
            dellist.add(end_date);
            dellist.add(codesetid);
            ArrayList list = new ArrayList();
            list.add(dellist);

            dao.batchUpdate(delete_org.toString(), list);
            iscorrect = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iscorrect;
    }
}
