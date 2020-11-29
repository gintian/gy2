package com.hjsj.hrms.module.kq.config.shiftgroup.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public interface ShiftService {
	/**
	 * 获取快速查询的查询条件
	 * 
	 * @param type
	 *            类型
	 * @param valuesList
	 *            查询的条件
	 * @param exp
	 * @param cond
	 */
	void filterSql(String type, ArrayList<String> valuesList, String exp, String cond);

	/**
	 * 获取排序表格的列的json
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            第几周
	 * @param dataType
	 *            调用的页面参数 =shiftData：排班页面;=shiftCheck排班审查
	 * @return
	 */
	String getShiftcolumnsJson(int year, int month, int weekIndex, String dataType);

	/**
	 * 获取排序表格的列
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            第几周
	 * @param dataType
	 *            调用的页面参数 =shiftData：排班页面;=shiftCheck排班审查
	 * @return
	 */
	String getShiftcolumns(int year, int month, int weekIndex, String dataType);

	/**
	 * 获取排班数据
	 * 
	 * @param paramMap
	 *            参数集合：{year:年,month:月,weekIndex:第几周,imit:每页的条数,page:第几页,
	 *            groupId:班组id,cloumns:列,dataSql:查询数据的sql, dataType
	 *            调用的页面参数=shiftData：排班页面;=shiftCheck排班审查}
	 * @return
	 */
	ArrayList<LazyDynaBean> shiftDataList(HashMap<String, Object> paramMap);

	/**
	 * 排班数据总数
	 * 
	 * @return
	 */
	int getTotalCount();

	/**
	 * 获取当前方案编号
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            第几周
	 * @param groupId
	 *            班组id
	 * @return
	 */
	String getSchemeId(String year, String month, String weekIndex, String groupId);

	/**
	 * 获取当前月份的周数
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return
	 */
	ArrayList<HashMap<String, String>> weekList(int year, int month);

	/**
	 * 获取快速查询支持的列
	 * 
	 * @return
	 */
	String getFields();

	/**
	 * 获取有效的班次信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param classIds
	 *            已选的班次
	 * @param shiftInfoFlag
	 *            编辑班次的功能标识：="":表格直接双击单元格设置班次；=changgeShift：更换班次；=addShifts：新增班次
	 * @return
	 */
	ArrayList<HashMap<String, String>> getShiftInfoList(String groupId, String classIds, String shiftInfoFlag);

	/**
	 * 保存排班信息
	 * 
	 * @param date
	 *            排班日期
	 * @param record
	 *            页面中被修改班次的人员对应的整行数据
	 * @throws GeneralException
	 */
	void saveShiftInfoS(String date, JSONObject record) throws GeneralException;

	/**
	 * 保存排班方案人员对应表中的信息
	 * 
	 * @param schemeId
	 *            排班方案编号
	 * @param itemId
	 *            修改的指标id
	 * @param record
	 *            页面中被修改数据的人员对应的整行数据
	 * @throws GeneralException
	 */
	void saveSchemeEmp(String schemeId, String itemId, JSONObject record) throws GeneralException;

	/**
	 * 获取当前班组排班的年月
	 * 
	 * @param GroupId
	 *            班组编号
	 * @return
	 * @throws GeneralException
	 */
	String getShiftDate(String GroupId) throws GeneralException;

	/**
	 * 保存拖拽人员的顺序
	 * 
	 * @param schemeId
	 *            方案编号
	 * @param guidkey
	 *            被拖拽的人员的guidke
	 * @param modelGuidKey
	 *            拖拽到的位置的人员的guidkey
	 * @param dropPosition
	 *            拖拽到目标行的位置，=before：目标行上面；=after：目标行下面
	 * @throws GeneralException
	 */
	void saveDropSort(String schemeId, String guidkey, String modelGuidKey, String dropPosition)
	        throws GeneralException;

	/**
	 * 获取班组人员维护表格对象 getShiftGroupEmpTableConfig
	 * 
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月28日 下午1:53:02
	 * @author linbz
	 */
	String getShiftGroupEmpTableConfig(JSONObject jsonObj) throws GeneralException;

	/**
	 * 添加班组人员 addShiftGroupEmp
	 * 
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月30日 下午4:15:53
	 * @author linbz
	 */
	String addShiftGroupEmp(JSONObject jsonObj) throws GeneralException;

	/**
	 * 取消班组人员 deleteShiftGroupEmp
	 * 
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月30日 下午5:11:23
	 * @author linbz
	 */
	String deleteShiftGroupEmp(JSONObject jsonObj) throws GeneralException;

	/**
	 * 
	 * changeShiftGroupEmp
	 * 
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年12月1日 下午6:48:15
	 * @author linbz
	 */
	String changeShiftGroupEmp(JSONObject jsonObj) throws GeneralException;

	/**
	 * 获取调换人员的信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemId
	 *            方案编号
	 * @param guidKey
	 *            guidkey
	 * @param filterValue
	 *            查询条件
	 * @return
	 * @throws GeneralException
	 */
	ArrayList<HashMap<String, String>> searchPsrsonInfo(String groupId, String schemId, String guidKey,
	        String filterValue) throws GeneralException;

	/**
	 * 保存人员调换的数据
	 * 
	 * @param paramMap
	 *            参数对象:oldGuidKey:被调换人的guidkey; newGuidKey: 调换人的guidkey; year:年份
	 *            month:月份 weekIndex:第几周;schemeId:方案编号;
	 * @throws GeneralException
	 */
	void savePersonChange(HashMap<String, String> paramMap) throws GeneralException;

	/**
	 * 获取页面显示数据的日期范围
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            月份中的第几周
	 * @return
	 */
	String getWeekScope(String year, String month, String weekIndex);

	/**
	 * 获取页面显示数据的日期范围
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            月份中的第几周
	 * @return
	 */
	String getLastWeekScope(String year, String month, String weekIndex);

	/**
	 * 删除排班信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            方案编号
	 * @param weekScope
	 *            时间范围
	 * @throws GeneralException
	 */
	void deleteShiftInfo(String groupId, String schemeId, String weekScope) throws GeneralException;

	/**
	 * 查询排班方案的备注信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            排班方案编号
	 * @return
	 */
	HashMap<String, String> getShiftRemark(String groupId, String schemeId);

	/**
	 * 保存排班方案的备注信息
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            排班方案编号
	 * @param shiftComment
	 *            排班备注
	 * @param empComment
	 *            人员调换备注
	 * @param trainComment
	 *            培训备注
	 * @return
	 */
	void saveShiftRemark(String groupId, String schemeId, String shiftComment, String empComment, String trainComment);

	/**
	 * 复制上周排班的数据
	 * 
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            方案编号
	 * @param weekScope
	 *            本周时间范围
	 * @param lastWeekScope
	 *            上周时间范围
	 * @param copyType
	 *            复制方式
	 */
	void copyShiftInfo(String groupId, String schemeId, String weekScope, String lastWeekScope, String copyType);

	/**
	 * 自动排班
	 * 
	 * @param groupId
	 *            班组编号
	 * @param fromDate
	 *            开始时间
	 * @param toDate
	 *            结束时间
	 * @param shfitType
	 *            排班类型
	 */
	void autoShift(String groupId, String fromDate, String toDate, String shfitType);

	/**
	 * 发布班次方案
	 * 
	 * @param paramMap
	 *            参数集合：{ groupId：班组编号； schemeId：方案编号； state：具体操作=push：发布；=edit：编辑 }
	 */
	void pushShiftScheme(HashMap<String, String> paramMap);

	/**
	 * 粘帖排班信息保存
	 * 
	 * @param records
	 *            粘帖的排班数据
	 * @param groupId
	 *            班组编号
	 * @param schemeId
	 *            方案编号
	 */
	void copyShifts(JSONArray records, String groupId, String schemeId);

	/**
	 * 获取对应排班方案的状态
	 * 
	 * @return
	 */
	String getPushScheme();

	/**
	 * 获取班组的名称
	 * 
	 * @return
	 */
	String getGroupName();

	/**
	 * 获取统计列的值，包括工时列， 此方法只能在单元格排班、删除班次、复制班次、自动排班后获取统计列的值，否则只能返回空对象
	 * 
	 * @return
	 */
	HashMap<String, HashMap<String, String>> getCountDataMap();

	/**
	 * 获取页面显示的所有的列，格式：[{itemid:"b0110",itemDesc:"单位"},{itemid:"e0122",itemDesc:"部门"}...]
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param weekIndex
	 *            周
	 * @param dataType
	 *            调用的页面参数 =shiftData：排班页面;=shiftCheck排班审查
	 * @return
	 */
	ArrayList<HashMap<String, String>> getColumnList(int year, int month, int weekIndex, String dataType);

	/**
	 * 获取班组人员维护选择日期所需数据 getGroupChangeEmpData
	 * 
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2019年1月4日 下午3:17:31
	 * @author linbz
	 */
	HashMap getGroupChangeEmpData(JSONObject jsonObj) throws GeneralException;

	/**
	 * 获取页面显示记录的数量
	 * 
	 * @param dataType
	 *            调用的页面参数 =shiftData：排班页面;=shiftCheck排班审查
	 */
	int getPageSize(String dataType);

	/**
	 * 更改栏目设置保存后的submoduleid
	 * 
	 * @param dataType
	 *            调用的页面参数 =shiftData：排班页面;=shiftCheck排班审查
	 */
	void changeSubmoudleId(String dataType);
	/**
	 * 获取权限内的功能按钮
	 * @return
	 */
	String getButtons();
}
