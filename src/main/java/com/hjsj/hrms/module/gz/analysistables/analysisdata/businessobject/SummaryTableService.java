package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

public interface SummaryTableService {

    /**
     * 获得表格列头
     *
     * @return
     */
    ArrayList<ColumnsInfo> getColumnList() throws GeneralException;

    /**
     * 工资项目分类统计台帐:分类指标列表
     *
     * @param salaryid
     * @return
     */
    List<LazyDynaBean> getSalaryItem(String salaryid);

    /**
     * 获得页面数据
     * @param salaryids
     *           薪资账套id 多个,分隔
     * @param year
     *          年度
     * @param itemid
     *          薪资项目id
     * @param nbases
     *      人员库
     * @param scope
     *   =1 包含过行中数据
     * @param querySql
     * @param
     *      limit 每页显示条数
     * @param
     *      page 当前页
     * @return
     */
    ArrayList getDataList(String salaryids, String year,
                          String itemid, String nbases, String scope, String querySql, String sortSql, int limit, int page, int totalCount);

    /**
     * 获得总记录数
     * @param salaryids
     * @param year
     * @param nbases
     * @param scope
     * @param condSql
     * @return
     */
    int getDataCount(String salaryids, String year,String itemid, String nbases, String scope, String condSql)throws GeneralException;
}
