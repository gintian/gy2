package com.hjsj.hrms.module.gz.salarytemplate.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytemplate.businessobject.ApprovalSituationBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.module.gz.utils.SalaryUtils;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/***
 * 薪资发放历史数据交易类
 * @author ZhangHua
 * @date 13:45 2018/8/15
 */

public class ApprovalSituationTrans extends IBusiness {

    //数据范围类型
    private ScopeType scopeType;

    @Override
    public void execute() throws GeneralException {
        String salaryid = (String) SalaryUtils.decodeParam("salaryid", this.getFormHM());
        //薪资和保险区分标识  1：保险  否则是薪资
        String imodule = (String) SalaryUtils.decodeParam("imodule", this.getFormHM());
        /** optType:操作
         * INIT 历史信息主页面加载
         * FILTER 历史数据主页面过滤
         * DETAIL 明细页面
         * QUERYPERSON 人员查询页面
         * DETAILFILTER 明细页面筛选
         * QUERYPERSONFILTER 人员查询页面筛选
         * QUERYPERSONCHANGETIME 人员查询页面时间切换
         */
        String optType = (String) this.getFormHM().get("optType");
        if (StringUtils.isBlank(optType))
            optType = (String) SalaryUtils.decodeParam("optType", this.getFormHM());
        // 页面区分 0:薪资发放  1:审批  2:上报
        String viewtype = (String) SalaryUtils.decodeParam("viewtype", this.getFormHM());

        SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
        //账套管理员
        String manager = gzbo.getManager();
        ApprovalSituationBo approvalSituationBo = new ApprovalSituationBo(this.getFrameconn(), this.getUserView(), salaryid);
        String username=this.getUserView().getHm().containsKey("selfUsername")&&StringUtils.isNotBlank((String) this.getUserView().getHm().get("selfUsername"))?
                (String) this.getUserView().getHm().get("selfUsername"):this.getUserView().getUserName();
        //是否是账套管理员
        boolean isManager = true;
        if (StringUtils.isNotBlank(manager) && !username.equalsIgnoreCase(manager)) {
            isManager = false;
        }

        //是否启用应用机构
        boolean isControl = approvalSituationBo.getAgencyList(salaryid, "").size() == 0 ? false : true;

        this.initScopeType(manager, isControl);

        //历史信息主页面加载
        if ("INIT".equalsIgnoreCase(optType)) {
            //获取账套所有已提交数据年份
            ArrayList<CommonData> yearList = approvalSituationBo.getSalaryHistroyYear(salaryid, manager);
            if (yearList.size() == 0) {
                //"没有历史数据！"
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.notHistoryData")));
            }
            //取第一个年份
            String filterYear = yearList.get(0).getDataValue();

            ArrayList<String> filterAgencyList = new ArrayList();
            if (this.scopeType == ScopeType.SELFORGDATA) {
                filterAgencyList = approvalSituationBo.getAgencyList(salaryid, username);
            } else if (this.scopeType == ScopeType.ALLORGDATA) {
                filterAgencyList = approvalSituationBo.getAgencyList(salaryid, "");
            }

            if (filterAgencyList.size() == 0 && (this.scopeType == ScopeType.SELFORGDATA || this.scopeType == ScopeType.ALLORGDATA)) {
                //"没有操作权限，请联系管理员！"
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.notAgencyPurview")));
            }
            //获取过滤sql
            String filterSql = this.getFilterSql("SALARYHISTORY", salaryid, filterYear, filterAgencyList, gzbo);


            //获取是否会涉及到归档表
            boolean isNeedSalaryarchive = approvalSituationBo.isNeedSalaryarchive(filterSql);

            //获取列
            ArrayList<ColumnsInfo> columnsInfos = this.getColumns(salaryid, isManager, gzbo.getCtrlparam(), isControl);
            //拼接查询sql
            String strSql = this.getStrSql(filterSql, isNeedSalaryarchive, columnsInfos, isManager, isControl);


            TableConfigBuilder builder = new TableConfigBuilder("salaryApproval_" + salaryid, columnsInfos, "ApprovalSituation", userView, this.getFrameconn());
            if (isManager || !isControl) {
                builder.setDataSql(strSql);
                builder.setOrderBy(" ORDER BY A00Z2 DESC,A00Z3 ");
            } else {
                ArrayList list = approvalSituationBo.getSelfHelpDataList(strSql, filterYear, columnsInfos, filterAgencyList, gzbo.getCtrlparam());
                builder.setDataList(list);
            }
            builder.setColumnFilter(true);
            builder.setSortable(true);
            //栏目设置渲染位置
            builder.setSchemePosition("ApprovalSituation_schemeSetting");
            //栏目设置关闭回调
            builder.setSchemeSaveCallback("ApprovalSituation.closeSettingWindow");
            builder.setAutoRender(false);
            builder.setScheme(true);
            builder.setEditable(false);
            builder.setSetScheme(true);
            builder.setSelectable(false);
            builder.setLockable(true);
            builder.setRowdbclick("ApprovalSituation.rowclick");


            String config = builder.createExtTableConfig();

            ArrayList agencyList = new ArrayList();
            if (this.scopeType == ScopeType.ALLORGDATA) {
                agencyList = approvalSituationBo.getAgency(salaryid, "");
                //发起人是自己的应该也能查出username是自己的机构
            } else if (this.scopeType == ScopeType.SELFORGDATA || this.scopeType == ScopeType.AUTHORISMYSELF) {
                agencyList = approvalSituationBo.getAgency(salaryid, username);
            }
            //应用机构下拉列表数据
            this.getFormHM().put("AgencyList", agencyList);

            this.getFormHM().put("yearList", yearList);

            this.getFormHM().put("tableConfig", config);

            this.getFormHM().put("isNeedSalaryarchive", isNeedSalaryarchive ? "1" : "0");

            String isShowPublicPlan = "0";
            if ("2".equals(viewtype) && this.userView.hasTheFunction("031408")) {
                isShowPublicPlan = "1";
            } else if ("0".equals(viewtype)) {
                if (("1".equals(imodule) && this.userView.hasTheFunction("3250215")) ||
                        (!"1".equals(imodule) && this.userView.hasTheFunction("324020801"))) {
                    isShowPublicPlan = "1";
                }
            }
            this.getFormHM().put("isShowPublicPlan", isShowPublicPlan);
            this.getFormHM().put("isNeedSalaryarchive", isNeedSalaryarchive ? "1" : "0");

        }
        //历史数据主页面过滤
        else if ("FILTER".equalsIgnoreCase(optType)) {
            //过滤年份
            String filterYear = (String) this.getFormHM().get("filterYear");
            //过滤机构
            String agencyFilter = (String) this.getFormHM().get("filterAgency");
            ArrayList<String> agencyFilterList = this.getAgencyFilter(agencyFilter, salaryid, approvalSituationBo);
            TableDataConfigCache tableDataConfigCache = (TableDataConfigCache) userView.getHm().get("salaryApproval_" + salaryid);

            String filterSql = this.getFilterSql("SALARYHISTORY", salaryid, filterYear, agencyFilterList, gzbo);

            boolean isNeedSalaryarchive = approvalSituationBo.isNeedSalaryarchive(filterSql);
            String strSql = this.getStrSql(filterSql, isNeedSalaryarchive, tableDataConfigCache.getTableColumns(), isManager, isControl);
            if (isManager || !isControl) {
                tableDataConfigCache.setTableSql(strSql);
            } else {
                ArrayList list = approvalSituationBo.getSelfHelpDataList(strSql, filterYear, tableDataConfigCache.getTableColumns(), agencyFilterList, gzbo.getCtrlparam());
                tableDataConfigCache.setTableData(list);
            }
            this.getFormHM().put("isNeedSalaryarchive", isNeedSalaryarchive ? "1" : "0");
        }
        //人员查询页面&&明细页面
        else if ("DETAIL".equalsIgnoreCase(optType) || "QUERYPERSON".equalsIgnoreCase(optType)) {
            String strSql = "";
            ArrayList<ColumnsInfo> columnsInfos = this.getDetailsColumns(salaryid);
            boolean isNeedSalaryarchive = "1".equalsIgnoreCase((String) SalaryUtils.decodeParam("isNeedSalaryarchive", this.getFormHM())) ? true : false;
            if ("DETAIL".equalsIgnoreCase(optType)) {
                //业务日期
                String appdate = (String) SalaryUtils.decodeParam("appdate", this.getFormHM());
                //发放次数
                String count = (String) SalaryUtils.decodeParam("count", this.getFormHM());
                //机构
                String agencyFilter = (String) SalaryUtils.decodeParam("agencyFilter", this.getFormHM());
                ArrayList<String> agencyFilterList = this.getAgencyFilter(agencyFilter, salaryid, approvalSituationBo);

                String filterSql = this.getDetailsFilterSql("SALARYHISTORY", salaryid, appdate, count, agencyFilterList, gzbo.getCtrlparam());
                if (this.scopeType == ScopeType.INPURVIEW) {
                    filterSql += gzbo.getWhlByUnits("SALARYHISTORY", true);
                } else if (this.scopeType == ScopeType.AUTHORISMYSELF) {
                    filterSql += " AND UPPER(SALARYHISTORY.USERFLAG) ='" + this.getUserView().getUserName().toUpperCase() + "'";
                }
                strSql = this.getDetailsSql(columnsInfos, filterSql, isNeedSalaryarchive);
            }

            TableConfigBuilder builder = new TableConfigBuilder("salaryApprovalDetail_" + salaryid, columnsInfos, "SalaryDetails", userView, this.getFrameconn());
            if ("QUERYPERSON".equalsIgnoreCase(optType)) {
                //若为人员过滤，初次加载不显示数据
                builder.setDataSql("select * from salaryhistory where 1=2");
            } else {
                builder.setDataSql(strSql);
                builder.setOrderBy(" order by dbid, a0000, A00Z2, a00z3 ");

            }
            builder.setColumnFilter(true);
            builder.setSortable(true);
            //栏目设置渲染位置
            builder.setSchemePosition("SalaryDetails_schemeSetting");
            //栏目设置关闭回调
            builder.setSchemeSaveCallback("SalaryDetails.closeSettingWindow");
            builder.setAutoRender(false);
            builder.setScheme(true);
            builder.setEditable(false);
            builder.setSetScheme(true);
            builder.setSelectable(false);
            builder.setLockable(true);
            builder.setTableTools(new ArrayList());

            String config = builder.createExtTableConfig();
            this.getFormHM().put("tableConfig", config);

            this.getFormHM().put("lookStr", approvalSituationBo.getLookStr(gzbo.getCtrlparam()));

            String isShowPublicPlan = "0";
            if ("2".equals(viewtype) && this.userView.hasTheFunction("031408")) {
                isShowPublicPlan = "1";
            } else if ("0".equals(viewtype)) {
                if (("1".equals(imodule) && this.userView.hasTheFunction("3250215")) ||
                        (!"1".equals(imodule) && this.userView.hasTheFunction("324020801"))) {
                    isShowPublicPlan = "1";
                }
            }
            this.getFormHM().put("isShowPublicPlan", isShowPublicPlan);
            this.getFormHM().put("isNeedSalaryarchive", isNeedSalaryarchive ? "1" : "0");


        }
        //人员查询页面筛选&&详情页面筛选
        else if ("DETAILFILTER".equalsIgnoreCase(optType) || "QUERYPERSONFILTER".equalsIgnoreCase(optType)) {
            //唯一性指标值
            String onlyname = approvalSituationBo.getOnlyName(gzbo.getCtrlparam());
            TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get("salaryApprovalDetail_" + salaryid);
            ArrayList<ColumnsInfo> columnsInfos = tableCache.getTableColumns();
            if (StringUtils.isNotBlank(onlyname)) {
                boolean ishave = false;
                for (ColumnsInfo columnsInfo : columnsInfos) {
                    if (columnsInfo.getColumnId().equalsIgnoreCase(onlyname)) {
                        ishave = true;
                        break;
                    }
                }
                if (!ishave) {
                    onlyname = "";
                }
            }
            String filterSql = SalaryUtils.getQueryBoxSql(this.getFormHM(), this.getUserView(), "salaryApprovalDetail_" + salaryid, "SalaryDetails", onlyname);
            //页面模糊查询
            boolean isNeedSalaryarchive = "1".equalsIgnoreCase((String) SalaryUtils.decodeParam("isNeedSalaryarchive", this.getFormHM())) ? true : false;

            //人员查询页面
            if ("QUERYPERSONFILTER".equalsIgnoreCase(optType) && StringUtils.isNotBlank(filterSql)) {
                String agencyFilter = (String) SalaryUtils.decodeParam("agencyFilter", this.getFormHM());
                ArrayList<String> filterAgencyList = this.getAgencyFilter(agencyFilter, salaryid, approvalSituationBo);

                String fsql = this.getFilterSql("SALARYHISTORY", salaryid, "", filterAgencyList, gzbo);

                //起始年份
                String syear = (String) SalaryUtils.decodeParam("stime_year", this.getFormHM());
                //起始月份
                String smonth = (String) SalaryUtils.decodeParam("stime_month", this.getFormHM());
                //结束年份
                String eyear = (String) SalaryUtils.decodeParam("etime_year", this.getFormHM());
                //结束月份
                String emonth = (String) SalaryUtils.decodeParam("etime_month", this.getFormHM());
                StringBuffer sqlTime = new StringBuffer();
                sqlTime.append(" AND A00Z2 BETWEEN ").append(Sql_switcher.charToDate("'" + syear + "-" + smonth + "-01'")).append(" AND ");
                sqlTime.append(Sql_switcher.charToDate("'" + eyear + "-" + emonth + "-01'"));
                fsql += sqlTime.toString();

                String strSql = this.getDetailsSql(tableCache.getTableColumns(), fsql, isNeedSalaryarchive);
                tableCache.setTableSql(strSql);
                tableCache.setSortSql(" order by nbase,A0100,a00z2,a00z3 ");
            } else if ("QUERYPERSONFILTER".equalsIgnoreCase(optType)) {
                tableCache.setTableSql(" select * from salaryhistory where 1=2 ");
            }


            //去掉表名，防止表格工具追加后报错
            if (StringUtils.isNotBlank(filterSql)) {
                tableCache.setQuerySql(filterSql.replaceAll("data.", ""));
            } else {
                tableCache.setQuerySql("");
            }

            this.userView.getHm().put(SafeCode.encode(PubFunc.encrypt("salaryApprovalDetail_" + salaryid)), tableCache);

        }
        //人员查询页面时间切换
        else if ("QUERYPERSONCHANGETIME".equalsIgnoreCase(optType)) {
            TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get("salaryApprovalDetail_" + salaryid);
            String agencyFilter = (String) SalaryUtils.decodeParam("agencyFilter", this.getFormHM());
            ArrayList<String> filterAgencyList = this.getAgencyFilter(agencyFilter, salaryid, approvalSituationBo);

            String fsql = this.getFilterSql("SALARYHISTORY", salaryid, "", filterAgencyList, gzbo);

            //起始年份
            String syear = (String) SalaryUtils.decodeParam("stime_year", this.getFormHM());
            //起始月份
            String smonth = (String) SalaryUtils.decodeParam("stime_month", this.getFormHM());
            //结束年份
            String eyear = (String) SalaryUtils.decodeParam("etime_year", this.getFormHM());
            //结束月份
            String emonth = (String) SalaryUtils.decodeParam("etime_month", this.getFormHM());
            StringBuffer sqlTime = new StringBuffer();
            sqlTime.append(" AND A00Z2 BETWEEN ").append(Sql_switcher.charToDate("'" + syear + "-" + smonth + "-01'")).append(" AND ");
            sqlTime.append(Sql_switcher.charToDate("'" + eyear + "-" + emonth + "-01'"));
            fsql += sqlTime.toString();
            boolean isNeedSalaryarchive = approvalSituationBo.isNeedSalaryarchive(fsql);
            if (StringUtils.isNotBlank(tableCache.getQuerySql())) {

                String strSql = this.getDetailsSql(tableCache.getTableColumns(), fsql, isNeedSalaryarchive);
                tableCache.setTableSql(strSql);
                tableCache.setSortSql(" ORDER BY NBASE,A0100,A00Z2,A00Z3 ");
            } else {
                tableCache.setTableSql(" select * from salaryhistory where 1=2 ");
            }
            this.userView.getHm().put(SafeCode.encode(PubFunc.encrypt("salaryApprovalDetail_" + salaryid)), tableCache);
            this.getFormHM().put("isNeedSalaryarchive", isNeedSalaryarchive ? "1" : "0");

        }
        //导出汇总记录
        else if ("ExportExcel".equalsIgnoreCase(optType)) {

            //过滤年份
            String filterYear = this.getFormHM().containsKey("filterYear") ? (String) this.getFormHM().get("filterYear") : "";
            //过滤机构
            String agencyFilter = this.getFormHM().containsKey("filterAgency") ? (String) this.getFormHM().get("filterAgency") : "";

            ArrayList<String> filterAgencyList = this.getAgencyFilter(agencyFilter, salaryid, approvalSituationBo);
            TableDataConfigCache tableDataConfigCache = (TableDataConfigCache) userView.getHm().get("salaryApproval_" + salaryid);
            boolean isNeedSalaryarchive = "1".equalsIgnoreCase((String) SalaryUtils.decodeParam("isNeedSalaryarchive", this.getFormHM())) ? true : false;
            ArrayList<ColumnsInfo> columnsInfos = tableDataConfigCache.getDisplayColumns();
            String filterSql = this.getFilterSql("SALARYHISTORY", salaryid, filterYear, filterAgencyList, gzbo);

            //拼接查询sql
            String strSql = this.getStrSql(filterSql, isNeedSalaryarchive, columnsInfos, isManager, isControl);

            ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn(), this.getUserView());
            //页面显示0.导出也显示0
            //excelUtil.setConvertToZero(false);

            ArrayList headList = this.buildExcelColumn(columnsInfos, "1", salaryid, gzbo);

            String fileName = filterYear + "年历史数据_" + this.getUserView().getUserName() + ".xls";
            if (isManager || !isControl) {
                try {
                    StringBuffer str = new StringBuffer();
                    str.append(" select * from ( ").append(strSql).append(" )t ORDER BY A00Z2 DESC,A00Z3 ");
                    excelUtil.exportExcelBySql(fileName, "", null, headList, str.toString(), null, 0);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ArrayList list = approvalSituationBo.getSelfHelpDataList(strSql, filterYear, tableDataConfigCache.getTableColumns(), filterAgencyList, gzbo.getCtrlparam());
                ArrayList dataList = this.buildExportData(list, headList);
                try {
                    excelUtil.exportExcel(fileName, "", null, headList, dataList, null, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));

        }
        //导出明细数据
        else if ("ExportDetailsExcel".equalsIgnoreCase(optType)) {
            TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get("salaryApprovalDetail_" + salaryid);
            String strSql = " select * from (" + tableCache.getTableSql();
            if (StringUtils.isNotBlank(tableCache.getQuerySql())) {
                strSql += " " + tableCache.getQuerySql();
            }
            if (StringUtils.isNotBlank(tableCache.getFilterSql())) {
                strSql += " " + tableCache.getFilterSql();
            }
            strSql += ") t";
            if (StringUtils.isNotBlank(tableCache.getSortSql())) {
                strSql += " " + tableCache.getSortSql();
            }
            ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn(), this.getUserView());
            //excelUtil.setConvertToZero(false);
            String fileName = "人员明细数据_" + this.getUserView().getUserName() + ".xls";

            ArrayList headList = this.buildExcelColumn(tableCache.getDisplayColumns(), "2", salaryid, gzbo);
            try {
                excelUtil.exportExcelBySql(fileName, "", null, headList, strSql, null, 0);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
        }

    }

    /**
     * 获取过滤sql
     *
     * @param tableName
     * @param salaryid
     * @param filterYear   年份
     * @param filterAgency 机构
     * @param gzbo
     * @return
     * @author ZhangHua
     * @date 21:20 2018/8/28
     */
    private String getFilterSql(String tableName, String salaryid, String filterYear, ArrayList<String> filterAgency, SalaryTemplateBo gzbo) {
        StringBuffer strSql = new StringBuffer();
        strSql.append(" AND " + tableName + ".SALARYID =").append(salaryid).append(" ");
        if (StringUtils.isNotBlank(filterYear)) {
            strSql.append(" AND ").append(Sql_switcher.year(tableName + ".a00z2")).append(" = '").append(filterYear).append("'");
        }
        if (filterAgency.size() != 0) {
            ApprovalSituationBo bo = new ApprovalSituationBo(this.getFrameconn(), this.getUserView());
            strSql.append(" AND ( ");
            strSql.append(bo.buildstrSql(tableName, filterAgency, gzbo.getCtrlparam()));
            strSql.append(")");
        }

        if (this.scopeType == ScopeType.INPURVIEW) {
            strSql.append(gzbo.getWhlByUnits("SALARYHISTORY", true));
        } else if (this.scopeType == ScopeType.AUTHORISMYSELF) {
            strSql.append(" AND UPPER(SALARYHISTORY.USERFLAG) ='" + this.getUserView().getUserName().toUpperCase() + "'");
        }
        return strSql.toString();
    }

    /**
     * 获取数据sql
     *
     * @param filterSql
     * @param isNeedSalaryarchive 是否涉及归档表
     * @param columnsInfos
     * @param isManager           是否是管理员
     * @return
     */
    private String getStrSql(String filterSql, boolean isNeedSalaryarchive, ArrayList<ColumnsInfo> columnsInfos, boolean isManager, boolean isControl) {
        StringBuffer strSql = new StringBuffer();

        StringBuffer itemSql = new StringBuffer();
        if (isManager || !isControl) {
            String ruleOut = "a00z2,a00z3,num,details,a0000";

            for (ColumnsInfo columnsInfo : columnsInfos) {
                String itemId = columnsInfo.getColumnId();
                if (ruleOut.indexOf(itemId.toLowerCase()) == -1)
                    itemSql.append("sum(").append(itemId).append(") as ").append(itemId).append(" ,");
            }

            strSql.append(" SELECT A.*,B.NUM FROM  (");
            strSql.append(" SELECT ").append(itemSql).append(Sql_switcher.dateToChar("A00Z2", "YYYY-MM-DD") + " AS A00Z2,A00Z3 ");
            strSql.append(" FROM SALARYHISTORY WHERE 1=1 ").append(filterSql);
            strSql.append(" GROUP BY A00Z2,A00Z3 HAVING COUNT(*) >0 ");
            strSql.append(" ) A ");

            strSql.append(" LEFT JOIN (SELECT COUNT(*) AS NUM," + Sql_switcher.dateToChar("MAX(A00Z2)", "YYYY-MM-DD") + " AS A00Z2 , MAX(A00Z3) AS A00Z3 FROM ");
            strSql.append(" (SELECT  A00Z2,A00Z3 FROM SALARYHISTORY S WHERE 1=1 ").append(filterSql.toUpperCase().replaceAll("SALARYHISTORY", "S"));
            strSql.append(" GROUP BY UPPER(NBASE),A0100,A00Z2,A00Z3) C GROUP BY A00Z2,A00Z3  ) B ON A.A00Z2=B.A00Z2 AND A.A00Z3=B.A00Z3  ");

        } else {
            String ruleOut = "agencyid,agencyname,num,details";
            for (ColumnsInfo columnsInfo : columnsInfos) {
                if (ruleOut.indexOf(columnsInfo.getColumnId()) == -1) {
                    if ("D".equalsIgnoreCase(columnsInfo.getColumnType())) {
                        itemSql.append(Sql_switcher.dateToChar(columnsInfo.getColumnId(), "YYYY-MM-DD")).append(" as ").append(columnsInfo.getColumnId()).append(" , ");
                    } else {
                        itemSql.append(columnsInfo.getColumnId()).append(",");
                    }
                }
            }
            strSql.append(" SELECT ").append(itemSql).append(" 1 AS NUM ,'' AS DETAILS ,'' AS AGENCYID,'' AS AGENCYNAME ");
            strSql.append(" FROM SALARYHISTORY WHERE 1=1 ").append(filterSql);

        }

        if (isNeedSalaryarchive) {
            String c_strSql = strSql.toString().toUpperCase().replaceAll("SALARYHISTORY", "SALARYARCHIVE");
            strSql.append(" UNION ALL ");
            strSql.append(c_strSql);
        }
        return strSql.toString();
    }


    private ArrayList<ColumnsInfo> getColumns(String salaryid, boolean isManager, SalaryCtrlParamBo ctrlParamBo, boolean isControl) {

        ArrayList<FieldItem> fieldItems = new ArrayList();

        SalaryTemplateBo bo = new SalaryTemplateBo(this.getFrameconn(), this.getUserView());
        ArrayList setList = bo.getSalaryItemList(" UPPER(ITEMTYPE)='N' ", salaryid + "", 1);
        FieldItem fielditem;

        fielditem = new FieldItem();
        fielditem.setItemid("A00Z2");
        fielditem.setItemdesc(ResourceFactory.getProperty("label.gz.appdate"));//"业务日期"
        fielditem.setItemtype("D");
        fielditem.setItemlength(10);
        fielditem.setAlign("left");
        fieldItems.add(fielditem);

        fielditem = new FieldItem();
        fielditem.setItemid("A00Z3");
        fielditem.setItemdesc(ResourceFactory.getProperty("hmuster.label.counts"));//次数"
        fielditem.setItemtype("N");
        fielditem.setItemlength(50);
        fieldItems.add(fielditem);

        fielditem = new FieldItem();
        fielditem.setItemid("num");
        fielditem.setItemdesc(ResourceFactory.getProperty("menu.gz.personnum"));//"人数"
        fielditem.setItemtype("N");
        fielditem.setItemlength(50);
        fieldItems.add(fielditem);

        if (!isManager && isControl) {
            String orgid = "";
            String deptid = "";
            //归属单位
            orgid = ctrlParamBo.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid");
            //归属部门
            deptid = ctrlParamBo.getValue(SalaryCtrlParamBo.SUM_FIELD, "deptid");
            if (StringUtils.isBlank(orgid)) {
                orgid = "B0110";
            }
            if (StringUtils.isBlank(deptid)) {
                deptid = "E0122";
            }
            fielditem = (FieldItem) DataDictionary.getFieldItem(orgid).clone();
            fielditem.setVisible(false);
            fieldItems.add(fielditem);

            fielditem = (FieldItem) DataDictionary.getFieldItem(deptid).clone();
            fielditem.setVisible(false);
            fieldItems.add(fielditem);

            fielditem = new FieldItem();
            fielditem.setItemid("agencyId");
            fielditem.setItemdesc("");//机构id
            fielditem.setItemtype("A");
            fielditem.setItemlength(50);
            fielditem.setVisible(false);
            fieldItems.add(fielditem);

            fielditem = new FieldItem();
            fielditem.setItemid("agencyName");
            fielditem.setItemdesc(ResourceFactory.getProperty("report.organization.name"));//"机构名称"
            fielditem.setItemtype("A");
            fielditem.setAlign("left");
            fielditem.setItemlength(50);
            fielditem.setCodesetid("UN");
            fieldItems.add(fielditem);
        }


        String ruleOut = "'A00Z1','A00Z0','A00Z2','A00Z3','A01Z0'";
        for (int i = 0; i < setList.size(); i++) {
            LazyDynaBean bean = (LazyDynaBean) setList.get(i);
            String itemid = (String) bean.get("itemid");

            if (ruleOut.indexOf("'" + itemid.toUpperCase() + "'") != -1) {
                continue;
            }
            FieldItem tempItem = DataDictionary.getFieldItem(itemid.toLowerCase());
            if (tempItem == null) {
                continue;
            }
            fieldItems.add(tempItem);

        }
        ArrayList<ColumnsInfo> columnsInfos = SalaryUtils.convertFieldItemToColumnsInfo(fieldItems);

        //默认锁列的字段
        String lockStr = ",agencyname,a00z2,a00z3,num,";
        //默认隐藏的字段（栏目设置可以设置成显示）
        for (ColumnsInfo info : columnsInfos) {
            String id = info.getColumnId();
            if (lockStr.indexOf(id.toLowerCase()) != -1) {
                info.setLocked(true);
            } else if ("N".equalsIgnoreCase(info.getColumnType()) && info.getLoadtype() == ColumnsInfo.LOADTYPE_BLOCK) {
                info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
            }

            if ("A00Z2".equalsIgnoreCase(info.getColumnId()) || "A00Z3".equalsIgnoreCase(info.getColumnId())) {
                info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
            }
            //取消过滤
            info.setFilterable(false);
            //取消排序
            info.setSortable(false);

        }

        ColumnsInfo columnsInfo = SalaryUtils.getColumnsInfo("details", "", "详情", 80, "A",
                0, true, ColumnsInfo.LOADTYPE_BLOCK, "center", 10);
        columnsInfo.setRendererFunc("ApprovalSituation.buildDetails");
        columnsInfo.setFilterable(false);
        columnsInfo.setSortable(false);
        columnsInfos.add(columnsInfo);
        return columnsInfos;
    }


    /**
     * 获取明细页面列
     *
     * @param salaryid
     * @return
     */
    private ArrayList<ColumnsInfo> getDetailsColumns(String salaryid) {
        SalarySetBo setbo = new SalarySetBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
        ArrayList<FieldItem> itemList = setbo.searchGzItem();
        ArrayList<ColumnsInfo> columnsInfos = SalaryUtils.convertFieldItemToColumnsInfo(itemList);
        ArrayList<ColumnsInfo> columnsInfos_t = new ArrayList();
        //默认锁列的字段
        String lockStr = ",a0101,b0110,e0122,a00z2,a00z3,";
        //默认隐藏的字段（栏目设置可以设置成显示）
        String hiddenStr = ",a00z0,a00z1,";
        //不显示的字段
        String removeStr = "add_flag,sp_flag,sp_flag2,";
        for (ColumnsInfo info : columnsInfos) {
            String id = info.getColumnId();
            if("N".equalsIgnoreCase(info.getColumnType())) {
            	info.setDefaultValue("0");
            }
            if (removeStr.indexOf(id.toLowerCase()) != -1) {
                continue;
            }
            if (lockStr.indexOf(id.toLowerCase()) != -1) {
                info.setLocked(true);
            }
            if (hiddenStr.indexOf(id.toLowerCase()) != -1) {
                info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
            }
            info.setQueryable(false);
            columnsInfos_t.add(info);
        }
        return columnsInfos_t;

    }

    /**
     * 获取明细页面sql
     *
     * @param columnsInfos
     * @param filterSql
     * @param isNeedSalaryarchive 是否涉及归档表
     * @return
     */
    private String getDetailsSql(ArrayList<ColumnsInfo> columnsInfos, String filterSql, boolean isNeedSalaryarchive) {
        StringBuffer strSql = new StringBuffer();

        strSql.append(" SELECT dbid,");
        for (ColumnsInfo columnsInfo : columnsInfos) {
            strSql.append(columnsInfo.getColumnId()).append(" ,");
        }
        strSql.deleteCharAt(strSql.length() - 1);
        strSql.append(" FROM SALARYHISTORY WHERE 1=1 ");
        if (StringUtils.isNotBlank(filterSql)) {
            strSql.append(filterSql);
        }
        if (isNeedSalaryarchive) {
            String str = strSql.toString().toUpperCase().replaceAll("SALARYHISTORY", "SALARYARCHIVE");
            strSql.append(" UNION ALL ").append(str);
        }

        return strSql.toString();

    }

    /**
     * 获取明细页面过滤sql
     *
     * @param tableName
     * @param salaryid
     * @param appDate
     * @param count
     * @param filterAgency 机构
     * @param ctrlparam
     * @return
     */
    private String getDetailsFilterSql(String tableName, String salaryid, String appDate, String count, ArrayList<String> filterAgency, SalaryCtrlParamBo ctrlparam) {
        StringBuffer strSql = new StringBuffer();
        strSql.append(" AND ").append("a00z2 = ").append(Sql_switcher.charToDate("'" + appDate + "'")).append(" AND a00z3 =").append("'" + count + "'");
        strSql.append(" AND SALARYID =").append(salaryid).append(" ");

        if (filterAgency.size() != 0) {
            ApprovalSituationBo bo = new ApprovalSituationBo(this.getFrameconn(), this.getUserView());
            strSql.append(" AND ( ");
            strSql.append(bo.buildstrSql(tableName, filterAgency, ctrlparam));
            strSql.append(")");
        }

        return strSql.toString();
    }


    private ArrayList buildExportData(ArrayList list, ArrayList<LazyDynaBean> headList) {
        ArrayList<LazyDynaBean> dataList = new ArrayList();
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
        String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
        if (StringUtils.isBlank(display_e0122) || "00".equals(display_e0122))
            display_e0122 = "0";
        for (int i = 0; i < list.size(); i++) {
            LazyDynaBean rowBean = (LazyDynaBean) list.get(i);
            LazyDynaBean t_rowBean = new LazyDynaBean();
            for (LazyDynaBean bean : headList) {
                String content = String.valueOf(rowBean.get((String) bean.get("itemid")));
                LazyDynaBean dynaBean = new LazyDynaBean();
                String codesetid = (String) bean.get("codesetid");

                if ("A".equalsIgnoreCase((String) bean.get("colType")) && StringUtils.isNotBlank(codesetid) && !"0".equals(codesetid)) {
                    if (StringUtils.isNotBlank(codesetid)) {
                        //此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值
                        if ("um".equalsIgnoreCase(codesetid)) {
                            String theUM = "";
                            if (Integer.parseInt(display_e0122) == 0)
                                theUM = AdminCode.getCodeName("UM", content);
                            else {
                                CodeItem item = AdminCode.getCode("UM", content, Integer.parseInt(display_e0122));
                                if (item != null) {
                                    theUM = StringUtils.isBlank(theUM) ? AdminCode.getCode("UN", content, Integer.parseInt(display_e0122)).getCodeitem() : theUM;
                                } else {
                                    theUM = AdminCode.getCodeName("UM", content);
                                    if (StringUtils.isBlank(theUM))
                                        theUM = AdminCode.getCodeName("UN", content);
                                }
                            }
                            content = theUM;
                        } else {
                            String value = "";
                            //如果设置了display_e0122按display_e0122走 sunjian 2017-07-01
                            if ("UN".equalsIgnoreCase(codesetid)) {
                                CodeItem item = AdminCode.getCode("UN", content, Integer.parseInt(display_e0122));
                                if (item != null)
                                    value = item.getCodename();
                                else
                                    value = AdminCode.getCodeName("UN", content);
                                if (StringUtils.isBlank(value))
                                    value = AdminCode.getCodeName("UM", content);
                            } else {
                                value = AdminCode.getCodeName(codesetid, content);
                            }
                            content = value;

                        }
                    }
                }

                dynaBean.set("content", content);
                t_rowBean.set((String) bean.get("itemid"), dynaBean);
            }
            dataList.add(t_rowBean);

        }
        return dataList;
    }

    /**
     * @param columnsInfos
     * @param Type         1 导出汇总， 2导出明细
     * @return
     * @throws GeneralException 
     */
    private ArrayList buildExcelColumn(ArrayList<ColumnsInfo> columnsInfos, String Type, String salaryid, SalaryTemplateBo gzbo) throws GeneralException {
    	ArrayList<LazyDynaBean> headList = new ArrayList();
    	ArrayList<ColumnsInfo> columnsInfos1 = new ArrayList();
    	int num = 0;
    	HashMap<String, Integer> width_map = new HashMap<String, Integer>();
    	String columnid = "";
    	try {
	      //栏目设置已经存在，则从数据库中取
    		if("1".equals(Type))
    			columnid = "salaryApproval_" + salaryid;
    		else
    			columnid = "salaryApprovalDetail_" + salaryid;
			int schemeId = gzbo.getSchemeId(columnid);
			// 从数据库中得到可以显示的薪资项目{id:width}
			if(schemeId > 0){
				width_map = gzbo.getTableItemsWithToMap(schemeId, "1");
			}
	        for (int i = 0; i < columnsInfos.size(); i++) {
	            ColumnsInfo columnsInfo = columnsInfos.get(i);
	            if (columnsInfo.isLocked()) {
	                columnsInfos1.add(num, columnsInfo);
	                num++;
	            } else {
	                columnsInfos1.add(columnsInfo);
	            }
	        }
	        columnsInfos = columnsInfos1;
	
	        for (ColumnsInfo columnsInfo : columnsInfos) {
	            if ("DETAILS".equalsIgnoreCase(columnsInfo.getColumnId()) || (columnsInfo.getLoadtype() != ColumnsInfo.LOADTYPE_BLOCK && columnsInfo.getLoadtype() != ColumnsInfo.LOADTYPE_ALWAYSLOAD)) {
	                continue;
	            }
	            LazyDynaBean bean = new LazyDynaBean();
	            //表头格式
	            HashMap headStyleMap = new HashMap();
	            String columnId = columnsInfo.getColumnId();
	            
	            int columnsWith = columnsInfo.getColumnWidth();
	            if(width_map.size() > 0) {
	            	columnsWith = width_map.get(columnId)==null?columnsWith : width_map.get(columnId);
	            }
	            headStyleMap.put("columnWidth", columnsWith * 30);
	            headStyleMap.put("isFontBold", true);
	            // 该列宽度
	            bean.set("headStyleMap", headStyleMap);
	            //内容单元格格式
	            HashMap colStyleMap = new HashMap();
	            colStyleMap.put("columnWidth", columnsInfo.getColumnWidth() * 30);
	            short align = 1;
	            if (StringUtils.isBlank(columnsInfo.getTextAlign())) {
	                align = 1;
	            } else if ("left".equalsIgnoreCase(columnsInfo.getTextAlign())) {
	                align = 1;
	            } else if ("right".equalsIgnoreCase(columnsInfo.getTextAlign())) {
	                align = 3;
	            } else {
	                align = 2;
	            }
	            colStyleMap.put("align", align);
	            bean.set("colStyleMap", colStyleMap);
	            String decwidth = String.valueOf(columnsInfo.getDecimalWidth());
	            // 列头名称
	            bean.set("content", columnsInfo.getColumnDesc());
	            // 列头代码
	            bean.set("itemid", columnsInfo.getColumnId());
	            // 列头代码
	            bean.set("codesetid", columnsInfo.getCodesetId());
	            //添加合计标识
	            if (columnsInfo.getSummaryType() == ColumnsInfo.SUMMARYTYPE_SUM) {
	                bean.set("total", "1");
	            }
	            // 列小数点后面位数
	            bean.set("decwidth", decwidth);
	            // 该列数据类型
	            if ("A00Z2".equalsIgnoreCase(columnsInfo.getColumnId()) && "1".equals(Type)) {
	                bean.set("colType", "A");
	            } else {
	                bean.set("colType", columnsInfo.getColumnType());
	            }
	            headList.add(bean);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
		}
        return headList;
    }


    /**
     * @param manager
     * @param isControl
     */
    private void initScopeType(String manager, boolean isControl) {
        //是否共享账套
        boolean isShared = StringUtils.isNotBlank(manager) ? true : false;

        //共享
        if (isShared) {
            //启用所属单位
            if (isControl) {
                if (this.getUserView().getUserName().equalsIgnoreCase(manager)) {
                	//对于共享的管理员，直接拿账套里面的所有人就行，因为可能出现管理范围外的人，这样按照应用机构走就错了
                    this.scopeType = ScopeType.AUTHORISMYSELF;
                } else {
                    this.scopeType = ScopeType.SELFORGDATA;
                }
            }
            //不启用所属单位
            else {
                if (this.getUserView().getUserName().equalsIgnoreCase(manager)) {
                    this.scopeType = ScopeType.AUTHORISMYSELF;
                } else {
                    this.scopeType = ScopeType.INPURVIEW;
                }
            }
        }
        //非共享
        else {//对于非共享的，直接拿账套里面的所有人就行，因为可能出现管理范围外的人，这样按照应用机构走就错了
            this.scopeType = ScopeType.AUTHORISMYSELF;
        }

//        1 共享 ｛
//        启用了机构｛
//
//        管理员
//                取全部机构信息
//
//        非管理员
//                取所属机构信息
//
//｝
//        未启用机构｛
//        管理员
//                取发起人是自己
//        非管理员
//                取业务范围内
//｝
//
//｝
//
//        2 非共享｛
//        启用了机构｛
//        取所属机构数据
//｝
//        未启用机构｛
//        取发起人是自己
//｝
//｝

    }

    /**
     * 拼接机构过滤
     *
     * @param agencyFilter
     * @param salaryid
     * @param approvalSituationBo
     * @return
     */
    private ArrayList<String> getAgencyFilter(String agencyFilter, String salaryid, ApprovalSituationBo approvalSituationBo) {
        ArrayList<String> agencyFilterList = new ArrayList();
        if (StringUtils.isNotBlank(agencyFilter) && !"all".equalsIgnoreCase(agencyFilter)) {
            agencyFilterList.add(agencyFilter);
        } else if (this.scopeType == ScopeType.SELFORGDATA) {
            agencyFilterList = approvalSituationBo.getAgencyList(salaryid, this.getUserView().getUserName());
        } else if (this.scopeType == ScopeType.ALLORGDATA) {
            agencyFilterList = approvalSituationBo.getAgencyList(salaryid, "");
        }
        return agencyFilterList;
    }


    /**
     * 数据范围类型
     */
    public enum ScopeType {
        //取全部机构信息
        ALLORGDATA,
        //取所在机构信息
        SELFORGDATA,
        //取发起人是自己
        AUTHORISMYSELF,
        //取权限范围内
        INPURVIEW
    }


}
