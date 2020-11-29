package com.hjsj.hrms.module.kq.config.period.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

public interface PeriodService {
	/**
	 *   获取考勤期间list
	 *  （方法名以list开头 后接驼峰格式表名）
     *
     * @param sqlWhere      数据范围
     * @param parameterList 参数 (不需要写where 以and开头)
     * @param sqlSort       排序sql(不需要写order by 仅写字段即可，后可跟desc)
     * @return ArrayList<LazyDynaBean> (LazyDynaBean内为该表查询结果的全部字段，查询结果 bean 中的key为全小写字段名)
     * @throws GeneralException 接口方法必须抛出异常,异常信息需自己定义
     * @author haosl
     * @date 11:29 2018/10/31
     */
	ArrayList<LazyDynaBean> listKq_duration(String sqlWhere , ArrayList parameterList, String sqlSort) throws GeneralException;
	
	/**
	 *  获得表格控件json串
	 * @return
	 */
	public String getShiftsTableConfig();
	
	/**
	 * 删除考勤期间（已创建考勤数据的不允许删除）
	 * @param kq_year 考勤年度
	 * @param kq_durations 考勤期间列表
	 */
	public String deleteDurations(String kq_year, List<String> kq_durations);
	
	/**
	 *kq_year中 校验是否有考勤期间
	 * @param kq_year
	 * @return
	 */
	public boolean checkHasPrivPeriod(String kq_year) throws Exception;
	/**
	 * 	新建考勤期间
	 * @param jsonObj 前台传递的json参数对象
	 * 		jsonObj 属性有：<br/>
	 * 			kq_year ： 考勤年度<br/>
	 * 			model ：新建期间的方式 =1通上一年度；=2 按自然月；<br/>
	 * 			model=3时需要传参 start_month（其实月份）start_day（其实日期）privios_month （="1" 代表起始日期自上月起）
	 * @return
	 */
	public String crteatePeriod(JSONObject jsonObj);
	/**
	 *  获得考勤年度
	 * @return
	 * @throws GeneralException
	 */
	public List<LazyDynaBean> getYearList() throws GeneralException;
	
	/**
	 * 表格控件查询sql
	 * @return
	 */
	public String getTableSql();
	
}
