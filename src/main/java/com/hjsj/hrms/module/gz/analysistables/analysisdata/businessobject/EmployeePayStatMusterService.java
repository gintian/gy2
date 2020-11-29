package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Map;

public interface EmployeePayStatMusterService {
    /**
     * 页面数据列
     * haosl
     *
     * @return
     * @param headList
     * @param nbases    已选人员库 、分隔
     *      列头list
     */
    ArrayList<ColumnsInfo> getColumnList(ArrayList<LazyDynaBean> headList,String nbases) throws GeneralException;
    /**
     * 得到表头列表
     *
     * @param rsdtlid
     *      项目统计表编号
     * @return
     */
    ArrayList<LazyDynaBean> getTableHeadlist(String rsdtlid) throws GeneralException;

    /**
     * 因方法需要的参数太多，故放入paramMap 中存放参数
     * @param paramMap 中有以下参数必传
     * nbases 人员库串 ','分隔
     * salaryids 薪资类别串 ','分隔
     * rsdtlid  项目统计表编号
     * headList 显示列
     * tatflag 统计方式 =1 按年统计，=2按区间统计
     * year  年份
     * starttime 开始时间
     * endtime 结束时间
     * limit 分页信息：每页显示多少条
     * page 分页信息：当前页
     * isShowTotal 是否显示合计行
     * condSql 查询条件
     * scope 是否包含过程中数据
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     */
    ArrayList<LazyDynaBean> getDataList(Map paramMap, int total_count) throws GeneralException;

    /**
     * 因方法需要的参数太多，故放入paramMap 中存放参数
     * @param paramMap 中有以下参数必传
     * nbases 人员库串 ','分隔
     * salaryids 薪资类别串 ','分隔
     * rsdtlid  项目统计表编号
     * headList 显示列
     * tatflag 统计方式 =1 按年统计，=2按区间统计
     * year  年份
     * starttime 开始时间
     * endtime 结束时间
     * isShowTotal 是否显示合计行
     * condSql 查询条件
     * scope 是否包含过程中数据
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     */
    int getDataCount(Map paramMap) throws GeneralException;
}
