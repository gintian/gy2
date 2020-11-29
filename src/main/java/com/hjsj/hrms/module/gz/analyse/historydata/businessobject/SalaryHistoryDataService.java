package com.hjsj.hrms.module.gz.analyse.historydata.businessobject;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Title SalaryHistoryDataService
 * @Description 薪资历史数据业务类
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/1/13
 * @Version 1.0.0
 */
public interface SalaryHistoryDataService {
	
    /**
     * 获取tablebuilder配置
     * @author wangbs
     * @param type
     * @param salaryId
     * @param appdate
     * @param querySql
     * @return String
     * @date 2020/1/13 15:43
     */
    String getSalaryHistoryTableConfig(String type,String salaryId,String appdate,String querySql) throws GeneralException;

    /**
     * 获取薪资类别数据
     * @author wangbs
     * @param name
     * @throws GeneralException 抛出异常
     * @date 2020/1/13 15:48
     */
    void listSalaryTemplateData(String name) throws GeneralException;
    
    /**
     * 归档历史薪资数据
     * @param type 0：全部 1：时间范围
     * @param startDate
     * @param endDate
     * @param salaryId
     * @throws GeneralException
     * @author sunml
     * 2020年1月19日
     */
    void archiveSalaryHistoryData(String type,String startDate,String endDate,String salaryId) throws GeneralException;

    /**
     * 还原历史薪资数据
     * @param type 0：全部 1：时间范围
     * @param startDate
     * @param endDate
     * @param salaryId
     * @return
     * @throws GeneralException
     * @author sunml
     * 2020年1月19日
     */
    String revertSalaryHistoryData(String type,String startDate,String endDate,String salaryId) throws GeneralException;

    /**
     * 删除历史薪资数据
     * @param type 0：全部 1：时间范围
     * @param startDate
     * @param endDate
     * @param salaryId
     * @throws GeneralException
     * @author sunml
     * 2020年1月19日
     */
    void deleteSalaryHistoryData(String type,String startDate,String endDate,String salaryId) throws GeneralException;
    
    /**
     * 同步数据表结构
     * @throws GeneralException 抛出异常
     */
    void syncSalaryarchiveStrut() throws GeneralException;
    
    /**
     * 同步个税归档表
     * @throws GeneralException
     */
    void syncSalaryTaxArchiveStrut() throws GeneralException;
    
    /**
     * 归档|还原个税明细表
     * @param type
     * @param startDate
     * @param endDate
     * @param salaryId
     * @param userView
     * @param opt 1:归档  2：还原
     */
    void pigeonholeTaxData(String type,String startDate,String endDate,String salaryId,UserView userView,int opt) throws GeneralException;
    
    /**
     * 还原数据判断结构是否改变
     * @return
     * @throws GeneralException
     */
    boolean strutIsChange() throws GeneralException;

    /**
     * 获取薪资类别表格
     * @author liuyd
     * @param type
     * @throws GeneralException 抛出异常
     * @date 2020/1/15 11:54
     */
    String getHistoryTemplateConfig(String type, List<String> valuesList) throws GeneralException;
    /**
     * 初始化获得薪资账套
     * @author sheny
     * @return salaryId 薪资账套id
     * @date 2020/1/15 15:35
     */
    String getSalaryId() throws GeneralException;
    /**
     * 初始化获得薪资发放日期
     * @author sheny
     * @param salaryId 薪资账套Id
     * @param transType 区分是否归档
     * @return appdate
     * @date 2020/1/15 15:35
     */
    String getAppdate(String salaryId,String transType) throws GeneralException;
    /**
     * 初始化获得薪资归属时间
     * @author sheny
     * @param salaryId 薪资账套Id
     * @param appDate 发放日期
     * @param transType 归档  未归档
     * @return count 薪资归属次数
     * @date 2020/1/15 15:35
     */
    Map getCount(String salaryId, String appDate, String transType)throws GeneralException;
    /**
     * 获取表单列
     * @author sheny
     * @param salaryId 薪资账套Id
     * @return columnsFieldList 表单列指标
     * @date 2020/1/15 15:35
     */
    List getColumnsFieldList(String salaryId) throws GeneralException;
    /**
     * 搜索时获取数据sql
     * @author sheny
     * @param columnsFieldList 表单列指标
     * @param salaryId 薪资账套Id
     * @param type history 未归档 achieve 归档
     * @param appdate 薪资归属日期
     * @param count  薪资归属次数
     * @param valuesList 搜索内容
     * @param searchType 1为输入查询，2为方案查询
     * @param exp 方案查询公式
     * @param cond 方案查询数据
     * @return sqldata 表单数据sql
     * @date 2020/1/15 15:35
     */
    String getSqldata(List columnsFieldList,String type,String salaryId,String appdate,String count,
                      ArrayList<String> valuesList,String searchType,String exp,String cond);
    /**
     * 获得薪资账套指标集
     * @author sheny
     * @param salaryId 薪资账套Id
     * @return reportList 薪资账套指标集
     * @date 2020/1/15 15:35
     */
    ArrayList<LazyDynaBean> getReportList(String salaryId) throws GeneralException;
    /**
     * 获得薪资账套集
     * @author sheny
     * @return salaryTypeList 薪资账套集
     * @date 2020/1/15 15:35
     */
    List getSalaryType() throws GeneralException;

    /**
     * 获取切换日期组件的数据
     * @param salaryId
     * @param appdate
     * @param transType
     * @return
     */
    ArrayList getDateList(String salaryId, String appdate, String transType) throws GeneralException;
}
