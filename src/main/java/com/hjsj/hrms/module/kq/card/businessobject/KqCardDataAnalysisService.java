package com.hjsj.hrms.module.kq.card.businessobject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface KqCardDataAnalysisService {
	/**
	 * 生成页面表
	 * 
	 * @return
	 */
	String getTableConfig();

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
	 * 导出数据
	 * 
	 * @param param
	 *            要导出的数据：{nbase:人员库，a0100:人员编号，cardtime:打卡时间}
	 */
	String exportCardData(JSONArray param);
	
	/**
	 * 数据分析
	 * 
	 * @param param
	 *            日期参数：{sDate:开始时间，eDate：结束时间}
	 */
	void dataAnalys(JSONObject param);
	/**
	 * 设置日期参数
	 * @param dateParam
	 */
	void setDateParam(JSONObject dateParam);
}
