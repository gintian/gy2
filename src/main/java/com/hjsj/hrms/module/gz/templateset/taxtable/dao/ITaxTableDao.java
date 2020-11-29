package com.hjsj.hrms.module.gz.templateset.taxtable.dao;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.DynaBean;

import java.util.List;

/**
 * @Description: 税率表数据库操作接口类
 * @Author manjg
 * @Date 2019/12/3 16:06
 * @Version V1.0
 **/
public interface ITaxTableDao {

    /**
     * 获取全部税率方案集合
     * @return 税率方案集合
     * @throws GeneralException
     */
    List<DynaBean> listTaxTables() throws GeneralException;

    /**
     * 获取指定税率表方案集合信息(导出税率表)
     * @param ids 税率表id
     * @return 返回指定税率表方案集合
     * @throws GeneralException
     */
    List<DynaBean> listTaxTables(String ids) throws GeneralException;

    /**
     * 批量新增税率方案
     * @param voList 税率方案集合
     * @throws GeneralException
     */
    void insertTaxTable(List<RecordVo> voList) throws GeneralException;

    /**
     * 批量更新税率表方案
     * @param voList 税率方案集合
     * @throws GeneralException
     */
    void updateTaxTable(List<RecordVo> voList) throws GeneralException;

    /**
     * 批量删除税率表方案
     * @param voList 税率方案集合
     * @throws GeneralException
     */
    void deleteTaxTable(List<RecordVo> voList) throws GeneralException;

    /**
     * 获取指定税率方案明细集合
     * @param ids 税率方案明细id
     * @return 税率方案明细集合
     * @throws GeneralException
     */
    List<DynaBean> listTaxTableDetails(String ids) throws GeneralException;

    /**
     * 批量新增税率方案明细集合
     * @param voList 税率方案明细集合
     * @throws GeneralException
     */
    void insertTaxTableDetail(List<RecordVo> voList) throws GeneralException;

    /**
     * 批量更新税率方案明细集合
     * @param voList 税率方案明细集合
     * @throws GeneralException
     */
    void updateTaxTableDetail(List<RecordVo> voList) throws GeneralException;

    /**
     * 批量删除税率方案明细集合
     * @param voList 税率方案明细集合
     * @throws GeneralException
     */
    void deleteTaxTableDetail(List<RecordVo> voList) throws GeneralException;

    /**
     * 查询税率表方案是否在计算公式中使用
     * @param ids
     * @return [{
     *              standid:xxx,//税率表id
     *              description:'xxx',//税率表名称
     *              salaryid:xxx,//薪资类别id
     *              cname:'xxx',//薪资类别名称
     *              hzname:'xxx',//指标描述
     *              itemname:'xxx'//指标id
     *          },{...}...]
     * @throws GeneralException
     */
    List<DynaBean> isHaveTaxTableTosalaryformula(List ids) throws GeneralException;

    /**
     * 获取计税方式代码项集合
     * @return 计税方式代码项集合
     * @throws GeneralException
     */
    List<DynaBean> listTaxModeCodeItem() throws GeneralException;

    /**
     * 获取指定税率表下税率明细表最大ID值
     * @param tableName 表名称
     * @return 最大ID值
     * @throws GeneralException
     */
    String getMaxTaxitemId(String tableName) throws GeneralException;

    /**
     * 根据表名获取税率表或明细表最大ID值
     * @return
     * @throws GeneralException
     */
    String getMaxId(String tableName) throws GeneralException;
}
