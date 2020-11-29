package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.SummaryTableService;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 人员 工资|保险 汇总表 接口实现
 */
public class SummaryTableServiceImpl implements SummaryTableService {

    private UserView userView;
    private Connection conn;
    private GzAnalysisUtil gzUtil;

    public SummaryTableServiceImpl(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
        gzUtil = new GzAnalysisUtil(this.conn, this.userView);
    }

    @Override
    public ArrayList<ColumnsInfo> getColumnList() throws GeneralException {
        ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
        try {

            ColumnsInfo column = null;
            column = gzUtil.getColumnsInfo("seq", ResourceFactory.getProperty("gz_new.gz_sort"), 50, "", "A", 0, 0);
            column.setQueryable(false);
            column.setTextAlign("right");
            column.setLocked(true);
            column.setSortable(false);
            columns.add(column);

            //单位
            column = gzUtil.getColumnsInfo("b0110", ResourceFactory.getProperty("gz.columns.b0110Name"), 150, "UN", "A", 0, 0);
            column.setTextAlign("left");
            //锁列
            column.setLocked(true);
            columns.add(column);

            //部门
            column = gzUtil.getColumnsInfo("e0122", ResourceFactory.getProperty("hrms.e0122"), 150, "UM", "A", 0, 0);
            column.setTextAlign("left");
            //锁列
            column.setLocked(true);
            column.setFilterable(true);
            column.setDoFilterOnLoad(true);
            columns.add(column);

            //姓名
            column = gzUtil.getColumnsInfo("a0101", ResourceFactory.getProperty("gz.columns.a0101"), 90, "", "A", 0, 0);
            column.setTextAlign("left");
            //锁列
            column.setLocked(true);
            columns.add(column);
            //1-12月
            for (int i = 1; i <= 12; i++) {
                column = gzUtil.getColumnsInfo("amonth_" + i, gzUtil.getUpperMonth(i), 90, "", "N", 10, 2);
                column.setTextAlign("right");
                columns.add(column);
            }

            //合计
            column = gzUtil.getColumnsInfo("sumdata", ResourceFactory.getProperty("gz.gz_acounting.total"), 100, "", "N", 10, 2);
            column.setTextAlign("right");
            columns.add(column);
            //月平均
            column = gzUtil.getColumnsInfo("avgdata", ResourceFactory.getProperty("gz.analysistable.monthavg"), 100, "", "N", 10, 2);
            column.setTextAlign("right");
            columns.add(column);
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
        return columns;
    }

    @Override
    public ArrayList getDataList(String salaryids, String year,
                                 String itemid, String nbases, String scope, String querySql, String sortSql, int limit, int page, int totalCount) {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            //薪资权限条件
            String sqlWhere = gzUtil.getPrivSQL("salaryhistory", nbases, salaryids, "", "");

            StringBuffer sql = new StringBuffer();
            StringBuffer commanSql = new StringBuffer();
            //表格数据sql
            commanSql.append(" select " + Sql_switcher.isnull("b0110", "''") + " as b0110," + Sql_switcher.isnull("e0122", "''") + " as e0122,a0101,a0100,nbase," + itemid + ",");
            commanSql.append(Sql_switcher.month("a00z0"));
            commanSql.append(" as amonth,a0000 from salaryhistory where ");
            commanSql.append(Sql_switcher.year("a00z0"));
            commanSql.append("=?");
            //是否含审批中数据 =1 包含
            if (!"1".equals(scope)) {
                commanSql.append(" and sp_flag='06'");
            }
            commanSql.append(sqlWhere);
            sql.append("select * from (");
            sql.append("select b0110,e0122,a0101,a0100,nbase,a0000,");
            for (int i = 1; i <= 12; i++) {
                sql.append("sum(case when amonth=" + i + " then " + Sql_switcher.isnull(itemid, "0") + " else 0 end) as amonth_" + i);
                sql.append(",");
            }
            sql.append("sum(" + Sql_switcher.isnull(itemid, "0") + ") as sumdata,");
            sql.append("avg(" + Sql_switcher.isnull(itemid, "0") + ") as avgdata");
            sql.append(" from (");
            sql.append("select sum(" + itemid + ") as " + itemid + ", b0110,e0122,a0101,a0100,nbase,amonth,a0000");
            sql.append(" from (");
            sql.append(commanSql);
            sql.append(" union all ");
            sql.append(commanSql.toString().replaceAll("salaryhistory", "salaryarchive"));
            sql.append(") untable group by a0100,nbase,b0110,e0122,a0101,amonth,a0000");
            sql.append(") t");
            sql.append(" group by a0100, b0110, e0122, nbase,a0101,a0000) ct");
            if (StringUtils.isNotEmpty(querySql)) {
                sql.append(" where 1=1 " + querySql);
            }
            if (StringUtils.isEmpty(sortSql)) {
                sql.append(" order by ct.a0000");
            } else {
                sql.append(" order by " + sortSql);
            }

            List values = new ArrayList();
            values.add(year);
            values.add(year);
            int seq = 1;
            if (limit > 0 && page > 0) {
                seq = limit*(page-1)+1;
                rs = dao.search(sql.toString(),values, limit, page);
            } else {
                rs = dao.search(sql.toString(), values);
            }
            LazyDynaBean bean = null;
            boolean flag = false;
            while (rs.next()) {
                bean = new LazyDynaBean();
                bean.set("seq", String.valueOf(seq));
                bean.set("a0101", rs.getString("a0101"));
                String b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
                String e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
                String bName = AdminCode.getCodeName("UN", b0110);
                String eName = AdminCode.getCodeName("UM", e0122);
                bean.set("b0110", b0110 + "`" + bName);
                bean.set("e0122", e0122 + "`" + eName);
                for (int i = 1; i <= 12; i++) {
                    bean.set("amonth_" + i, rs.getString("amonth_" + i));
                }
                bean.set("sumdata", rs.getString("sumdata"));
                bean.set("avgdata", rs.getString("avgdata"));
                list.add(bean);
                flag = true;
                seq++;
            }
            //有数据时，在循环外增加合计行,只在最后一行加合计
            //有查询条件时不显示总计
            if (StringUtils.isEmpty(querySql) && flag && (totalCount == -1 || (Math.ceil(totalCount/Double.valueOf(limit)) == page))) {
                //总计行
                HashMap totalProject = this.getTotalProject(itemid, year, sqlWhere,scope);
                String totalAvgSql = sql.toString().replace("*","avg(avgdata) as avgdata");
                totalAvgSql = totalAvgSql.substring(0,totalAvgSql.indexOf("order"));
                bean = new LazyDynaBean();
                bean.set("seq", ResourceFactory.getProperty("label.gz.datasum"));
                bean.set("a0101", "");
                for (int i = 1; i <= 12; i++) {
                    bean.set("amonth_" + String.valueOf(i), totalProject.get(String.valueOf(i)) == null ? "" : (String) totalProject.get(String.valueOf(i)));
                }
                bean.set("b0110", "");
                bean.set("e0122", "");
                String total = (String)totalProject.get("allSum");
                bean.set("sumdata", total);
                rs = dao.search(totalAvgSql,values);
                String avgdata = "";
                if(rs.next()){
                    avgdata =PubFunc.divide(rs.getString("avgdata"), "1",2);
                }
                bean.set("avgdata", avgdata);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;

    }

    /**
     * 求分析项目每个月所有人的总合
     *
     * @param itemid
     * @param year
     * @param sqlWhere
     * @return
     */
    private HashMap getTotalProject(String itemid, String year, String sqlWhere,String scope) throws GeneralException {
        HashMap map = new HashMap();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            StringBuffer buf = new StringBuffer();
            StringBuffer sql = new StringBuffer();

            buf.append("select " + itemid + ",");
            buf.append(Sql_switcher.month("a00z0") + " as amonth");
            buf.append(" from salaryhistory where ");
            buf.append(Sql_switcher.year("a00z0"));
            buf.append("=? ");
            //是否含审批中数据 =1 包含
            if (!"1".equals(scope)) {
                buf.append(" and sp_flag='06'");
            }
            buf.append(sqlWhere);


            sql.append("select sum(" + itemid + ") as " + itemid + ",amonth from (");
            sql.append(buf);
            sql.append(" union all ");
            sql.append(buf.toString().replaceAll("salaryhistory", "salaryarchive"));
            sql.append(") t");
            if (Sql_switcher.searchDbServer() == Constant.ORACEL || Sql_switcher.searchDbServer() == Constant.DB2) {
                sql.append(" group by rollup(t.amonth)");
            } else {
                sql.append(" group by t.amonth with rollup ");
            }

            List values = new ArrayList();
            values.add(year);
            values.add(year);

            String all = "";
            rs = dao.search(sql.toString(), values);
            while (rs.next()) {
                if (rs.getString("amonth") == null)
                    continue;
                map.put(rs.getString("amonth"), rs.getString(itemid));
                all = GzAnalyseBo.add(all, rs.getString(itemid), 2);
            }
            map.put("allSum", all);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    @Override
    public List<LazyDynaBean> getSalaryItem(String salaryid) {
        List<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        RowSet rs = null;
        DbWizard dbWizard = new DbWizard(this.conn);
        try {
            String[] salaryids = salaryid.split(",");
            StringBuffer buf = new StringBuffer();
            buf.append("SELECT S.itemid,S.itemtype,max(S.itemlength) as itemlength,max(S.decwidth) as decwidth,max(nWidth) as nwidth,F.codesetid,f.itemdesc from salaryset S,");
            buf.append("fielditem F where S.itemid=F.itemid and S.salaryid in(");
            List values = new ArrayList();
            for (int i = 0; i < salaryids.length; i++) {
                if (i > 0)
                    buf.append(",");
                buf.append("?");
                values.add(salaryids[i]);
            }
            buf.append(") and S.itemid in (select itemid  from fielditem where useflag='1') and ");
            buf.append(" S.itemid in (select itemid from salaryset WHERE itemtype='N') group by S.itemid,F.CodeSetID,F.itemdesc,s.itemtype "
                    + " ORDER BY S.itemid");
            ContentDAO dao = new ContentDAO(this.conn);

            rs = dao.search(buf.toString(), values);
            while (rs.next()) {
                String itemid = rs.getString("itemid");
                String itemdesc = rs.getString("itemdesc");
                LazyDynaBean bean = new LazyDynaBean();
                if (this.userView != null && "0".equals(this.userView.analyseFieldPriv(itemid))) {
                    continue;
                }
                if (!dbWizard.isExistField("salaryhistory", itemid, false) || !dbWizard.isExistField("salaryarchive", itemid, false)) {
                    continue;
                }
                bean.set("name", itemdesc);
                bean.set("value", itemid);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    @Override
    public int getDataCount(String salaryids, String year, String itemid, String nbases, String scope, String condSql)throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        int count = 0;
        try {
            GzAnalysisUtil gzUtil = new GzAnalysisUtil(this.conn,this.userView);

            String b_units = this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
            //薪资权限条件
            String sqlWhere = gzUtil.getPrivSQL("salaryhistory", nbases, salaryids, b_units, scope);
            StringBuffer sql = new StringBuffer();
            StringBuffer commanSql = new StringBuffer();
            //表格数据sql

            commanSql.append(" select " + Sql_switcher.isnull("b0110", "''") + " as b0110," + Sql_switcher.isnull("e0122", "''") + " as e0122,a0101,a0100,nbase," + itemid + ",");
            commanSql.append(Sql_switcher.month("a00z0"));
            commanSql.append(" as amonth from salaryhistory where ");
            commanSql.append(Sql_switcher.year("a00z0"));
            commanSql.append("=?");
            //是否含审批中数据 =1 包含
            if (!"1".equals(scope)) {
                commanSql.append(" and sp_flag='06'");
            }
            commanSql.append(sqlWhere);
            sql.append("select count(*) as cnum from (");
            sql.append("select max(b0110) as b0110,max(e0122) as e0122,max(a0101) as a0101,max(a0100) as a0100,max(nbase) as nbase,");
            for (int i = 1; i <= 12; i++) {
                sql.append("sum(case when amonth=" + i + " then " + Sql_switcher.isnull(itemid, "0") + " else 0 end) as amonth_" + i);
                sql.append(",");
            }
            sql.append("sum(" + Sql_switcher.isnull(itemid, "0") + ") as sumdata,");
            sql.append("avg(" + Sql_switcher.isnull(itemid, "0") + ") as avgdata");
            sql.append(" from (");
            sql.append("select sum(" + itemid + ") as " + itemid + ", b0110,e0122,a0101,a0100,nbase,amonth");
            sql.append(" from (");
            sql.append(commanSql);
            sql.append(" union all ");
            sql.append(commanSql.toString().replaceAll("salaryhistory", "salaryarchive"));
            sql.append(") untable group by a0100,nbase,b0110,e0122,a0101,amonth");
            sql.append(") t");
            sql.append(" group by a0100, b0110, e0122, nbase,a0101) ct");
            if (StringUtils.isNotEmpty(condSql)) {
                sql.append(" where 1=1 " + condSql);
            }
            List values = new ArrayList();
            values.add(year);
            values.add(year);

            rs = dao.search(sql.toString(), values);
            if(rs.next()){
                count=rs.getInt("cnum");
            }

        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return count;
    }
}
