package com.hjsj.hrms.module.kq.card.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 打卡数据页面接口类
 * 
 * @Title: KqCardDataService.java
 * @Description: 打卡数据页面提供的接口
 * @Company: hjsj
 * @Create time: 2019年8月20日 下午4:44:10
 * @author chenxg
 * @version 7.5
 */
public interface KqCardDataService {
	/**
	 * 生成页面表
	 * 
	 * @return
	 * @throws GeneralException 
	 */
	String getTableConfig() throws GeneralException;

	/**
	 * 获取页面显示的指标用于生成快速查询功能
	 * 
	 * @return
	 */
	String getFieldsArray();

	/**
	 * 切换日期查询
	 * 
	 * @param param
	 *            日期参数：{sDate:开始时间，eDate：结束时间}
	 */
	void searchCardData(JSONObject param);

	/**
	 * 删除数据
	 * 
	 * @param param
	 *            要删除数据的参数：{nbase:人员库，a0100:人员编号，cardtime:打卡时间}
	 */
	void deleteCardData(JSONArray param);

	/**
	 * 导出数据
	 * 
	 * @param param
	 *            要导出的数据：{nbase:人员库，a0100:人员编号，cardtime:打卡时间}
	 */
	String exportCardData(JSONArray param);
	/**
	 * 导出模板
	 * 
	 * @param importType
	 *            模板类型 =1：单列；=2多列
	 */
	String exportCardTemplate(String importType);
	/**
	 * 导入模板
	 * 
	 * @param param
	 *            要导出的数据：{path:模板路径，filename:文件名称}
	 */
	void importCardTemplate(JSONObject param);
	/**
	 * 获取导入数据的异常信息
	 * @return
	 */
	String getErrorMsg();
	/**
	 * 导入的数据保存到数据库
	 * @return
	 */
	int saveCardData();
}
