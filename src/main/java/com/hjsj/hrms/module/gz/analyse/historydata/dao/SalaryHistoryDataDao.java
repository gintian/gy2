package com.hjsj.hrms.module.gz.analyse.historydata.dao;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import java.util.List;

/**
 * @Title SalaryHistoryDataDao
 * @Description 薪资历史数据数据库操作类
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/1/13
 * @Version 1.0.0
 */
public interface SalaryHistoryDataDao {

    /**
     * 获取薪资类别
     * @author wangbs
     * @param salaryIdList
     * @param pageIndex
     * @param pageSize
     * @return List<RecordVo>
     * @throws GeneralException 抛出异常
     * @date 2020/1/13 15:51
     */
    List<RecordVo> listSalaryTemplate(List salaryIdList, int pageIndex, int pageSize) throws GeneralException;

    /**
     * 删除历史薪资数据
     * @author wangbs
     * @param type
     * @param salaryId
     * @param startDate
     * @param endDate
     * @return RecordVo>
     * @throws GeneralException 抛出异常
     * @date 2020/1/13 15:52
     */
    void deleteSalaryHistoryData(String type,String startDate,String endDate,String salaryId,UserView userView) throws GeneralException;
    
    /**
     * 归档历史薪资数据
     * @param type
     * @param startDate
     * @param endDate
     * @param salaryId
     * @param userView
     * @throws GeneralException
     * @author sunml
     * 2020年1月19日
     */
    void archiveSalaryHistoryData(String type, String startDate, String endDate,String salaryId,UserView userView) throws GeneralException;
    
    /**
     * 还原历史薪资数据
     * @param type
     * @param startDate
     * @param endDate
     * @param salaryId
     * @param userView
     * @throws GeneralException
     * @author sunml
     * 2020年1月19日
     */
    void revertSalaryHistoryData(String type,String startDate,String endDate,String salaryId,UserView userView) throws GeneralException;
    
    /**
     * 删除个税明细表
     * @param type
     * @param startDate
     * @param endDate
     * @param salaryId
     * @param userView
     * @throws GeneralException
     * @author sunml
     * 2020年1月19日
     */
    void deleteTaxData(String type,String startDate,String endDate,String salaryId,UserView userView) throws GeneralException;
    
    /**
     * 归档个税明细表
     * @param tableName
     * @param type
     * @param startDate
     * @param endDate
     * @param salaryId
     * @param userView
     * @param flag 1:历史薪资 2：个税明细
     * @throws GeneralException
     * @author sunml
     * 2020年1月19日
     */
    void archiveTaxData(String tableName,String type,String startDate,String endDate,String salaryId,UserView userView,int flag) throws GeneralException;
    
    /**
     * 还原个税明 表
     * @param tableName
     * @param type
     * @param startDate
     * @param endDate
     * @param salaryId
     * @param userView
     * @param flag 1：历史薪资 2：个税明细
     * @throws GeneralException
     * @author sunml
     * 2020年1月19日
     */
    void revertTaxData(String tableName,String type,String startDate,String endDate,String salaryId,UserView userView,int flag) throws GeneralException;
    
    /**
     * 获取薪资类别定义项
     * @param salaryId
     * @param gditem  排除系统项
     * @return
     * @author sunml
     * 2020年1月19日
     */
    String getSalaryItem(String salaryId,String gditem) throws GeneralException;
    
    /**
     * 是否存在add_flag指标项
     * @return
     */
    boolean isHaveAdd_flag() throws GeneralException;
   
    /**
     * 查询是否存在重复数据
     * @param type
     * @param startDate
     * @param endDate
     * @param salaryId
     * @param userView
     * @param sql
     * @return
     * @author sunml
     * 2020年1月19日
     */
    boolean repeatData(String type,String startDate,String endDate,String salaryId,UserView userView,StringBuffer sql) throws GeneralException;
      
    /**
     * 获得拼接sql语句的where条件
     * @param type
     * @param startDate
     * @param endDate
     * @param userView
     * @param flag 1:历史数据归档  2：个税明细归档
     * @return
     */
    String getWhereSQL(String type,String startDate,String endDate,String salaryId,UserView userView,int flag) throws GeneralException;
    
    /**
     * 获取薪资类别分页数据
     * @author liuyd
     * @return DynaBean
     * @throws GeneralException 抛出异常
     * @date 2020/1/13 15:52
     */
    List<DynaBean> getSwitchSalaryTemplateList(StringBuffer sql ,List<String> sqlList) throws GeneralException;


}
