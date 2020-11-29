package com.hjsj.hrms.module.gz.mytax.businessobject;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.util.List;
import java.util.Map;

/**
 * 我的个税业务接口类
 * @author wangb
 * @category hjsj 2019-03-14
 * @version 1.0
 *
 */
public interface MyTaxService {

	/**
	 * 获取配置的个税指标项 集合
     * 扩展此方法，增加被移除的指标项信息
     * 方便前台进行提示
     * 配置指标项的key itemList
     * 被移除的指标项key removeitemList
	 * @param userView 用户名
	 * @return 配置指标项list与被移除的指标项list
	 * @throws GeneralException 查询过程中出现异常信息
	 * @author wangb 2019-03-14
	 */
	Map getMyTaxItemList(UserView userView) throws GeneralException;
	
	/**
	 * 获取个人所得税扣缴明细表 指标集合
	 * @param userView
	 * @return
	 * @throws GeneralException
	 * @author wangb 2019-03-14
	 */
	List listGzTaxMxField(UserView userView) throws GeneralException;
	
	/**
	 * 保存个税指标项
	 * @param list 数据集合
	 * @param userView
	 * @return
	 * @throws GeneralException
	 * @author wangb 2019-03-14
	 */
	String saveMyTaxItem(List list,UserView userView) throws GeneralException;
	
	/**
	 * 删除指标项 
	 * @param ids 指标id xxx,xxx
	 * @param userView
	 * @return
	 * @throws GeneralException
	 * @author wangb 2019-03-14
	 */
	String deleteMyTaxItem(String ids,UserView userView) throws GeneralException;
	
	/**
	 * 初始化我的个税相关数据
	 * @param userView
	 * @return
	 * @throws GeneralException
	 */
	Map initMyTaxData(UserView userView) throws GeneralException;
	
	/**
	 * 获取某年个税数据
	 * @param year
	 * @param userView
	 * @return
	 * @throws GeneralException
	 */
	List getMyTaxData(String year,UserView userView) throws GeneralException;

	/**
	 * 校验计算公式
	 * @param 用户信息
	 * @param c_expr 公式
	 * @param itemType 指标类型
	 * @return
	 */
	String checkFormula(UserView userView,String c_expr,String itemType)  throws GeneralException;
	/**
	 * 获取一年中所有月的所得税
	 * @param userView 当前登录用户
	 * @param year 索要查询的年
	 * @return 一年所有月的所得税
	 */
	Map<String,String> getMonthSdsOfYear(UserView userView,String year) throws GeneralException;
}
