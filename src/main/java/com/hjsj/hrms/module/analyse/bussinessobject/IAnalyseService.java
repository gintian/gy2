package com.hjsj.hrms.module.analyse.bussinessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.List;
import java.util.Map;

/**
 * @Title IAnalyseService
 * @Description 工资分析业务接口
 * @Company hjsj
 * @Author wangbs、caoqy
 * @Date 2019/12/19
 * @Version 1.0.0
 */
public interface IAnalyseService {
    /**
     * 功能描述: 获取工资分析首界面加载数据
     * @author: caoqy
     * @param menuid:
     * @return: java.util.List
     * @date: 2019-12-19 15:47
     */
    List getAnalyseMainData(String menuid) throws GeneralException;

    /**
     * 获取平均统计数据
     * @author wangbs
     * @param viewTable 视图表
     * @param b0110 查询机构
     * @param year 所属年份
     * @param unit 单位（万元、元 等等）
     * @return <Map>
     * @throws GeneralException 抛出异常
     * @date 2019/12/19 13:28
     */
    Map getPingJunViewData(String viewTable, String b0110, String year,String unit) throws GeneralException;

    /**
     * 获取累计统计数据
     * @author wangbs
     * @param viewTable 视图表
     * @param b0110 查询机构
     * @param year 所属年份
     * @param unit 单位（万元、元 等等）
     * @return <Map>
     * @throws GeneralException 抛出异常
     * @date 2019/12/19 13:28
     */
    Map getLeiJiViewData(String viewTable, String b0110, String year, String unit) throws GeneralException;

    /**
     * 获取多项目多分类统计数据
     * @author wangbs
     * @param viewTable 视图表
     * @param b0110 查询机构
     * @param year 所属年份
     * @param unit 单位（万元、元 等等）
     * @return <Map>
     * @throws GeneralException 抛出异常
     * @date 2019/12/19 13:31
     */
    Map getMoreItemAndTypeViewData(String viewTable, String b0110, String year, String unit) throws GeneralException;

    /**
     * 功能描述: 获取占比统计数据
     *
     * @param viewTable : 视图表名
     * @param items :     配置项
     * @param b0110 :     单位id<加密>
     * @param year :      年份
     * @param unit :      单位（万元、元 等等）
     * @author: caoqy
     * @return: java.util.List<java.util.Map>
     * @date: 2019-12-24 10:17
     */
    Map<String, Object> getZhanBiViewData(String viewTable, String items, String b0110, String year, String unit) throws GeneralException;
    /**
     * 功能描述: 获取占比统计数据
     *
     * @param viewTable : 视图表名
     * @param items :     配置项
     * @param b0110 :     单位id<加密>
     * @param year :      年份
     * @param unit :      单位（万元、元 等等）
     * @author: caoqy
     * @return: java.util.List<java.util.Map>
     * @date: 2019-12-25 16:05:09
     */
    Map<String, Object> getTongBiViewData(String viewTable, String items, String b0110, String year, String unit) throws GeneralException;
}
